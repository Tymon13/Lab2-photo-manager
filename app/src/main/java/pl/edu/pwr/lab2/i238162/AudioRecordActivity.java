package pl.edu.pwr.lab2.i238162;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class AudioRecordActivity extends AppCompatActivity {
    private static final int RECORD_AUDIO_PERMISSION_CODE = 169;

    private MediaRecorder recorder;
    private String cacheFilePath;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }

        File cacheFile = new File(getCacheDir(), "temp.3gp");
        cacheFilePath = cacheFile.getPath();

        prepareRecorder(cacheFile);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.audio_permission_for_audio_dialog)
                       .setPositiveButton(R.string.alert_button_ok, null)
                       .setOnDismissListener(dialog -> this.finish());
                builder.create()
                       .show();
            }
        }
    }


    private void prepareRecorder(File cacheFile) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(cacheFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRecordFabClick(View v) {
        FloatingActionButton fab = findViewById(R.id.startRecordingFab);
        if (isRecording) {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_circle_24));
            recorder.stop();
            Intent intent = new Intent(this, AudioPreviewBeforeSave.class);
            intent.putExtra("filePath", cacheFilePath);
            startActivity(intent);
            this.finish();
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_stop_24));
            recorder.start();
        }

        isRecording = !isRecording;
    }
}
