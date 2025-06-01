package com.github.muscaa.telebot.link.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import fluff.bin.IBinaryInput;

public interface TcpClientListener<C extends TcpClient> {

    void onConnected(C client);

    void onReceived(C client, IBinaryInput in) throws IOException;

    void onDisconnected(C client);
}
