package com.example.sharebuttonapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextMessage = findViewById(R.id.editTextMessage);
        Button btnShare = findViewById(R.id.btnShare);

        btnShare.setOnClickListener(v -> {
            String text = editTextMessage.getText().toString().trim();

            if (text.isEmpty()) {
                editTextMessage.setError("Введите текст!");
                return;
            }

            // Неявный Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);

            Intent chooser = Intent.createChooser(shareIntent, "Поделиться через...");

            // Проверяем, есть ли хоть одно приложение для обработки
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        });
    }
}