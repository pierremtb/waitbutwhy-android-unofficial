package com.pierrejacquier.waitbutwhyunofficial.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WBWU.db";
    private static final int DATABASE_VERSION = 2;

    public static final String PERSON_TABLE_NAME = "read_posts";
    public static final String PERSON_COLUMN_ID = "_id";
    public static final String PERSON_COLUMN_LINK = "link";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PERSON_TABLE_NAME +
                        "(" + PERSON_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        PERSON_COLUMN_LINK + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PERSON_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertReadPost(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PERSON_COLUMN_LINK, link);

        db.insert(PERSON_TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PERSON_TABLE_NAME);
        return numRows;
    }

    public Integer deleteReadPost(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PERSON_TABLE_NAME,
                PERSON_COLUMN_LINK + " = ? ",
                new String[] { link });
    }

    public boolean isPostRead(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM " + PERSON_TABLE_NAME + " WHERE " +
                PERSON_COLUMN_LINK + "=?", new String[]{link});
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean toggleReadPost(String link) {
        if (isPostRead(link)) {
            deleteReadPost(link);
            return false;
        } else {
            insertReadPost(link);
            return true;
        }
    }
}