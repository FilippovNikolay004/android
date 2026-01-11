package com.example.birthdaycontactsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.List;

public final class ContactScanner {
    private ContactScanner() {
    }

    public static List<Contact> loadDecemberBirthdays(Context context) {
        List<Contact> results = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.START_DATE
        };
        String selection = ContactsContract.Data.MIMETYPE + " = ? AND "
                + ContactsContract.CommonDataKinds.Event.TYPE + " = ?";
        String[] selectionArgs = new String[]{
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
            if (cursor == null) {
                return results;
            }
            int nameIndex = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
            int dateIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
            while (cursor.moveToNext()) {
                String name = nameIndex >= 0 ? cursor.getString(nameIndex) : "";
                String rawDate = dateIndex >= 0 ? cursor.getString(dateIndex) : "";
                if (isDecemberBirthday(rawDate)) {
                    results.add(new Contact(name, formatBirthday(rawDate)));
                }
            }
        }
        return results;
    }

    private static boolean isDecemberBirthday(String rawDate) {
        if (rawDate == null) {
            return false;
        }
        String date = rawDate.trim();
        if (date.isEmpty()) {
            return false;
        }
        if (date.length() == 10 && date.charAt(4) == '-' && date.charAt(7) == '-') {
            return "12".equals(date.substring(5, 7));
        }
        if (date.length() == 7 && date.startsWith("--") && date.charAt(4) == '-') {
            return "12".equals(date.substring(2, 4));
        }
        if (date.length() == 10 && date.charAt(2) == '.' && date.charAt(5) == '.') {
            return "12".equals(date.substring(3, 5));
        }
        return date.contains(".12.") || date.contains("-12-");
    }

    private static String formatBirthday(String rawDate) {
        if (rawDate == null) {
            return "";
        }
        String date = rawDate.trim();
        if (date.length() == 10 && date.charAt(4) == '-' && date.charAt(7) == '-') {
            String year = date.substring(0, 4);
            String month = date.substring(5, 7);
            String day = date.substring(8, 10);
            return day + "." + month + "." + year;
        }
        if (date.length() == 7 && date.startsWith("--") && date.charAt(4) == '-') {
            String month = date.substring(2, 4);
            String day = date.substring(5, 7);
            return day + "." + month;
        }
        return date;
    }
}
