package com.sensei.easycalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.sensei.easycalc.core.ExpressionController;
import com.sensei.easycalc.core.Symbols;
import com.sensei.easycalc.core.angle.AngleUnit;
import com.sensei.easycalc.dao.DatabaseHelper;
import com.sensei.easycalc.ui.adapter.BottomViewPagerAdapter;
import com.sensei.easycalc.util.LocaleUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;

import me.grantland.widget.AutofitHelper;

import static com.sensei.easycalc.core.Symbols.*;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = "MainActivity";

    private TextView expressionView = null;
    private TextView answerView = null;
    private TextView memoryView = null;
    private ExpressionController controller = null;

    private BigDecimal memory = null;

    private boolean vibrate = true;
    private SharedPreferences sharedPreferences = null;

    private ViewPager pager = null;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        loadPreferences();
        DatabaseHelper.createInstance( this );
        setUpConstants();
        setUpContentView();
        super.onCreate( savedInstanceState );
        initializeComponents();
        setUpViewPager();
    }

    private void loadPreferences() {
        setLocale( sharedPreferences.getString( "locale", "en" ) );
        memory = new BigDecimal( sharedPreferences.getString( "memory", "0" ) );
        vibrate = sharedPreferences.getBoolean( "vibrate", true );
    }

    public void setLocale( String lang ) {
        Locale myLocale = new Locale( lang );
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void setUpContentView() {
        boolean showMemoryButtons = sharedPreferences.getBoolean( "mem_buttons", true );
        if( showMemoryButtons ) {
            setContentView( R.layout.activity_main );
        }
        else {
            setContentView( R.layout.activity_main_sans_memory );
        }
    }

    private void setUpConstants() {
        HashMap<String, Object> symbols = new HashMap<>();
        symbols.put( ADD, getString( R.string.add ) );
        symbols.put( SUBTRACT, getString( R.string.subtract ) );
        symbols.put( MULTIPLY, getString( R.string.multiply ) );
        symbols.put( DIVIDE, getString( R.string.divide ) );
        symbols.put( SQRT, getString( R.string.sqrt ) );
        symbols.put( SQUARE, getString( R.string.square ) );
        symbols.put( SCALE, Integer.parseInt( sharedPreferences.getString( "scale", "10" ) ) );
        symbols.put( LBRACKET, getString( R.string.lbracket ) );
        symbols.put( RBRACKET, getString( R.string.rbracket ) );
        symbols.put( SIN, getString( R.string.sin ) );
        symbols.put( COS, getString( R.string.cos ) );
        symbols.put( TAN, getString( R.string.tan ) );
        symbols.put( PI, getString( R.string.pi ) );

        if( sharedPreferences.getString( ANGLE_UNIT, getString( R.string.rad ) )
                             .equals( getString( R.string.deg ) ) ) {
            symbols.put( ANGLE_UNIT, AngleUnit.DEGREES );
            SwitchButton s = (SwitchButton)findViewById( R.id.angleUnitSelector );
            s.setChecked( true );
        }
        else {
            symbols.put( ANGLE_UNIT, AngleUnit.RADIANS );
        }

        Symbols.setUpConstants( symbols );
    }

    private void setUpViewPager() {
        pager = (ViewPager)findViewById( R.id.viewPager );
        BottomViewPagerAdapter adapter = new BottomViewPagerAdapter( getSupportFragmentManager(), this );
        pager.setAdapter( adapter );
        pager.addOnPageChangeListener( adapter );
        pager.setCurrentItem( 1 );

        TabLayout tabs = (TabLayout) findViewById( R.id.viewPagerTab );
        tabs.setupWithViewPager( pager );
    }

    private void initializeComponents() {
        expressionView = (TextView)findViewById( R.id.exprView );
        AutofitHelper.create( expressionView );
        answerView = (TextView)findViewById( R.id.answerView );
        AutofitHelper.create( answerView );
        memoryView = (TextView)findViewById( R.id.memoryView );
        controller = new ExpressionController( this );

        ((ImageButton)findViewById( R.id.settingsButton )).setImageResource( R.drawable.settings );
        ((ImageButton)findViewById( R.id.piButton )).setImageResource( R.drawable.pi );
        refreshMemoryView();

    }

    @Override
    public void onBackPressed() {
        if( pager.getCurrentItem() == 1 ) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString( "memory", memory.toPlainString() );
            editor.apply();
            super.onBackPressed();
        }
        else {
            pager.setCurrentItem( 1 );
        }
    }

    public void showExpression( String str ) {
        if( expressionView.getTag().equals( "Dirty" ) ) {
            expressionView.setBackground( getResources().getDrawable( R.color.colorPrimaryDark ) );
            expressionView.setTag( "Clean" );
        }
        expressionView.setText( LocaleUtil.convertToString( str, this ) );
    }

    public void showAnswer( String answer ) {
        answerView.setText( LocaleUtil.convertToString( answer, this ) );
    }

    public void showError() {
        expressionView.setBackground( getResources().getDrawable( R.color.colorError ) );
        expressionView.setTag( "Dirty" );
    }

    public ExpressionController getController() {
        return controller;
    }

    public void vibrate() {
        if( vibrate ) {
            Vibrator v = ( Vibrator ) this.getSystemService( VIBRATOR_SERVICE );
            v.vibrate( 25 );
        }
    }

    public void onNonNumpadButtonClick( View view ) {
        vibrate();
        controller.updateInput( ((Button)view).getText().toString() );
    }

    public void onSevenButtonClick( View view ) {
        vibrate();
        controller.updateInput( "7" );
    }

    public void onEightButtonClick( View view ) {
        vibrate();
        controller.updateInput( "8" );
    }

    public void onNineButtonClick( View view ) {
        vibrate();
        controller.updateInput( "9" );
    }

    public void onFourButtonClick( View view ) {
        vibrate();
        controller.updateInput( "4" );
    }

    public void onFiveButtonClick( View view ) {
        vibrate();
        controller.updateInput( "5" );
    }

    public void onSixButtonClick( View view ) {
        vibrate();
        controller.updateInput( "6" );
    }

    public void onOneButtonClick( View view ) {
        vibrate();
        controller.updateInput( "1" );
    }

    public void onTwoButtonClick( View view ) {
        vibrate();
        controller.updateInput( "2" );
    }

    public void onThreeButtonClick( View view ) {
        vibrate();
        controller.updateInput( "3" );
    }

    public void onDecimalButtonClick( View view ) {
        vibrate();
        controller.updateInput( "." );
    }

    public void onZeroButtonClick( View view ) {
        vibrate();
        controller.updateInput( "0" );
    }

    public void onSettingsButtonClick( View view ) {
        vibrate();
        Intent intent = new Intent( this, SettingsActivity.class );
        startActivity( intent );
    }

    public void onPiButtonClick( View view ) {
        vibrate();
        controller.updateInput( getResources().getString( R.string.pi ) );
    }

    public void onUnitSwitchFlip( View view ) {
        SwitchButton s = (SwitchButton) view;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if( s.isChecked() ) {
            editor.putString( ANGLE_UNIT, getString( R.string.deg ) );
            setSymbol( ANGLE_UNIT, AngleUnit.DEGREES );
        }
        else {
            editor.putString( ANGLE_UNIT, getString( R.string.rad ) );
            setSymbol( ANGLE_UNIT, AngleUnit.RADIANS );
        }
        editor.commit();
    }

    public void onHistoryDeleteButtonClick(View view ) {
        vibrate();
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.clear_history_prompt );
        builder.setPositiveButton( getString( R.string.yes ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper.getInstance().clearHistory();
                pager.getAdapter().notifyDataSetChanged();
            }
        } );
        builder.setNegativeButton( getString( R.string.no ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        } );
        builder.create().show();
    }

    public void onMemPlusButtonClick( View view ) {
        vibrate();
        BigDecimal answer = controller.getAnswer();
        if( answer == null ) {
            Toast.makeText( this, R.string.memoryAddError, Toast.LENGTH_LONG ).show();
        }
        else if( memory == null ){
            memory = new BigDecimal( 0 );
            memory = memory.add( answer );
        }
        else {
            memory = memory.add( answer );
        }
        refreshMemoryView();
    }

    public void onMemMinusButtonClick( View view ) {
        vibrate();
        BigDecimal answer = controller.getAnswer();
        if( answer == null ) {
            Toast.makeText( this, R.string.memorySubtractError, Toast.LENGTH_LONG ).show();
        }
        else if( memory == null ){
            memory = new BigDecimal( 0 );
            memory = memory.subtract( answer );
        }
        else {
            memory = memory.subtract( answer );
        }
        refreshMemoryView();
    }

    public void onMemClearButtonClick( View view ) {
        vibrate();
        memory = null;
        refreshMemoryView();
    }

    public void onMemRecallButtonClick( View view ) {
        vibrate();
        if( memory != null ) {
            controller.replaceInput( LocaleUtil.convertToString( memory.toPlainString(), this ) );
        }
    }

    private void refreshMemoryView() {
        if( memory == null || memory.equals( BigDecimal.ZERO ) ) {
            memoryView.setText( "" );
        } else {
            memoryView.setText( getString( R.string.mem_holder, LocaleUtil.convertToString( memory.toPlainString(), this ) ) );
        }
    }
}