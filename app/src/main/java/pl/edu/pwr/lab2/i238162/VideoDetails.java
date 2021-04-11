package pl.edu.pwr.lab2.i238162;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;

public class VideoDetails extends AppCompatActivity {
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        String filePath = getIntent().getStringExtra("filePath");

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
}
