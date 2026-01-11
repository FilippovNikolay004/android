package com.example.birthdaycontactsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_CONTACTS_CHANGED =
            "com.example.birthdaycontactsapp.ACTION_CONTACTS_CHANGED";
    private static final int PERMISSION_CODE = 1;
    private static final String[][] SAMPLE_CONTACTS = new String[][]{
            {"December Demo 1", "1990-12-12", "+10000000001"},
            {"December Demo 2", "1985-12-15", "+10000000002"},
            {"December Demo 3", "2000-12-25", "+10000000003"}
    };

    private RecyclerView rvBirthdays;
    private ContactAdapter adapter;
    private final List<Contact> decemberBirthdays = new ArrayList<>();

    private final BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshBirthdaysList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBirthdays = findViewById(R.id.rvBirthdays);
        rvBirthdays.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this, decemberBirthdays);
        rvBirthdays.setAdapter(adapter);

        if (hasContactsPermissions()) {
            initData();
        } else {
            requestContactsPermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_CONTACTS_CHANGED);
        ContextCompat.registerReceiver(this, refreshReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(refreshReceiver);
    }

    private void initData() {
        createSampleContactsIfNeeded();
        refreshBirthdaysList();
        schedulePeriodicScan();
    }

    private boolean hasContactsPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        }, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_CODE) {
            return;
        }
        if (hasContactsPermissions()) {
            initData();
        } else {
            Toast.makeText(this, R.string.permissions_required, Toast.LENGTH_SHORT).show();
        }
    }

    private void createSampleContactsIfNeeded() {
        for (String[] contact : SAMPLE_CONTACTS) {
            ensureContactWithBirthday(contact[0], contact[1], contact[2]);
        }
    }

    private void ensureContactWithBirthday(String name, String birthdayIso, String phoneNumber) {
        long contactId = getContactIdByName(name);
        if (contactId == -1L) {
            addContact(name, birthdayIso, phoneNumber);
            return;
        }
        if (!hasBirthdayEvent(contactId)) {
            long rawContactId = getRawContactId(contactId);
            if (rawContactId != -1L) {
                addBirthdayToRawContact(rawContactId, birthdayIso);
            }
        }
    }

    private long getContactIdByName(String name) {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        try (Cursor cursor = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                new String[]{name},
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return -1L;
    }

    private boolean hasBirthdayEvent(long contactId) {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{ContactsContract.Data._ID};
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ? AND "
                + ContactsContract.CommonDataKinds.Event.TYPE + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(contactId),
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
        };
        try (Cursor cursor = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            return cursor != null && cursor.moveToFirst();
        }
    }

    private long getRawContactId(long contactId) {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        try (Cursor cursor = cr.query(
                ContactsContract.RawContacts.CONTENT_URI,
                projection,
                selection,
                new String[]{String.valueOf(contactId)},
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return -1L;
    }

    private void addContact(String name, String birthdayIso, String phoneNumber) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);
        Uri rawContactUri = cr.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        if (rawContactUri == null) {
            return;
        }
        long rawContactId = ContentUris.parseId(rawContactUri);

        insertName(rawContactId, name);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            insertPhone(rawContactId, phoneNumber);
        }
        insertBirthday(rawContactId, birthdayIso);

        Toast.makeText(this, getString(R.string.contact_added, name), Toast.LENGTH_SHORT).show();
    }

    private void addBirthdayToRawContact(long rawContactId, String birthdayIso) {
        insertBirthday(rawContactId, birthdayIso);
    }

    private void insertName(long rawContactId, String name) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    private void insertPhone(long rawContactId, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    private void insertBirthday(long rawContactId, String birthdayIso) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Event.START_DATE, birthdayIso);
        values.put(ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    private void refreshBirthdaysList() {
        if (!hasContactsPermissions()) {
            return;
        }
        decemberBirthdays.clear();
        decemberBirthdays.addAll(ContactScanner.loadDecemberBirthdays(this));
        adapter.notifyDataSetChanged();
    }

    private void schedulePeriodicScan() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                BirthdayWorker.class,
                1,
                TimeUnit.DAYS
        ).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                BirthdayWorker.PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void sendSms(String phoneNumber, String message) {
        if (!hasSmsPermission()) {
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
