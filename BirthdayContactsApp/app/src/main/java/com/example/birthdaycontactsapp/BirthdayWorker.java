package com.example.birthdaycontactsapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.List;

public class BirthdayWorker extends Worker {
    public static final String PERIODIC_WORK_NAME = "birthday_scan_periodic";
    public static final String ONE_TIME_WORK_NAME = "birthday_scan_one_time";
    private static final String PREFS_NAME = "birthday_worker_prefs";
    private static final String KEY_DECEMBER_COUNT = "december_count";
    private static final String KEY_LAST_SCAN = "last_scan";

    public BirthdayWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure();
        }

        List<Contact> birthdays = ContactScanner.loadDecemberBirthdays(getApplicationContext());
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_DECEMBER_COUNT, birthdays.size())
                .putLong(KEY_LAST_SCAN, System.currentTimeMillis())
                .apply();
        return Result.success();
    }
}
