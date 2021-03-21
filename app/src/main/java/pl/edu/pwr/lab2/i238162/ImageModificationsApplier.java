package pl.edu.pwr.lab2.i238162;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ImageModificationsApplier extends AppCompatActivity {
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_modifications_applier);
        filePath = getIntent().getStringExtra("filePath");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView preview = findViewById(R.id.imagePreviewView);
        preview.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                String croppedFilePath = result.getUri()
                                               .getPath();
                try {
                    FileInputStream fis = new FileInputStream(croppedFilePath);
                    FileOutputStream fos = new FileOutputStream(filePath);
                    FileChannel inChannel = fis.getChannel();
                    FileChannel outChannel = fos.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    fis.close();
                    fos.close();
                    File croppedFileCache = new File(result.getUri()
                                                           .getPath());
                    croppedFileCache.deleteOnExit();
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to save cropped image", Toast.LENGTH_LONG)
                         .show();
                    e.printStackTrace();
                }
            }
        }
    }

    public void onCropButtonClick(View v) {
        File f = new File(filePath);
        Uri uri = Uri.fromFile(f);
        CropImage.activity(uri)
                 .start(this);
    }

    public void onSaveButtonClick(View v) {
        String filename = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
        Toast.makeText(this, getString(R.string.image_saved_text, filename), Toast.LENGTH_SHORT)
             .show();
        finish();
    }
}
