package pl.edu.pwr.lab2.i238162;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final File[] fileList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView filenameView;
        public ViewHolder(View view) {
            super(view);

            filenameView = view.findViewById(R.id.filenameView);
        }

        public TextView getFilenameView() {
            return filenameView;
        }
    }

    public FileListAdapter(Context c) {
        fileList = c.getFilesDir().listFiles();
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
        viewHolder.getFilenameView().setText(fileList[position].getName());
    }

    @Override
    public int getItemCount() {
        return fileList.length;
    }
}
