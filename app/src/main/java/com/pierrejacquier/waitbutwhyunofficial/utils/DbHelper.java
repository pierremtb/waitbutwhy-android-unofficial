package com.pierrejacquier.waitbutwhyunofficial.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pierrejacquier.waitbutwhyunofficial.data.PostItem;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WBWU.db";
    private static final int DATABASE_VERSION = 13;

    public static final String KEY_ID = "_id";
    public static final String KEY_LINK = "link";
    public static final String KEY_TITLE = "title";
    public static final String KEY_THUMBNAIL_LINK = "thumbnail_link";

    public static final String READ_POSTS_TABLE_NAME = "read_posts";
    public static final String BOOKMARKED_POSTS_TABLE_NAME = "bookmarked_posts";

    public static final String CREATE_TABLE_READ_POSTS =
            "CREATE TABLE " + READ_POSTS_TABLE_NAME +
            "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_LINK + " TEXT" +
            ")";

    public static final String CREATE_TABLE_BOOKMARKED_POSTS =
            "CREATE TABLE " + BOOKMARKED_POSTS_TABLE_NAME +
            "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_TITLE + " TEXT," +
                KEY_LINK + " TEXT," +
                KEY_THUMBNAIL_LINK + " TEXT" +
            ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_READ_POSTS);
        db.execSQL(CREATE_TABLE_BOOKMARKED_POSTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + READ_POSTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BOOKMARKED_POSTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertReadPost(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_LINK, link);

        db.insert(READ_POSTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteReadPost(String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(READ_POSTS_TABLE_NAME,
                KEY_LINK + " = ? ",
                new String[] { link });
    }

    public boolean isPostRead(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM " + READ_POSTS_TABLE_NAME + " WHERE " +
                KEY_LINK + "=?", new String[]{link});
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

    public List<PostItem> getBookmarkedPosts() {
        List<PostItem> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + BOOKMARKED_POSTS_TABLE_NAME, null);
        if (cursor .moveToFirst()) {
            while (!cursor.isAfterLast()) {
                PostItem post = new PostItem()
                        .withTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)))
                        .withLink(cursor.getString(cursor.getColumnIndex(KEY_LINK)))
                        .withBookmarked(true)
                        .withThumbnailLink(cursor.getString(cursor.getColumnIndex(KEY_THUMBNAIL_LINK)));
                posts.add(post);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return posts;
    }


    public boolean insertBookmarkedPost(PostItem post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_LINK, post.getLink());
        contentValues.put(KEY_TITLE, post.getTitle());
        contentValues.put(KEY_THUMBNAIL_LINK, post.getThumbnailLink());

        db.insert(BOOKMARKED_POSTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteBookmarkedPost(PostItem post) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(BOOKMARKED_POSTS_TABLE_NAME,
                KEY_LINK + " = ? ",
                new String[] { post.getLink() });
    }

    public boolean isPostBookmarked(PostItem post) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM " + BOOKMARKED_POSTS_TABLE_NAME + " WHERE " +
                KEY_LINK + "=?", new String[]{ post.getLink() });
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean toggleBookmarkedPost(PostItem post) {
        if (isPostBookmarked(post)) {
            deleteBookmarkedPost(post);
            return false;
        } else {
            insertBookmarkedPost(post);
            return true;
        }
    }
}