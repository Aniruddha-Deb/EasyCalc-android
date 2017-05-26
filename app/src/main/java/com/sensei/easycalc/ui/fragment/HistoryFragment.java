package com.sensei.easycalc.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sensei.easycalc.MainActivity;
import com.sensei.easycalc.R;
import com.sensei.easycalc.dao.DatabaseHelper;
import com.sensei.easycalc.ui.adapter.HistoryCursorAdapter;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private HistoryCursorAdapter cursorAdapter = null;
    private MainActivity activity = null;

    public HistoryFragment withActivity(MainActivity activity ) {
        this.activity = activity;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.history_layout, container, false );
    }

    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState ) {
        ListView historyView = (ListView)view.findViewById( R.id.historyList );

        historyView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView< ? > adapterView, View view, int i, long l) {
                activity.vibrate();
                String transaction = ((TextView)view.findViewById( R.id.historyItem )).getText().toString();
                String answer = transaction.split( " = " )[1];
                if( activity.getController() != null ) {
                    activity.getController().updateInput( answer );
                }
            }
        } );

        historyView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView< ? > adapterView, View view, int i, long l) {
                String transaction = ((TextView)view.findViewById( R.id.historyItem )).getText().toString();
                String expression = transaction.split( " = " )[0];
                if( activity.getController() != null ) {
                    activity.getController().updateInput( getResources().getString( R.string.clear ) );
                    activity.getController().updateInput( expression );
                }
                return true;
            }
        } );
        cursorAdapter = new HistoryCursorAdapter(
                            getActivity(),
                            DatabaseHelper.getInstance().getHistoryCursor() );
        historyView.setAdapter( cursorAdapter );
        super.onViewCreated( view, savedInstanceState );
    }

    public void refreshData() {
        if( cursorAdapter != null ) {
            Log.d( TAG, "Notifying cursorAdapter that dataset changed" );
            cursorAdapter.swapCursor( DatabaseHelper.getInstance().getHistoryCursor() );
        }
        else {
            Log.d( TAG, "CursorAdapter is null" );
        }
    }
}
