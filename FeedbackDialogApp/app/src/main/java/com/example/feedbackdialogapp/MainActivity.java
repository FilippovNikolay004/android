package com.example.feedbackdialogapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LAUNCH_COUNT = "launch_count";
    private static final int SHOW_FEEDBACK_AFTER = 5;
    private static final String FAVORITE_APP_PACKAGE = "org.telegram.messenger";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        incrementLaunchCount();
    }

    private void incrementLaunchCount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(LAUNCH_COUNT, 0);
        count++;
        prefs.edit().putInt(LAUNCH_COUNT, count).apply();

        if (count == SHOW_FEEDBACK_AFTER) {
            showFeedbackDialog();
        }
    }

    private void showFeedbackDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null); // Создадим этот layout ниже
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        ratingBar.setRating(0);

        new AlertDialog.Builder(this)
                .setTitle("Tell us what you think")
                .setView(dialogView)
                .setPositiveButton("Отправить", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    if (rating <= 3) {
                        showFeedbackChoiceDialog();
                    } else {
                        showPlayMarketDialog();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showFeedbackChoiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Оставить фидбек?")
                .setMessage("Хотите поделиться деталями?")
                .setPositiveButton("Да", (dialog, which) -> showFeedbackInputDialog())
                .setNegativeButton("Нет", null)
                .show();
    }

    private void showFeedbackInputDialog() {
        EditText input = new EditText(this);
        input.setHint("Ваш отзыв...");

        new AlertDialog.Builder(this)
                .setTitle("Фидбек")
                .setView(input)
                .setPositiveButton("Отправить", (dialog, which) -> {
                    String feedback = input.getText().toString();
                    Toast.makeText(this, "Спасибо за отзыв: " + feedback, Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showPlayMarketDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Спасибо за высокую оценку!")
                .setMessage("Перейти в Play Market и оставить отзыв?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + FAVORITE_APP_PACKAGE));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + FAVORITE_APP_PACKAGE)));
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}