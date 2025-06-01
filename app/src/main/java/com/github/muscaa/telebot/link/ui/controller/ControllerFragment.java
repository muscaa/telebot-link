package com.github.muscaa.telebot.link.ui.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.muscaa.telebot.link.TelebotLink;
import com.github.muscaa.telebot.link.databinding.FragmentControllerBinding;

import java.util.Arrays;
import java.util.List;

public class ControllerFragment extends Fragment {

    private FragmentControllerBinding binding;
    private LinearLayout listLayout;

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
        listLayout = binding.list;

        TelebotLink.INSTANCE.mClients.observe(getViewLifecycleOwner(), this::setClients);

        connect.setOnClickListener(view -> {
            TelebotLink.INSTANCE.connect(ip.getText().toString(), Integer.parseInt(port.getText().toString()), name.getText().toString());
        });

        disconnect.setOnClickListener(view -> {
            TelebotLink.INSTANCE.disconnect();
        });

        return root;
    }

    private void setClients(List<String> list) {
        listLayout.removeAllViews();

        for (String name : list) {
            LinearLayout panelLayout = new LinearLayout(getContext());
            panelLayout.setOrientation(LinearLayout.HORIZONTAL);
            panelLayout.setPadding(8, 8, 8, 8);
            panelLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(getContext());
            textView.setText(name);
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button button = new Button(getContext());
            button.setText("Link");
            button.setOnClickListener(view -> {
                TelebotLink.INSTANCE.link(name);
            });

            panelLayout.addView(textView);
            panelLayout.addView(button);

            listLayout.addView(panelLayout);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}