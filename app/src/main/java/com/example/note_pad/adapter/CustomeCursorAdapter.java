package com.example.note_pad.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.note_pad.R;
import com.example.note_pad.db.DataContact;
/*
* Custom Adapter class for show data in list view
*/
public class CustomeCursorAdapter extends CursorAdapter {
    public CustomeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.custome_list_view,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_title = view.findViewById(R.id.txt_title_lv);
        TextView tv_des = view.findViewById(R.id.txt_desc_lv);
        String title = cursor.getString(cursor.getColumnIndex(DataContact.DataEntity.TITLE));
        String desc = cursor.getString(cursor.getColumnIndex(DataContact.DataEntity.DESCRIPTION));
        tv_title.setText(title);
        tv_des.setText(desc);
    }
}
