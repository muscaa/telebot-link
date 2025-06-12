package com.github.muscaa.telebot.link.network;

import com.github.muscaa.telebot.link.TelebotLink;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fluff.bin.Binary;
import fluff.bin.IBinaryInput;

public class StunClientListener<V extends StunClient> implements TcpClientListener<V> {

    public static final byte PACKET_S2C_LIST = 0x00;
    public static final byte PACKET_C2S_LOGIN = 0x01;
    public static final byte PACKET_C2S_LINK_REQUEST = 0x02;
    public static final byte PACKET_S2C_LINK_REQUEST = 0x03;
    public static final byte PACKET_C2S_LINK_RESPONSE = 0x04;
    public static final byte PACKET_S2C_LINK_RESPONSE = 0x05;

    @Override
    public void onConnected(StunClient client) {
        sendC2SLogin(client, client.getName());
    }

    @Override
    public void onReceived(StunClient client, IBinaryInput in) throws IOException {
        byte packetId = in.Byte();
        switch (packetId) {
            case PACKET_S2C_LIST:
                readS2CList(client, in);
                break;
            case PACKET_S2C_LINK_REQUEST:
                readS2CLinkRequest(client, in);
                break;
            case PACKET_S2C_LINK_RESPONSE:
                readS2CLinkResponse(client, in);
                break;
            default:
                TelebotLink.INSTANCE.print("Unknown packet ID: " + packetId);
                break;
        }
    }

    @Override
    public void onDisconnected(StunClient client) {}

    public void readS2CList(StunClient client, IBinaryInput in) throws IOException {
        List<String> list = new LinkedList<>();

        int size = in.Int();
        for (int i = 0; i < size; i++) {
            String name = in.LenString();

            list.add(name);
        }

        onList(client, list);
    }

    public void sendC2SLogin(StunClient client, String name) {
        client.send(out -> {
            out.Byte(PACKET_C2S_LOGIN);
            out.LenString(name);
        });
    }

    public void sendC2SLinkRequest(StunClient client, String name) {
        client.send(out -> {
            out.Byte(PACKET_C2S_LINK_REQUEST);
            out.LenString(name);
        });
    }

    public void readS2CLinkRequest(StunClient client, IBinaryInput in) throws IOException {
        String from = in.LenString();

        onLinkRequest(client, from);
    }

    public void sendC2SLinkResponse(StunClient client, String name, boolean accept) {
        client.send(out -> {
            out.Byte(PACKET_C2S_LINK_RESPONSE);
            out.LenString(name);
            out.Boolean(accept);
        });
    }

    public void readS2CLinkResponse(StunClient client, IBinaryInput in) throws IOException {
        String name = in.LenString();
        boolean accept = in.Boolean();

        if (accept) {
            String myip = in.LenString();
            int myport = in.Int();
            String ip = in.LenString();
            int port = in.Int();

            onLinkAccepted(client, name, myip, myport, ip, port);
        } else {
            String declineMessage = in.LenString();

            onLinkDeclined(client, name, declineMessage);
        }
    }

    public void onList(StunClient client, List<String> list) {
        TelebotLink.INSTANCE.mClients.postValue(list);
    }

    public void onLinkRequest(StunClient client, String from) {
        TelebotLink.INSTANCE.print("Accepted link request from " + from);
        sendC2SLinkResponse(client, from, true);
    }

    public void onLinkAccepted(StunClient client, String name, String myip, int myport, String ip, int port) {
        TelebotLink.INSTANCE.print("Link accepted: " + name + " @ " + ip + ":" + port + " (on " + myip + ":" + myport + ")");

        TelebotLink.INSTANCE.onLinkAccepted(name, myip, myport, ip, port);
    }

    public void onLinkDeclined(StunClient client, String name, String message) {
        TelebotLink.INSTANCE.print("Link declined: " + name + " - " + message);
    }
}
