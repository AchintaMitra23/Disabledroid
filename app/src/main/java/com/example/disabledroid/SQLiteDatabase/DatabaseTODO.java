package com.example.disabledroid.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.disabledroid.TodoActivity;

public class DatabaseTODO extends SQLiteOpenHelper {

    private static final String DB_NAME = "TODO.db", TABLE_NAME = "TODO";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (TASK TEXT PRIMARY KEY NOT NULL)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseTODO(Context context) {
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

    public boolean insertTask(String task) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("TASK", task);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        return result == -1 ? false : true;
    }

    public boolean updateTask(String oldTask, String newTask) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("TASK", newTask);
        long result = sqLiteDatabase.update(TABLE_NAME, cv, "TASK = ?", new String[]{oldTask});
        return result == -1 ? false : true;
    }

    public boolean deleteTask(String task) {
        sqLiteDatabase = this.getWritableDatabase();
        long result = sqLiteDatabase.delete(TABLE_NAME, "TASK = ?", new String[]{task});
        return result == -1 ? false : true;
    }

    public Cursor getTask() {
        sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }
}
