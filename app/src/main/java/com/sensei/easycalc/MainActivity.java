package com.sensei.easycalc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sensei.easycalc.core.ExpressionController;
import com.sensei.easycalc.dao.DatabaseHelper;
import com.sensei.easycalc.ui.adapter.BottomViewPagerAdapter;
import com.sensei.easycalc.util.LocaleUtil;

import java.math.BigDecimal;

import me.grantland.widget.AutofitHelper;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    private TextView expressionView = null;
    private TextView answerView = null;
    private TextView memoryView = null;
    private ExpressionController controller = null;

    private BigDecimal memory = null;

    private ViewPager pager = null;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        DatabaseHelper.createInstance( this );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        initializeComponents();
        setUpViewPager();
    }

    private void setUpViewPager() {
        pager = (ViewPager)findViewById( R.id.viewPager );
        BottomViewPagerAdapter adapter = new BottomViewPagerAdapter( getSupportFragmentManager(), this );
        pager.setAdapter( adapter );
        pager.addOnPageChangeListener( adapter );
        pager.setCurrentItem( 1 );
    }

    private void initializeComponents() {
        expressionView = (TextView)findViewById( R.id.exprView );
        AutofitHelper.create( expressionView );
        answerView = (TextView)findViewById( R.id.answerView );
        AutofitHelper.create( answerView );
        memoryView = (TextView)findViewById( R.id.memoryView );
        controller = new ExpressionController( this );

        memory = new BigDecimal( 0 );
    }

    @Override
    public void onBackPressed() {
        if( pager.getCurrentItem() == 1 ) {
            super.onBackPressed();
        }
        else {
            pager.setCurrentItem( 1 );
        }
    }

    public void showExpression( String str ) {
        expressionView.setText( LocaleUtil.convertToString( str, this ) );
    }

    public void showAnswer( String answer ) {
        answerView.setText( LocaleUtil.convertToString( answer, this ) );
    }

    public ExpressionController getController() {
        return controller;
    }

    private void vibrate() {
        Vibrator v = (Vibrator)this.getSystemService( VIBRATOR_SERVICE );
        v.vibrate( 25 );
    }

    public void onNonNumpadButtonClick(View view ) {
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

    public void onHistoryButtonClick( View view ) {
        vibrate();
        ImageButton b = (ImageButton)view;

        if( b.getTag().equals( getString( R.string.history ) ) ) {
            pager.setCurrentItem( 0 );
            b.setImageResource( R.drawable.calculator );
            b.setTag( getString( R.string.numpad ) );
        }
        else {
            pager.setCurrentItem( 1 );
            b.setTag( getString( R.string.history ) );
            b.setImageResource( R.drawable.history );
        }
    }

    public void animateHistoryButton( ImageButton b ) {
        if( pager.getCurrentItem() == 0 ) {
            b.setImageResource( R.drawable.calculator );
            b.setTag( getString( R.string.numpad ) );
        }
        else {
            b.setTag( getString( R.string.history ) );
            b.setImageResource( R.drawable.history );
        }
    }

    public void onHistoryDeleteButtonClick( View view ) {
        vibrate();
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.clear_history_prompt );
        builder.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper.getInstance().clearHistory();
                pager.getAdapter().notifyDataSetChanged();
            }
        } );
        builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialogInterface, int i ) {
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
            controller.updateInput( LocaleUtil.convertToString( memory.toPlainString(), this ) );
        }
    }

    private void refreshMemoryView() {
        if( memory == null || memory.equals( BigDecimal.ZERO ) ) {
            memoryView.setText( "" );
        } else {
            memoryView.setText( "MEM = " + LocaleUtil.convertToString( memory.toPlainString(), this ) );
        }
    }
}