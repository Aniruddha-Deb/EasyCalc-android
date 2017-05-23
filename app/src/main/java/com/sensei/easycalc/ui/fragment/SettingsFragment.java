package com.sensei.easycalc.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.sensei.easycalc.R;
import com.sensei.easycalc.SettingsActivity;

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

        final ListPreference list = (ListPreference )findPreference( "locale" );
        list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange( Preference preference, Object newValue ) {
                String locale = (String)newValue;
                ((SettingsActivity)getActivity()).setLocale( locale );
                Intent i = new Intent( getActivity(), SettingsActivity.class );
                startActivity( i );
                getActivity().finish();
                return true;
            }
        });
    }
}
