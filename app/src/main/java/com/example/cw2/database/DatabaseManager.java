package com.example.cw2.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/* assists with database inputs */
public class DatabaseManager {

    private DatabaseHelper databaseHelper;
    private final Context context;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public void open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    public void insert(String locationTitle, String locationAddress, String locationDescription, String locationReminder) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.locationTitle, locationTitle);
        contentValue.put(DatabaseHelper.locationAddress, locationAddress);
        contentValue.put(DatabaseHelper.locationDescription, locationDescription);
        contentValue.put(DatabaseHelper.locationReminder, locationReminder);


        database.insert(DatabaseHelper.tableName, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] {DatabaseHelper._ID, DatabaseHelper.locationTitle, DatabaseHelper.locationAddress, DatabaseHelper.locationDescription, DatabaseHelper.locationReminder};
        Cursor cursor = database.query(DatabaseHelper.tableName, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(String id, String locationTitle, String locationAddress, String locationDescription, String locationReminder) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.locationTitle, locationTitle);
        contentValues.put(DatabaseHelper.locationAddress, locationAddress);
        contentValues.put(DatabaseHelper.locationDescription, locationDescription);
        contentValues.put(DatabaseHelper.locationReminder, locationReminder);


        return database.update(DatabaseHelper.tableName, contentValues, DatabaseHelper._ID + " = " + id, null);
    }

    public void delete(String id) {
        database.delete(DatabaseHelper.tableName, DatabaseHelper._ID + "=" + id, null);
    }

}