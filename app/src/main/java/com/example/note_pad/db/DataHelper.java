package com.example.note_pad.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notepaddata.db";

    public DataHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DataContact.DataEntity.TABLE_NAME + " (" +
                        DataContact.DataEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DataContact.DataEntity.TITLE + " TEXT," +
                        DataContact.DataEntity.DESCRIPTION + " INTEGER)";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
