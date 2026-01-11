package com.example.providerapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import java.util.Objects;

public class StudentContentProvider extends ContentProvider {

    public static final String AUTHORITY = "site.sunmeat.helloworld.provider";
    private static final String PATH_STUDENTS = "students";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_STUDENTS);

    private static final int CODE_STUDENTS = 100;
    private static final int CODE_STUDENT_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_STUDENTS, CODE_STUDENTS);
        uriMatcher.addURI(AUTHORITY, PATH_STUDENTS + "/#", CODE_STUDENT_ID);
    }

    private AppDatabase database;

    @Override
    public boolean onCreate() {
        database = Room.databaseBuilder(
                        Objects.requireNonNull(getContext()).getApplicationContext(),
                        AppDatabase.class,
                        "student_database"
                )
                .allowMainThreadQueries()   // For demo only; avoid main-thread queries in production.
                .build();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder
    ) {
        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case CODE_STUDENTS:
                cursor = database.studentDao().getAllStudentsBlocking();
                break;

            case CODE_STUDENT_ID:
                long id = ContentUris.parseId(uri);
                cursor = database.studentDao().getStudentByIdBlocking(id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri
            );
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case CODE_STUDENTS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH_STUDENTS;

            case CODE_STUDENT_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH_STUDENTS;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("Insert not implemented");
    }

    @Override
    public int delete(
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs
    ) {
        throw new UnsupportedOperationException("Delete not implemented");
    }

    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs
    ) {
        throw new UnsupportedOperationException("Update not implemented");
    }
}

