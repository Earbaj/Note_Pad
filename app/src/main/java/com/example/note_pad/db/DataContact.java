package com.example.note_pad.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DataContact {
    private DataContact() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.note_pad";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DATA = "mydb";
    public static final Uri FINAL_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DATA);

    public static class DataEntity implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        public static final String _id = BaseColumns._ID;
        public static final String TABLE_NAME = "mydb";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
    }
}
