package pl.edu.pwr.lab2.i238162;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.fileListRecyclerView);
        recyclerView.setAdapter(new FileListAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onFabClick(View v) {
        Intent myIntent = new Intent(this, ImageCaptureActivity.class);
        startActivity(myIntent);
    }
}
