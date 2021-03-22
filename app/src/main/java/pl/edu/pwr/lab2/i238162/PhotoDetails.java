package pl.edu.pwr.lab2.i238162;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

public class PhotoDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        String filePath = getIntent().getStringExtra("filePath");
        PhotoView photoView = findViewById(R.id.photo_view);
        photoView.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }
}
