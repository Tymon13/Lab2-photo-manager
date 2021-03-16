package pl.edu.pwr.lab2.i238162;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final File[] fileList;

    public FileListAdapter(Context c) {
        fileList = c.getFilesDir()
                    .listFiles();
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
        viewHolder.getFilenameView()
                  .setText(fileList[position].getName());
        ImageView previewView = viewHolder.getPreviewView();
        int imageDimension = viewHolder.getPreviewViewDimension();
        previewView.setImageBitmap(
                decodeSampledBitmapFromResource(fileList[position].getPath(), imageDimension, imageDimension));
    }

    @Override
    public int getItemCount() {
        return fileList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView filenameView;
        private final ImageView previewImageView;
        private final int previewDimension;

        public ViewHolder(View view) {
            super(view);

            filenameView = view.findViewById(R.id.filenameView);
            previewImageView = view.findViewById(R.id.filePreviewImage);
            previewDimension = (int) view.getResources()
                                         .getDimension(R.dimen.file_list_image_preview_size);
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
    }

}
