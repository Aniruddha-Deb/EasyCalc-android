package com.sensei.easycalc;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.sensei.easycalc.ui.fragment.SettingsFragment;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        setSupportActionBar( (Toolbar)findViewById( R.id.my_toolbar ) );
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled( true );
        ab.setDisplayShowHomeEnabled( true );
        setTitle( getString( R.string.settings ) );
        getFragmentManager().beginTransaction()
                .add( R.id.settings, new SettingsFragment() )
                .commit();
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask( this );
    }

    public void setLocale( String lang ) {
        Locale myLocale = new Locale( lang );
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
