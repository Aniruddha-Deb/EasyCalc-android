package com.sensei.easycalc.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sensei.easycalc.R;
import com.sensei.easycalc.util.LocaleUtil;

public class HistoryCursorAdapter extends CursorAdapter {

    private static final String TAG = "HistoryCursorAdapter";

    private LayoutInflater layoutInflater = null;
    private Context c = null;

    public HistoryCursorAdapter( Context context, Cursor c ) {
        super( context, c );
        this.c = context;
        layoutInflater = LayoutInflater.from( context );
    }

    @Override
    public View newView( Context context, Cursor cursor, ViewGroup viewGroup ) {
        return layoutInflater.inflate( R.layout.history_list_item, viewGroup, false );
    }

    @Override
    public void bindView( View view, Context context, Cursor cursor ) {
        String exprString = cursor.getString( cursor.getColumnIndexOrThrow( "expression" ) );
        String expression = LocaleUtil.convertToString( exprString, c );
        String answer =
                LocaleUtil.convertToString(
                        cursor.getString( cursor.getColumnIndexOrThrow( "answer" ) ), c );
        String output = expression + " = " + answer;

        TextView outputView = (TextView)view.findViewById( R.id.historyItem );
        outputView.setText( output );
    }
}
