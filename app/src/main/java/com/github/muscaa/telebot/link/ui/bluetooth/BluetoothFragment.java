package com.github.muscaa.telebot.link.ui.bluetooth;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.muscaa.telebot.link.TelebotLink;
import com.github.muscaa.telebot.link.databinding.FragmentBluetoothBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import fluff.bin.Binary;

public class BluetoothFragment extends Fragment {

    private FragmentBluetoothBinding binding;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBluetoothBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        previewView = binding.previewView;
        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();

        return root;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this.getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                ResolutionSelector resolutionSelector = new ResolutionSelector.Builder()
                        .setResolutionFilter((supportedSizes, rotationDegrees) -> {
                            TelebotLink.INSTANCE.print(supportedSizes.stream()
                                    .map(Size::toString)
                                    .collect(Collectors.joining("; ")));

                            return supportedSizes.stream()
                                    .filter(size -> size.getWidth() == 1280 && size.getHeight() == 720)
                                    .collect(Collectors.toList());
                        })
                        .build();
                Preview preview = new Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .build();
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setResolutionSelector(resolutionSelector)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    //processFrame(image);
                    image.close();
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
            } catch (Exception e) {
                TelebotLink.INSTANCE.print(e.toString());
            }
        }, ContextCompat.getMainExecutor(this.getContext()));
    }

    private void processFrame(ImageProxy image) {
        int maxWidth = 1280 / 8;
        int maxHeight = 720 / 4;

        YuvImage yuvImage = new YuvImage(
                getYUVBytes(image),
                ImageFormat.NV21,
                image.getWidth(),
                image.getHeight(),
                null
        );

        try {
            for (int x = 0; x < image.getWidth(); x += maxWidth) {
                for (int y = 0; y < image.getHeight(); y += maxHeight) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Binary.Short(out::write, (short) x);
                    Binary.Short(out::write, (short) y);
                    Binary.Short(out::write, (short) maxWidth);
                    Binary.Short(out::write, (short) maxHeight);
                    yuvImage.compressToJpeg(new android.graphics.Rect(x, y, x + maxWidth, y + maxHeight), 60, out);

                    TelebotLink.INSTANCE.dsend(out.toByteArray());
                }
            }
        } catch (Exception e) {
            TelebotLink.INSTANCE.print(e.toString());
        }

        //byte[] jpeg = imageToByteArray(image);

        //if (!RcClient.INSTANCE.network.isConnected()) return;

        //RcClient.INSTANCE.network.send(new PacketFrame(jpeg));
    }

    private byte[] getYUVBytes(ImageProxy imageProxy) {
        ByteBuffer yBuffer = imageProxy.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = imageProxy.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = imageProxy.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        uBuffer.get(nv21, ySize + uSize, uSize);
        vBuffer.get(nv21, ySize, vSize);

        return nv21;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        cameraExecutor.shutdown();
    }
}