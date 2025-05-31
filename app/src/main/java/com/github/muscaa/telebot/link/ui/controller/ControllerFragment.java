package com.github.muscaa.telebot.link.ui.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.muscaa.telebot.link.TelebotLink;
import com.github.muscaa.telebot.link.databinding.FragmentControllerBinding;

public class ControllerFragment extends Fragment {

    private FragmentControllerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ControllerViewModel controllerViewModel = new ViewModelProvider(this).get(ControllerViewModel.class);

        binding = FragmentControllerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText ip = binding.ip;
        EditText port = binding.port;
        EditText name = binding.name;
        Button connect = binding.connect;
        Button disconnect = binding.disconnect;

        connect.setOnClickListener(view -> {
            TelebotLink.INSTANCE.connect(ip.getText().toString(), Integer.parseInt(port.getText().toString()), name.getText().toString());
        });

        disconnect.setOnClickListener(view -> {
            TelebotLink.INSTANCE.disconnect();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}