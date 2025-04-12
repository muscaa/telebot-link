package com.github.muscaa.telebot.link.ui.logs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogsViewModel() {
        mText = new MutableLiveData<>();
        StringBuilder sb = new StringBuilder();
        sb.append("""
                hello
                world
                """);

        for (int i = 0; i < 200; i++) {
            sb.append(i).append("\n");
        }

        mText.setValue(sb.toString());
    }

    public LiveData<String> getText() {
        return mText;
    }
}