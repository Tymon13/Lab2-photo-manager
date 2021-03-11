package pl.edu.pwr.lab2.i238162;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFabClick(View v) {
        Intent myIntent = new Intent(this, ImageCapture.class);
        startActivity(myIntent);
    }
}
