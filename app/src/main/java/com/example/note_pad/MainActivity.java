package com.example.note_pad;


import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.note_pad.adapter.CustomeCursorAdapter;
import com.example.note_pad.db.DataContact;
import com.example.note_pad.db.DataHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DATA_LOADER = 0;
    private FloatingActionButton fab;
    private DataHelper helper;
    private SQLiteDatabase db;
    public ListView lv;
    public CustomeCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.floatingActionButton);
        lv = findViewById(R.id.lv_result);
        helper = new DataHelper(this);
        cursorAdapter = new CustomeCursorAdapter(this, null);

        //Floating action button for add data
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
            }
        });

        //ListView on item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                Uri dataUri = ContentUris.withAppendedId(DataContact.FINAL_CONTENT_URI, l);
                intent.setData(dataUri);
                startActivity(intent);
            }
        });


        lv.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(DATA_LOADER, null, MainActivity.this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {DataContact.DataEntity._id,
                DataContact.DataEntity.TITLE,
                DataContact.DataEntity.DESCRIPTION};
        return new CursorLoader(this,
                DataContact.FINAL_CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}