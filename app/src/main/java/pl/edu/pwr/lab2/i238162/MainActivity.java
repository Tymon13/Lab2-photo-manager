package pl.edu.pwr.lab2.i238162;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int MENU_SORT_BY_NAMES_ASCENDING = R.id.sort_names_ascending;
    public static final int MENU_SORT_BY_NAMES_DESCENDING = R.id.sort_names_descending;
    public static final int MENU_SORT_BY_DATES_ASCENDING = R.id.sort_by_date_ascending;
    public static final int MENU_SORT_BY_DATES_DESCENDING = R.id.sort_by_date_descending;

    private static final int fileRemoveUndoTimeout = 5000;
    private boolean fabSubmenuVisible = false;
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
        createDir(new File(getFilesDir(), getString(R.string.photos_directory)));
        createDir(new File(getFilesDir(), getString(R.string.videos_directory)));
    }

    private void createDir(File directory) {
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.i(this.getLocalClassName(), "Created " + directory + " directory.");
            } else {
                Log.e(this.getLocalClassName(), "Failed to create " + directory);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fabSubmenuVisible = false;
        setSubmenuVisibility(View.GONE);
        startRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        setupSearchAction(menu);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int mode = preferences.getInt(getString(R.string.sort_mode_preferences_key), MENU_SORT_BY_NAMES_ASCENDING);
        MenuItem itemToSelect = menu.findItem(mode);
        if (itemToSelect != null) {
            onOptionsItemSelected(itemToSelect);
        } else {
            //something went wrong, fix by resetting to default
            onOptionsItemSelected(menu.findItem(R.id.sort_names_ascending));
        }

        return true;
    }

    private void setupSearchAction(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search)
                                                 .getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            adapter.setFilter(null);
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        int sortMode = item.getItemId();
        adapter.sortItems(sortMode);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit()
                   .putInt(getString(R.string.sort_mode_preferences_key), sortMode)
                   .apply();
        return super.onOptionsItemSelected(item);
    }

    private void startRecyclerView() {
        int sortMode = getPreferences(MODE_PRIVATE).getInt(getString(R.string.sort_mode_preferences_key),
                                                           MENU_SORT_BY_NAMES_ASCENDING);
        adapter = new FileListAdapter(this, sortMode);
        RecyclerView recyclerView = findViewById(R.id.fileListRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onAddItemFabClick(View v) {
        if (fabSubmenuVisible) {
            setSubmenuVisibility(View.GONE);
        } else {
            setSubmenuVisibility(View.VISIBLE);
        }
        fabSubmenuVisible = !fabSubmenuVisible;
    }

    public void onAddPhotoFabClick(View v) {
        Intent myIntent = new Intent(this, ImageCaptureActivity.class);
        startActivity(myIntent);
    }

    public void onAddVideoFabClick(View v) {
        Intent myIntent = new Intent(this, VideoRecordActivity.class);
        startActivity(myIntent);
    }

    public void onAddAudioFabClick(View w) {
        Intent myIntent = new Intent(this, AudioRecordActivity.class);
        startActivity(myIntent);
    }

    private void setSubmenuVisibility(int visibility) {
        View addPhoto = findViewById(R.id.addImageFab);
        View addVideo = findViewById(R.id.addVideoFab);
        View addAudio = findViewById(R.id.addAudioFab);
        addPhoto.setVisibility(visibility);
        addVideo.setVisibility(visibility);
        addAudio.setVisibility(visibility);
    }

}
