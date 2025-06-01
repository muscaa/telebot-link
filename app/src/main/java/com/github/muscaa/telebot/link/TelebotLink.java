package com.github.muscaa.telebot.link;

import androidx.lifecycle.MutableLiveData;

import com.github.muscaa.telebot.link.network.StunClient;

import java.util.List;

public class TelebotLink {

    public static final TelebotLink INSTANCE = new TelebotLink();

    public final MutableLiveData<String> mLogs = new MutableLiveData<>("");
    public final MutableLiveData<List<String>> mClients = new MutableLiveData<>(List.of());

    private final StunClient stunClient = new StunClient();

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
}
