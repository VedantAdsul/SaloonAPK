package com.mountreachsolution.SaloonAPK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.mountreachsolution.SaloonAPK.databinding.ActivityCameraBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding b;
    private String gender;
    private String service;
    private FaceDetector faceDetector;
    private ExecutorService analysisExecutor;
    private Handler mainHandler;

    private enum State { IDLE, SEARCHING, ANALYZING, DONE }
    private State currentState = State.IDLE;

    private final List<FaceShapeAnalyzer.FaceFeatures> featureSamples = new ArrayList<>();
    private static final int ANALYSIS_DURATION_MS = 2500;
    private static final int REQUIRED_SAMPLES = 15;

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCamera();
                else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        gender = getIntent().getStringExtra("gender");
        service = getIntent().getStringExtra("service");

        analysisExecutor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        setupFaceDetector();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }

        b.btnStartAnalysis.setOnClickListener(v -> {
            b.btnStartAnalysis.setVisibility(View.GONE);
            b.analysisProgress.setVisibility(View.VISIBLE);
            b.tvInstruction.setText("Position your face inside the oval");
            currentState = State.SEARCHING;
        });
    }

    private void setupFaceDetector() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();
        faceDetector = FaceDetection.getClient(options);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                preview.setSurfaceProvider(b.previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(720, 1280))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(analysisExecutor, this::analyzeFrame);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void analyzeFrame(ImageProxy imageProxy) {
        if (currentState == State.IDLE || currentState == State.DONE) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        handleFaceDetected(faces.get(0));
                    } else {
                        if (currentState == State.ANALYZING) {
                            resetAnalysis();
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void handleFaceDetected(Face face) {
        if (currentState == State.SEARCHING) {
            startAnalysisCountdown();
        }

        if (currentState == State.ANALYZING) {
            FaceShapeAnalyzer.FaceFeatures features = FaceShapeAnalyzer.extractFeatures(face);
            if (features != null) {
                featureSamples.add(features);
                mainHandler.post(() -> {
                    int progress = (int) (((float) featureSamples.size() / REQUIRED_SAMPLES) * 100);
                    b.analysisProgress.setProgress(Math.min(100, progress));
                });
            }
        }
    }

    private void startAnalysisCountdown() {
        currentState = State.ANALYZING;
        featureSamples.clear();
        mainHandler.post(() -> {
            b.tvInstruction.setText("Hold steady, analyzing...");
            b.analysisProgress.setProgress(0, true);
        });

        mainHandler.postDelayed(this::finishAnalysis, ANALYSIS_DURATION_MS);
    }

    private void finishAnalysis() {
        if (currentState != State.ANALYZING) return;
        currentState = State.DONE;
        analysisExecutor.shutdown();

        if (featureSamples.size() < REQUIRED_SAMPLES / 2) {
            mainHandler.post(() -> Toast.makeText(this, "Could not get a stable lock. Please try again.", Toast.LENGTH_LONG).show());
            resetAnalysis();
            return;
        }

        float avgRatio = 0, avgJawAngle = 0, avgForeheadW = 0, avgCheekW = 0, avgJawW = 0;
        for (FaceShapeAnalyzer.FaceFeatures sample : featureSamples) {
            avgRatio += sample.ratio;
            avgJawAngle += sample.jawAngle;
            avgForeheadW += sample.foreheadW;
            avgCheekW += sample.cheekW;
            avgJawW += sample.jawW;
        }
        int sampleCount = featureSamples.size();
        FaceShapeAnalyzer.FaceFeatures averagedFeatures = new FaceShapeAnalyzer.FaceFeatures(
                avgRatio / sampleCount,
                avgJawAngle / sampleCount,
                avgForeheadW / sampleCount,
                avgCheekW / sampleCount,
                avgJawW / sampleCount
        );

        String finalShape = FaceShapeAnalyzer.classifyFromAverageFeatures(averagedFeatures);

        Bitmap bitmap = b.previewView.getBitmap();

        if (bitmap != null) {
            BitmapHolder.setBitmap(bitmap);
            Intent i = new Intent(CameraActivity.this, ResultActivity.class);
            i.putExtra("faceShape", finalShape);
            i.putExtra("gender", gender);
            i.putExtra("service", service);
            i.putExtra("debugInfo", averagedFeatures.getDebugInfo());
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Could not capture image. Please try again.", Toast.LENGTH_SHORT).show();
            resetAnalysis();
        }
    }

    private void resetAnalysis() {
        mainHandler.post(() -> {
            currentState = State.IDLE;
            featureSamples.clear();
            mainHandler.removeCallbacksAndMessages(null);
            b.tvInstruction.setText("Press Start to Begin Analysis");
            b.analysisProgress.setVisibility(View.INVISIBLE);
            b.analysisProgress.setProgress(0);
            b.btnStartAnalysis.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (analysisExecutor != null && !analysisExecutor.isShutdown()) {
            analysisExecutor.shutdown();
        }
    }
}