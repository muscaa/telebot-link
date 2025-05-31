package com.github.muscaa.telebot.link.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import fluff.bin.IBinaryInput;

public interface TcpClientListener<V extends TcpClient> {

    void onConnected(V client);

    void onReceived(V client, IBinaryInput in) throws IOException;

    void onDisconnected(V client);
}
