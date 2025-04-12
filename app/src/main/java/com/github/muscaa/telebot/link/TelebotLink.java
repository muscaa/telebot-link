package com.github.muscaa.telebot.link;

import androidx.lifecycle.MutableLiveData;

public class TelebotLink {

    public static final TelebotLink INSTANCE = new TelebotLink();

    public final MutableLiveData<String> mLogs = new MutableLiveData<>("");

    public void print(Object o) {
        mLogs.setValue(o + "\n" + mLogs.getValue());

        System.out.println(o);
    }
}
