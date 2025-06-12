package com.github.muscaa.telebot.link;

import androidx.lifecycle.MutableLiveData;

import com.github.muscaa.telebot.link.network.StunClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class TelebotLink {

    public static final TelebotLink INSTANCE = new TelebotLink();

    public final MutableLiveData<String> mLogs = new MutableLiveData<>("");
    public final MutableLiveData<List<String>> mClients = new MutableLiveData<>(List.of());

    private final StunClient stunClient = new StunClient();

    private String name;
    private String myip;
    private int myport;
    private String ip;
    private int port;

    private DatagramSocket dsocket;
    private InetAddress address;

    public void connect(String ip, int port, String name) {
        print("Connecting to " + ip + ":" + port + " as " + name + "...");

        stunClient.setName(name);
        stunClient.connect(ip, port);
    }

    public void link(String name) {
        stunClient.link(name);
    }

    public void disconnect() {
        stunClient.disconnect();
    }

    public void print(Object o) {
        mLogs.postValue(o + "\n" + mLogs.getValue());

        System.out.println(o);
    }

    public void dsend(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        dsocket.send(packet);
    }

    public void onLinkAccepted(String name, String myip, int myport, String ip, int port) {
        this.name = name;
        this.myip = myip;
        this.myport = myport;
        this.ip = ip;
        this.port = port;

        Thread t = new Thread(() -> {
            try {
                dsocket= new DatagramSocket();
                address = InetAddress.getByName("192.168.0.100");
            } catch (Exception e) {
                print(e.toString());
            }
        });
        t.setDaemon(true);
        t.setName("Network Connection");
        t.start();
    }
}
