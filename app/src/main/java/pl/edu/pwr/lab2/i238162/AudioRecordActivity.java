package pl.edu.pwr.lab2.i238162;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class AudioRecordActivity extends AppCompatActivity {
    private MediaRecorder recorder;
    private String cacheFilePath;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        File cacheFile = new File(getCacheDir(), "temp.3gp");
        cacheFilePath = cacheFile.getPath();

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
            Intent intent = new Intent(this, VideoPreviewBeforeSave.class);
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
