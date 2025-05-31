package com.github.muscaa.telebot.link.network;

import com.github.muscaa.telebot.link.TelebotLink;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import fluff.bin.IBinaryInput;
import fluff.bin.IBinaryOutput;
import fluff.bin.stream.BinaryInputStream;
import fluff.bin.stream.BinaryOutputStream;
import fluff.functions.gen.obj.TVoidFunc1;

public class TcpClient<V extends TcpClient> {

    private final TcpClientListener<V> listener;

    private Socket socket;
    private Thread thread;
    private InputStream input;
    private OutputStream output;

    public TcpClient(TcpClientListener<V> listener) {
        this.listener = listener;
    }

    public void connect(String ip, int port) {
        if (isConnected()) {
            disconnect();
        }

        thread = new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                input = socket.getInputStream();
                output = socket.getOutputStream();
            } catch (IOException e) {
                TelebotLink.INSTANCE.print(e.toString());
            }

            listener.onConnected((V) this);

            while (isConnected()) {
                try {
                    byte[] buf = new byte[1024];
                    int len = input.read(buf);

                    IBinaryInput in = new BinaryInputStream(new ByteArrayInputStream(buf, 0, len));
                    listener.onReceived((V) this, in);
                } catch (IOException e) {
                    TelebotLink.INSTANCE.print(e.toString());

                    disconnect();
                }
            }
        });
        thread.setName("Tcp Client Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void send(TVoidFunc1<IBinaryOutput, IOException> func) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        IBinaryOutput out = new BinaryOutputStream(data);

        try {
            func.invoke(out);

            send(data.toByteArray());
        } catch (IOException e) {
            TelebotLink.INSTANCE.print(e.toString());

            disconnect();
        }
    }

    public void send(byte[] data) {
        if (!isConnected()) {
            return;
        }

        try {
            output.write(data);
        } catch (IOException e) {
            TelebotLink.INSTANCE.print(e.toString());

            disconnect();
        }
    }

    public void disconnect() {
        if (!isConnected()) {
            return;
        }

        try {
            socket.close();

            listener.onDisconnected((V) this);
        } catch (IOException e) {
            TelebotLink.INSTANCE.print(e.toString());
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
