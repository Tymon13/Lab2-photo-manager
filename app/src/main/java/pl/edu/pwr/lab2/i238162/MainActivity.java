package pl.edu.pwr.lab2.i238162;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFabClick(View v) {
        Toast.makeText(this, "Not implemented, heh", Toast.LENGTH_LONG).show();
    }
}
