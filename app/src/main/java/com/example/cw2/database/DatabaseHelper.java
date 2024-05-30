package com.example.cw2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName = "GPS DATABASE";
    public static final String tableName = "LOCATIONS";

    public static final String _ID = "_id";
    public static final String locationTitle = "locationTitle";
    public static final String locationAddress = "locationAddress";
    public static final String locationDescription = "locationDescription";
    public static final String locationReminder = "locationReminder";

    private static final String CREATE_TABLE = "create table " + tableName + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            locationTitle + " TEXT NOT NULL, " +
            locationAddress + " TEXT NOT NULL, " +
            locationDescription + " TEXT, " +
            locationReminder + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}