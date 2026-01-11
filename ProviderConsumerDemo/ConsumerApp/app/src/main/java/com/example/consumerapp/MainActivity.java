package com.example.consumerapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final Uri STUDENTS_URI = Uri.parse(
            "content://site.sunmeat.helloworld.provider/students");

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        String[] from = {"firstName", "lastName"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                0);

        listView.setAdapter(adapter);

        loadStudents();
    }

    private void loadStudents() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    STUDENTS_URI,
                    null, null, null, null);

            if (cursor != null) {
                Cursor old = adapter.swapCursor(cursor);
                if (old != null) {
                    old.close();
                }
            } else {
                Toast.makeText(
                        this,
                        "No data received. Is the provider app installed?",
                        Toast.LENGTH_LONG
                ).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(
                    this,
                    "No permission to access provider. Is it exported?",
                    Toast.LENGTH_LONG
            ).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(
                    this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
