package pl.edu.pwr.lab2.i238162;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.UseCase;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageCaptureActivity extends CameraPreviewActivity {
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
    }

    @Override
    protected UseCase getCaptureUseCase() {
        imageCapture = new ImageCapture.Builder().setTargetRotation(cameraPreviewView.getDisplay()
                                                                                     .getRotation())
                                                 .build();
        return imageCapture;
    }

    public void onTakePictureFabClick(View v) {
        File outputFile = new File(getCacheDir(), "temp.jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                outputFile).build();
        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Intent intent = new Intent(ImageCaptureActivity.this, ImageModificationsApplier.class);
                intent.putExtra("filePath", outputFile.getPath());
                startActivity(intent);

                ImageCaptureActivity.this.finish();
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                ImageCaptureActivity.this.runOnUiThread(() -> new AlertDialog.Builder(
                        ImageCaptureActivity.this).setMessage(R.string.image_save_fail_alert)
                                                  .setPositiveButton(R.string.alert_button_ok, null)
                                                  .setOnDismissListener(dialog -> ImageCaptureActivity.this.finish())
                                                  .show());
            }
        });
    }


}
