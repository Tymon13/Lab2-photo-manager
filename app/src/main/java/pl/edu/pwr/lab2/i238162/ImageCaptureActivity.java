package pl.edu.pwr.lab2.i238162;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageCaptureActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 13;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_capture);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startPreview();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPreview();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Camera permissions are required for adding new photos/videos")
                       .setPositiveButton("Ok", null)
                       .setOnDismissListener(dialog -> ImageCaptureActivity.this.finish());
                builder.create()
                       .show();
            }
        }
    }

    public void onTakePictureFabClick(View v) {
        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = timeStampPattern.format(java.time.LocalDateTime.now());
        String filename = "IMG_" + timestamp + ".jpg";
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(new File(this.getFilesDir(), filename)).build();
        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                ImageCaptureActivity.this.runOnUiThread(
                        () -> Toast.makeText(ImageCaptureActivity.this, "Image saved as " + filename,
                                             Toast.LENGTH_SHORT)
                                   .show());
                ImageCaptureActivity.this.finish();
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                ImageCaptureActivity.this.runOnUiThread(
                        () -> new AlertDialog.Builder(ImageCaptureActivity.this).setMessage("Failed to save image!")
                                                                                .setPositiveButton("Ok", null)
                                                                                .setOnDismissListener(
                                                                                        dialog -> ImageCaptureActivity.this.finish())
                                                                                .show());
            }
        });
    }

    private void startPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        PreviewView cameraPreviewView = findViewById(R.id.cameraPreviewView);
        preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                                                    .build();

        imageCapture = new ImageCapture.Builder().setTargetRotation(cameraPreviewView.getDisplay()
                                                                                     .getRotation())
                                                 .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

}