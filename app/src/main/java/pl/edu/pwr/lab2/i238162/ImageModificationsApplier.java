package pl.edu.pwr.lab2.i238162;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;

public class ImageModificationsApplier extends AppCompatActivity {
    private String tempFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_modifications_applier);
        tempFilePath = getIntent().getStringExtra("filePath");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView preview = findViewById(R.id.imagePreviewView);
        preview.setImageBitmap(BitmapFactory.decodeFile(tempFilePath));
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
                    Files.copy(new File(croppedFilePath).toPath(), new File(tempFilePath).toPath(),
                               StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    Toast.makeText(this, getString(R.string.image_save_fail_message), Toast.LENGTH_LONG)
                         .show();
                    e.printStackTrace();
                }
                File croppedFileCache = new File(result.getUri()
                                                       .getPath());
                croppedFileCache.deleteOnExit();
            }
        }
    }

    public void onCropButtonClick(View v) {
        File f = new File(tempFilePath);
        Uri uri = Uri.fromFile(f);
        CropImage.activity(uri)
                 .start(this);
    }

    public void onSaveButtonClick(View v) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = timeStampPattern.format(java.time.LocalDateTime.now());
        String defaultFilename = getString(R.string.filename_photo_prompt_default, timestamp);
        input.setText(defaultFilename);

        new AlertDialog.Builder(this).setTitle(R.string.filename_prompt_title)
                                     .setView(input)
                                     .setPositiveButton(R.string.save_button_text, (dialog, which) -> {
                                         saveFile(input.getText()
                                                       .toString() + getString(R.string.filename_photo_extension));
                                         finish();
                                     })
                                     .setNegativeButton(R.string.cancel_button, null)
                                     .setOnDismissListener(dialog -> {
                                         File tempFile = new File(tempFilePath);
                                         tempFile.deleteOnExit();
                                     })
                                     .show();
    }

    private void saveFile(String filename) {
        File baseDirectory = new File(getFilesDir(), getString(R.string.photos_directory));
        File outputFile = new File(baseDirectory, filename);
        try {
            Files.copy(new File(tempFilePath).toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.image_save_fail_message), Toast.LENGTH_LONG)
                 .show();
            e.printStackTrace();
        }

        Toast.makeText(ImageModificationsApplier.this, getString(R.string.image_saved_text, filename),
                       Toast.LENGTH_SHORT)
             .show();
    }
}
