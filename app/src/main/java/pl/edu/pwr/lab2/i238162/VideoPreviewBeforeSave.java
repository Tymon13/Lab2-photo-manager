package pl.edu.pwr.lab2.i238162;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;

public class VideoPreviewBeforeSave extends AppCompatActivity {
    // probably not best way to solve this, but at least it's DRY
    protected int saveDirectory = R.string.videos_directory;
    protected int saveFailMessage = R.string.video_save_fail_message;
    protected int savedText = R.string.video_saved_text;
    protected int filenamePromptDefault = R.string.filename_video_prompt_default;
    protected int filenameExtension = R.string.filename_video_extension;

    private SimpleExoPlayer player;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview_before_save);
        filePath = getIntent().getStringExtra("filePath");

        player = new SimpleExoPlayer.Builder(this).build();
        StyledPlayerView playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        MediaItem video = MediaItem.fromUri(Uri.fromFile(new File(filePath)));
        player.addMediaItem(video);
        player.prepare();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    public void onSaveButtonClick(View v) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = timeStampPattern.format(java.time.LocalDateTime.now());
        String defaultFilename = getString(filenamePromptDefault, timestamp);
        input.setText(defaultFilename);

        new AlertDialog.Builder(this).setTitle(R.string.filename_prompt_title)
                                     .setView(input)
                                     .setPositiveButton(R.string.save_button_text, (dialog, which) -> {
                                         saveFile(input.getText()
                                                       .toString() + getString(filenameExtension));
                                         finish();
                                     })
                                     .setNegativeButton(R.string.cancel_button, null)
                                     .setOnDismissListener(dialog -> {
                                         File tempFile = new File(filePath);
                                         tempFile.deleteOnExit();
                                     })
                                     .show();
    }

    private void saveFile(String filename) {
        File baseDirectory = new File(getFilesDir(), getString(saveDirectory));
        File outputFile = new File(baseDirectory, filename);
        try {
            Files.copy(new File(filePath).toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Toast.makeText(this, getString(saveFailMessage), Toast.LENGTH_LONG)
                 .show();
            e.printStackTrace();
        }

        Toast.makeText(this, getString(savedText, filename), Toast.LENGTH_SHORT)
             .show();
    }
}
