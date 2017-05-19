package com.sensei.easycalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        setSupportActionBar( (Toolbar)findViewById( R.id.my_toolbar ) );
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled( true );
        setTitle( "Settings" );
        getFragmentManager().beginTransaction()
                .add( R.id.settings, new SettingsFragment() )
                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
    }
}
