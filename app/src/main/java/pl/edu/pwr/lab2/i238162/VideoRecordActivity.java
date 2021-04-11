package pl.edu.pwr.lab2.i238162;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.UseCase;
import androidx.camera.core.VideoCapture;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class VideoRecordActivity extends CameraPreviewActivity {
    private static final int RECORD_AUDIO_PERMISSION_CODE = 169;
    private FloatingActionButton recordButton;
    private VideoCapture videoCapture;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        recordButton = findViewById(R.id.takePictureFab);
        recordButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_circle_24));
        recordButton.setOnClickListener(v -> recordFabAction());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.audio_permission_needed_dialog)
                       .setPositiveButton(R.string.alert_button_ok, null)
                       .setOnDismissListener(dialog -> VideoRecordActivity.this.finish());
                builder.create()
                       .show();
            }
        }
    }

    private void recordFabAction() {
        if (isRecording) {
            recordButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_circle_24));
            videoCapture.stopRecording();
        } else {
            recordButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_stop_24));
            startRecording();
        }
        isRecording = !isRecording;
    }

    @SuppressLint("RestrictedApi") // Video capture is not officially supported, but it works, so...
    private void startRecording() {
        File cacheOutput = new File(getCacheDir(), "temp.mp4");
        VideoCapture.OutputFileOptions options = new VideoCapture.OutputFileOptions.Builder(cacheOutput).build();
        videoCapture.startRecording(options, ContextCompat.getMainExecutor(this),
                                    new VideoCapture.OnVideoSavedCallback() {
                                        @Override
                                        public void onVideoSaved(
                                                @NonNull VideoCapture.OutputFileResults outputFileResults) {
                                            Log.i("VideoRecord", "Video recorded successfully");
                                            Intent intent = new Intent(VideoRecordActivity.this,
                                                                       VideoPreviewBeforeSave.class);
                                            intent.putExtra("filePath", cacheOutput.getPath());
                                            startActivity(intent);

                                            VideoRecordActivity.this.finish();
                                        }

                                        @Override
                                        public void onError(int videoCaptureError, @NonNull String message,
                                                            @Nullable Throwable cause) {
                                            Toast.makeText(VideoRecordActivity.this, "Failed to record a video",
                                                           Toast.LENGTH_LONG)
                                                 .show();
                                            Log.e("VideoRecord", "Video record failed: " + message);
                                        }
                                    });
    }

    @Override
    protected UseCase getCaptureUseCase() {
        videoCapture = new VideoCapture.Builder().build();
        return videoCapture;
    }
}
