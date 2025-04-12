package com.github.muscaa.telebot.link.ui.logs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.muscaa.telebot.link.TelebotLink;
import com.github.muscaa.telebot.link.databinding.FragmentLogsBinding;

public class LogsFragment extends Fragment {

    private FragmentLogsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogsViewModel logsViewModel = new ViewModelProvider(this).get(LogsViewModel.class);

        binding = FragmentLogsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textLogs = binding.textLogs;
        TelebotLink.INSTANCE.mLogs.observe(getViewLifecycleOwner(), textLogs::setText);

        TelebotLink.INSTANCE.print("logs");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}