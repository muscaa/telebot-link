package com.github.muscaa.telebot.link;

import androidx.lifecycle.MutableLiveData;

import com.github.muscaa.telebot.link.network.StunClient;

public class TelebotLink {

    public static final TelebotLink INSTANCE = new TelebotLink();

    public final MutableLiveData<String> mLogs = new MutableLiveData<>("");

    private final StunClient stunClient = new StunClient();

    public void connect(String ip, int port, String name) {
        print("Connecting to " + ip + ":" + port + " as " + name + "...");

        stunClient.setName(name);
        stunClient.connect(ip, port);
    }

    public void disconnect() {
        stunClient.disconnect();
    }

    public void print(Object o) {
        mLogs.postValue(o + "\n" + mLogs.getValue());

        System.out.println(o);
    }
}
