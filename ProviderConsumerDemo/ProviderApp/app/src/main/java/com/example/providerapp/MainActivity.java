package com.example.providerapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase db = AppDatabase.getDatabase(this);
        db.studentDao().deleteAll();
        db.studentDao().insert(new Student("Ivan", "Petrov"));
        db.studentDao().insert(new Student("Maria", "Sidorova"));
        db.studentDao().insert(new Student("Alexey", "Ivanov"));
    }
}
