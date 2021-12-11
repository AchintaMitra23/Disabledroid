package com.example.disabledroid.SQLiteDatabase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseDayPlan extends SQLiteOpenHelper {

    private static final String DB_NAME = "DAY_PLAN.db", TABLE_NAME = "DAY_PLAN";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (DATE TEXT PRIMARY KEY NOT NULL, IDEA TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase sqLiteDatabase;

    public DatabaseDayPlan(Context context) {
        super(context, DB_NAME, null, VERSION);
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

    public boolean insertPlan(String date, String idea) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DATE", date);
        cv.put("IDEA", idea);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public boolean updatePlan(String date, String idea) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("IDEA", idea);
        long result = sqLiteDatabase.update(TABLE_NAME, cv, "DATE = ?", new String[]{date});
        return result != -1;
    }

    public boolean deletePlan(String date) {
        sqLiteDatabase = this.getWritableDatabase();
        long result = sqLiteDatabase.delete(TABLE_NAME, "DATE = ?", new String[]{date});
        return result != -1;
    }

    public Cursor getPlan() {
        sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    @SuppressLint("Recycle")
    public String getParticularPlan(String date) {
        sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor;
        String temp = null;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(date)) {
                temp = cursor.getString(1);
                break;
            }
        }
        return temp;
    }
}

