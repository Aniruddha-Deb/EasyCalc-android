package com.sensei.easycalc.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
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

        final EditTextPreference decimalAccuracy = (EditTextPreference)findPreference( "scale" );
        decimalAccuracy.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange( Preference preference, Object newValue ) {
                if( Integer.parseInt( (String)newValue ) > 50 ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                    builder.setMessage( R.string.accuracy_prompt );
                    builder.setPositiveButton( getString( R.string.ok ), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // continue
                        }
                    } );
                    builder.create().show();
                }
                return true;
            }
        } );

    }
}
