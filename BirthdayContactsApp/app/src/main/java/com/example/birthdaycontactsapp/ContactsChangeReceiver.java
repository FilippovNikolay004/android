package com.example.birthdaycontactsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ContactsChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent refreshIntent = new Intent(MainActivity.ACTION_CONTACTS_CHANGED);
        refreshIntent.setPackage(context.getPackageName());
        context.sendBroadcast(refreshIntent);

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BirthdayWorker.class).build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                BirthdayWorker.ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
        );

        Toast.makeText(context, R.string.contacts_changed, Toast.LENGTH_SHORT).show();
    }
}
