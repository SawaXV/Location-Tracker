package com.example.cw2.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;


/* allow data to be used outside of app */
public class GeotrackerProvider extends ContentProvider {

    private DatabaseHelper databaseHelper;
    static final String PROVIDER_NAME = "com.example.cw2.database.GeotrackerProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/LOCATIONS";
    static final Uri CONTENT_URI = Uri.parse(URL);
    public static final int LOCATIONS = 1;
    public static final int LOCATION_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(PROVIDER_NAME, "LOCATIONS", LOCATIONS);
        sURIMatcher.addURI(PROVIDER_NAME, "LOCATIONS" + "/#", LOCATION_ID);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseHelper.tableName);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LOCATION_ID:
                queryBuilder.appendWhere(DatabaseHelper._ID + "="
                        + uri.getLastPathSegment());
                break;
            case LOCATIONS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        Cursor cursor = queryBuilder.query(databaseHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case LOCATIONS:
                return "vnd.android.cursor.dir/vnd.example.LOCATIONS";
            case LOCATION_ID:
                return "vnd.android.cursor.item/vnd.example.LOCATIONS";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = database.insert(DatabaseHelper.tableName, "", values);

        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Could not add URI: " + uri);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (sURIMatcher.match(uri) == 1) {
            count = database.delete(DatabaseHelper.tableName, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (sURIMatcher.match(uri) == 1) {
            count = database.update(DatabaseHelper.tableName, values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }
}