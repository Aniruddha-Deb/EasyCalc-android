package com.sensei.easycalc.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import com.sensei.easycalc.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        addPreferencesFromResource( R.xml.preferences );
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        view.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
    }
}
