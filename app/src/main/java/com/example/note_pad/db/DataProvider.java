package com.example.note_pad.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
* ContentProvider Class manages access to a structured set of data.
* It encapsulates the data and provide mechanisms for defining data security.
* */

public class DataProvider extends ContentProvider {
    private static final int DATA = 100;
    private static final int DATA_ID = 101;
    private static final String LOG_TAG = DataProvider.class.getSimpleName();
    public DataHelper helper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(DataContact.CONTENT_AUTHORITY,DataContact.PATH_DATA, DATA);
        sUriMatcher.addURI(DataContact.CONTENT_AUTHORITY, DataContact.PATH_DATA + "/#", DATA_ID);
    }
    @Override
    public boolean onCreate() {
        helper = new DataHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = helper.getReadableDatabase();


        Cursor cursor = null;


        int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:

                cursor = database.query(DataContact.DataEntity.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DATA_ID:

                selection = DataContact.DataEntity._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(DataContact.DataEntity.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:
                return DataContact.DataEntity.CONTENT_LIST_TYPE;
            case DATA_ID:
                return DataContact.DataEntity.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:
                return insertD(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:
                rowsDeleted = database.delete(DataContact.DataEntity.TABLE_NAME, selection, selectionArgs);
                break;
            case DATA_ID:
                selection = DataContact.DataEntity._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted =  database.delete(DataContact.DataEntity.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:
                return updateD(uri, contentValues, selection, selectionArgs);
            case DATA_ID:

                selection = DataContact.DataEntity._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateD(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertD(Uri uri, ContentValues values){

        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(DataContact.DataEntity.TABLE_NAME,null,values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateD(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsUpdated = db.update(DataContact.DataEntity.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
