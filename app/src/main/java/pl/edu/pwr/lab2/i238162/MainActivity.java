package pl.edu.pwr.lab2.i238162;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int fileRemoveUndoTimeout = 5000;
    private FileListAdapter adapter;
    private final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                                                                                                              ItemTouchHelper.LEFT |
                                                                                                                      ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            String filePath = adapter.startRemoveFileTimer(position, fileRemoveUndoTimeout);
            String filename = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
            Snackbar.make(findViewById(R.id.MainLayout), getString(R.string.remove_file_notification, filename),
                          fileRemoveUndoTimeout)
                    .setAction(R.string.undo_button, v -> adapter.cancelRemoveFileTimer(filePath, position))
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMediaDirectories();
    }

    private void createMediaDirectories() {
        File photosDirectory = new File(getFilesDir(), getString(R.string.photos_directory));
        if (!photosDirectory.exists()) {
            if (photosDirectory.mkdirs()) {
                Log.i(this.getLocalClassName(), "Created " + photosDirectory + " directory.");
            } else {
                Log.e(this.getLocalClassName(), "Failed to create " + photosDirectory);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRecyclerView();
    }

    private void startRecyclerView() {
        adapter = new FileListAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.fileListRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onFabClick(View v) {
        Intent myIntent = new Intent(this, ImageCaptureActivity.class);
        startActivity(myIntent);
    }
}
