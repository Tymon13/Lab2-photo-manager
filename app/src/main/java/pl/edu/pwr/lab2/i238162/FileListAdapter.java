package pl.edu.pwr.lab2.i238162;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final ArrayList<File> fileList;
    private final Handler handler = new Handler();
    private final HashMap<String, Runnable> pendingFileRemoves = new HashMap<>();
    private final Context parentContext;
    private final File favouritesFile;
    private final List<String> favourites;

    public FileListAdapter(Context c, int sortMode) {
        parentContext = c;
        favouritesFile = new File(parentContext.getFilesDir(), "favourites.txt");
        favourites = readFavourites();

        fileList = new ArrayList<>();
        File photosDir = new File(c.getFilesDir(), c.getString(R.string.photos_directory));
        File[] photos = photosDir.listFiles();
        if (photos != null) {
            Collections.addAll(fileList, photos);
        }
        sortItems(sortMode);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    private static String getCreationDate(File file) {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            //there's probably a better method to do that, but I don't know it
            String creationDate = "" + attr.creationTime();
            creationDate = creationDate.replace('T', ' ');
            return creationDate.substring(0, creationDate.length() - 1);
        } catch (Exception e) {
            return "?";
        }
    }

    public void sortItems(int itemId) {
        switch (itemId) {
            case MainActivity.MENU_SORT_BY_NAMES_ASCENDING:
                fileList.sort(Comparator.comparing(File::getName));
                break;
            case MainActivity.MENU_SORT_BY_NAMES_DESCENDING:
                fileList.sort(Comparator.comparing(File::getName)
                                        .reversed());
                break;
            case MainActivity.MENU_SORT_BY_DATES_ASCENDING:
                fileList.sort(Comparator.comparing(FileListAdapter::getCreationDate));
                break;
            case MainActivity.MENU_SORT_BY_DATES_DESCENDING:
                fileList.sort(Comparator.comparing(FileListAdapter::getCreationDate)
                                        .reversed());
                break;
        }
        notifyDataSetChanged();
    }

    private List<String> readFavourites() {
        if (!favouritesFile.exists()) {
            try {
                if (!favouritesFile.createNewFile()) {
                    Log.e(getClass().getName(), "Error when creating favourites file");
                }
            } catch (IOException e) {
                Log.e(getClass().getName(), "Error when creating favourites file");
            }
        }
        try {
            return Files.readAllLines(favouritesFile.toPath());
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error when reading favourites file");
        }
        return new ArrayList<>();
    }

    private void writeFavourites() {
        try {
            Files.write(favouritesFile.toPath(), favourites);
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error when writing favourites file");
        }
    }

    public String startRemoveFileTimer(int position, int timeoutInMs) {
        File fileToRemove = fileList.remove(position);
        String filename = fileToRemove.getPath();
        Log.i(this.getClass()
                  .getName(), "Adding task to remove " + filename + " after " + timeoutInMs + "ms");
        Runnable remover = () -> {
            Log.i(this.getClass()
                      .getName(), "Removing " + filename + " now.");
            pendingFileRemoves.remove(filename);
            if (!fileToRemove.delete()) {
                Log.e(this.getClass()
                          .getName(), "Removing " + filename + " failed.");
            }
        };
        pendingFileRemoves.put(filename, remover);
        handler.postDelayed(remover, timeoutInMs);
        this.notifyItemRemoved(position);
        return filename;
    }

    public void cancelRemoveFileTimer(String filename, int position) {
        Runnable remover = pendingFileRemoves.remove(filename);
        handler.removeCallbacks(remover);
        Log.i(this.getClass()
                  .getName(), "Stopping task to remove " + filename + ".");
        fileList.add(position, new File(filename));
        this.notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.file_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String filename = fileList.get(position)
                                  .getName();
        viewHolder.getFilenameView()
                  .setText(filename);
        viewHolder.getCreationDateView()
                  .setText(getCreationDate(fileList.get(position)));

        ImageView previewView = viewHolder.getPreviewView();
        int imageDimension = viewHolder.getPreviewViewDimension();
        previewView.setImageBitmap(decodeSampledBitmapFromResource(fileList.get(position)
                                                                           .getPath(), imageDimension, imageDimension));

        Drawable icon;
        if (favourites.contains(filename)) {
            icon = ContextCompat.getDrawable(parentContext, R.drawable.ic_baseline_star_24);
        } else {
            icon = ContextCompat.getDrawable(parentContext, R.drawable.ic_baseline_star_border_24);
        }
        viewHolder.getFavouriteButton()
                  .setImageDrawable(icon);
        viewHolder.getFavouriteButton()
                  .setOnClickListener(v -> toggleItemOnFavouritesList(viewHolder, filename));
    }

    private void toggleItemOnFavouritesList(ViewHolder viewHolder, String filename) {
        ImageView favouriteButton = viewHolder.getFavouriteButton();
        if (favourites.contains(filename)) {
            favouriteButton.setImageDrawable(
                    ContextCompat.getDrawable(parentContext, R.drawable.ic_baseline_star_border_24));
            favourites.remove(filename);
            writeFavourites();
            Toast.makeText(parentContext, parentContext.getString(R.string.favourites_removed), Toast.LENGTH_SHORT)
                 .show();
        } else {
            favouriteButton.setImageDrawable(ContextCompat.getDrawable(parentContext, R.drawable.ic_baseline_star_24));
            favourites.add(filename);
            writeFavourites();
            Toast.makeText(parentContext, parentContext.getString(R.string.favourites_added), Toast.LENGTH_SHORT)
                 .show();
        }
    }


    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView previewImageView;
        private final int previewDimension;
        private final TextView filenameView;
        private final TextView creationDateView;
        private final ImageView favouriteButton;

        public ViewHolder(View view) {
            super(view);

            filenameView = view.findViewById(R.id.filenameView);
            previewImageView = view.findViewById(R.id.filePreviewImage);
            previewDimension = (int) view.getResources()
                                         .getDimension(R.dimen.file_list_image_preview_size);
            creationDateView = view.findViewById(R.id.creationDateView);
            favouriteButton = view.findViewById(R.id.favouriteIconView);
        }

        public TextView getFilenameView() {
            return filenameView;
        }

        public ImageView getPreviewView() {
            return previewImageView;
        }

        public int getPreviewViewDimension() {
            return previewDimension;
        }

        public TextView getCreationDateView() {
            return creationDateView;
        }

        public ImageView getFavouriteButton() {
            return favouriteButton;
        }
    }

}
