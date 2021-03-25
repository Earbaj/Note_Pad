package com.example.note_pad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.note_pad.db.DataContact;
import com.example.note_pad.db.DataHelper;

public class InsertActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_DATA_LOADER = 0;
    private EditText etTitle;
    private EditText etDes;
    private DataHelper helper;
    private SQLiteDatabase db;
    private ContentValues values;
    private Uri currUri;
    private Button btn_sv;
    private boolean mDataHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDataHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        etTitle = findViewById(R.id.ettitle);
        etDes = findViewById(R.id.etdescription);
        btn_sv = findViewById(R.id.btn_save);
        //create database helper object
        helper = new DataHelper(this);
        db = helper.getWritableDatabase();
        values = new ContentValues();
        //set touch listener for edit text
        etTitle.setOnTouchListener(mTouchListener);
        etDes.setOnTouchListener(mTouchListener);
        // Action bar for back
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        currUri = intent.getData();
        // check if uri is not null than set the title Edit otherwise set it Add
        if (currUri != null) {
            setTitle("Edit Data");
            btn_sv.setText("Update");
            getLoaderManager().initLoader(EXISTING_DATA_LOADER, null, this);
        } else {
            setTitle("Add Data");
        }

    }

    // Button for save Data && also delete
    public void Save(View view) {
        String title = etTitle.getText().toString().trim();
        String description = etDes.getText().toString().trim();
        if (currUri != null && TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            return;
        }
        values.put(DataContact.DataEntity.TITLE, title);
        values.put(DataContact.DataEntity.DESCRIPTION, description);
        if (currUri == null) {
            Uri uri = getContentResolver().insert(DataContact.FINAL_CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(getApplicationContext(), "Data failed to insert", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Data Inserted",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            getContentResolver().update(currUri, values, null, null);
            Toast.makeText(getApplicationContext(), "Data Updated",
                    Toast.LENGTH_SHORT).show();
        }

    }

    // Button for end the activity
    public void Cancel(View view) {
        Intent intent = new Intent(InsertActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_data:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                final Intent upIntent = new Intent(this, MainActivity.class);
                if (!mDataHasChanged) {
                    NavUtils.navigateUpTo(InsertActivity.this, upIntent);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpTo(InsertActivity.this, upIntent);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the data hasn't changed, continue with handling back button press
        if (!mDataHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {DataContact.DataEntity._id,
                DataContact.DataEntity.TITLE,
                DataContact.DataEntity.DESCRIPTION};
        return new CursorLoader(this,
                currUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(DataContact.DataEntity.TITLE);
            int descrColumnIndex = cursor.getColumnIndex(DataContact.DataEntity.DESCRIPTION);
            String title = cursor.getString(titleColumnIndex);
            String descript = cursor.getString(descrColumnIndex);
            etTitle.setText(title);
            etDes.setText(descript);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        etTitle.setText("");
        etDes.setText("");
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteData();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteData() {
        // Only perform the delete if this is an existing data.
        if (currUri != null) {
            int rowsDeleted = getContentResolver().delete(currUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_data_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_data_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}