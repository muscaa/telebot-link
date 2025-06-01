package com.github.muscaa.telebot.link.network;

import com.github.muscaa.telebot.link.TelebotLink;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fluff.bin.IBinaryInput;
import fluff.bin.IBinaryOutput;
import fluff.bin.stream.BinaryInputStream;
import fluff.bin.stream.BinaryOutputStream;
import fluff.functions.gen.obj.TVoidFunc1;

public class TcpClient<C extends TcpClient, L extends TcpClientListener<C>> {

    protected static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    protected final L listener;

    protected Socket socket;
    protected Thread thread;
    protected InputStream input;
    protected OutputStream output;

    public TcpClient(L listener) {
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

            listener.onConnected((C) this);

            while (isConnected()) {
                try {
                    byte[] buf = new byte[1024];
                    int len = input.read(buf);

                    IBinaryInput in = new BinaryInputStream(new ByteArrayInputStream(buf, 0, len));
                    listener.onReceived((C) this, in);
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
        if (!isConnected()) {
            return;
        }

        EXECUTOR.execute(() -> {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            IBinaryOutput out = new BinaryOutputStream(data);

            try {
                func.invoke(out);

                output.write(data.toByteArray());
            } catch (IOException e) {
                TelebotLink.INSTANCE.print(e.toString());

                disconnect();
            }
        });
    }

    public void disconnect() {
        if (!isConnected()) {
            return;
        }

        EXECUTOR.execute(() -> {
            try {
                socket.close();

                listener.onDisconnected((C) this);
            } catch (IOException e) {
                TelebotLink.INSTANCE.print(e.toString());
            }
        });
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
