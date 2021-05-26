package com.example.disabledroid.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseLocation extends SQLiteOpenHelper {

    private static final String DB_NAME = "LOCATION.db", TABLE_NAME = "LOCATION";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (LOCATION TEXT PRIMARY KEY NOT NULL)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseLocation(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean insertLocation(String location) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("LOCATION", location);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        return result == -1 ? false : true;
    }

    public boolean deleteLocation(String location) {
        sqLiteDatabase = this.getWritableDatabase();
        long result = sqLiteDatabase.delete(TABLE_NAME, "LOCATION = ?", new String[]{location});
        return result == -1 ? false : true;
    }

    public Cursor getLocation() {
        sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }
}


