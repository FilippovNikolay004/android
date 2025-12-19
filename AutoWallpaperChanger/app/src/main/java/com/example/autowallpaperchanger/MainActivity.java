package com.example.autowallpaperchanger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStartService);
        Button btnStop = findViewById(R.id.btnStopService);

        btnStart.setOnClickListener(v -> startService(new Intent(this, WallpaperService.class)));

        btnStop.setOnClickListener(v -> stopService(new Intent(this, WallpaperService.class)));
    }
}