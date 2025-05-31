package com.github.muscaa.telebot.link.ui.bluetooth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.muscaa.telebot.link.TelebotLink;
import com.github.muscaa.telebot.link.databinding.FragmentHomeBinding;

public class BluetoothFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BluetoothViewModel bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        bluetoothViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        TelebotLink.INSTANCE.print("bluetooth");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}