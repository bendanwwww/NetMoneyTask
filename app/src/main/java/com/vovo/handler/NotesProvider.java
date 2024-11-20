package com.vovo.handler;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class NotesProvider extends ContentProvider {
    private static final int NOTES = 100;
    private static final int NOTE_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SQLiteDatabase database;

    static {
        uriMatcher.addURI(NotesContract.AUTHORITY, "notes", NOTES);
        uriMatcher.addURI(NotesContract.AUTHORITY, "notes/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        // 创建数据库帮助器
        DatabaseHelper helper = new DatabaseHelper(getContext());
        database = helper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                // 查询所有数据
                cursor = database.query(NotesContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTE_ID:
                // 查询单条数据
                String id = uri.getLastPathSegment();
                cursor = database.query(NotesContract.TABLE_NAME, projection, NotesContract.COLUMN_ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                id = database.insert(NotesContract.TABLE_NAME, null, values);
                if (id > 0) {
                    Uri returnUri = Uri.withAppendedPath(NotesContract.CONTENT_URI, String.valueOf(id));
                    getContext().getContentResolver().notifyChange(returnUri, null);
                    return returnUri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                count = database.update(NotesContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                count = database.update(NotesContract.TABLE_NAME, values, NotesContract.COLUMN_ID + "=?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                count = database.delete(NotesContract.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                count = database.delete(NotesContract.TABLE_NAME, NotesContract.COLUMN_ID + "=?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case NOTES:
                return "vnd.android.cursor.dir/vnd.com.example.myapp.notes";
            case NOTE_ID:
                return "vnd.android.cursor.item/vnd.com.example.myapp.notes";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "notes.db";
        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_TABLE = "CREATE TABLE " + NotesContract.TABLE_NAME + " (" +
                NotesContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesContract.COLUMN_TEXT + " TEXT NOT NULL);";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NotesContract.TABLE_NAME);
            onCreate(db);
        }
    }
}

