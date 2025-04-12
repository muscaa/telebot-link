package com.github.muscaa.telebot.link.ui.logs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is logs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}