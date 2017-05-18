package com.sensei.easycalc.core;

import android.util.Log;

import com.sensei.easycalc.MainActivity;
import com.sensei.easycalc.R;
import com.sensei.easycalc.dao.DatabaseHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ExpressionController {

    private static final String TAG = "ExpressionController";

    private StringBuilder expression = null;
    private Lexer         lexer      = null;
    private Evaluator     evaluator  = null;
    private MainActivity  activity   = null;

    private static String   CMD_EQUALS = null;
    private static String   CMD_CLEAR  = null;
    private static String   CMD_DELETE = null;
    private static String[] CMDS       = null;

    public ExpressionController(MainActivity activity ) {
        this.activity = activity;
        createComponents();
    }

    private void createComponents() {
        expression = new StringBuilder();
        lexer = new Lexer( expression.toString(), activity );
        evaluator = new Evaluator( activity );

        CMD_EQUALS = activity.getString( R.string.equals );
        CMD_CLEAR  = activity.getString( R.string.clear  );
        CMD_DELETE = activity.getString( R.string.delete );
        CMDS = new String[]{ CMD_EQUALS, CMD_CLEAR, CMD_DELETE };
    }

    private boolean isCommandInput( String input ) {
        for( String s : CMDS ) {
            if( input.equals( s ) ) {
                return true ;
            }
        }
        return false ;
    }

    private void processCommand( String cmd ) {
        if( cmd.equals( CMD_CLEAR ) ) {
            expression.delete( 0, expression.length() );
            refreshOutput();
        }
        else if( cmd.equals( CMD_DELETE ) ) {
            try {
                expression.delete( expression.length()-1, expression.length() );
                refreshOutput();
            }
            catch( StringIndexOutOfBoundsException e ) {
                // do nothing
            }
        }
        else if( cmd.equals( CMD_EQUALS ) ) {
            saveAnswerToHistory();
            outputAnswerOnExpressionView();
        }
    }

    private void saveAnswerToHistory() {
        Log.d( TAG, getDisplayableAnswer() );
        Log.d( TAG, getDisplayableExpression() );
        if( ! ( getDisplayableAnswer().equals( "" ) || getDisplayableExpression().equals( "" ) ) ) {
            String ansCheck = getDisplayableAnswer().replace( "-", "–" );
            String exprCheck = getDisplayableExpression().trim().replace( "\\s", "" );
            if( !( ansCheck.equals( exprCheck ) ) ) {
                DatabaseHelper.getInstance().addTransactionToDatabase(
                        getDisplayableExpression(),
                        getDisplayableAnswer() );
            }
        }
    }

    private void outputAnswerOnExpressionView() {
        expression = new StringBuilder( getDisplayableAnswer() );
        refreshOutput();
    }

    private void refreshOutput() {
        String expr = getDisplayableExpression();
        String ans = getDisplayableAnswer();
        activity.showExpression( expr );
        activity.showAnswer( ans );
    }

    private String getDisplayableAnswer() {
        BigDecimal answer = getAnswer();
        if( answer == null ) {
            return "";
        }
        return answer.toPlainString();
    }

    private String getDisplayableExpression() {
        lexer.reset( expression.toString() );
        ArrayList<Token> tokens = lexer.getAllTokens();

        String s = "";

        for( Token t : tokens ) {
            s += " " + t.getTokenValue();
        }
        return s;
    }

    public BigDecimal getAnswer() {
        lexer.reset( expression.toString() );
        try {
            return evaluator.evaluate( lexer );
        } catch( Exception ex ) {
            Log.e( TAG, ex.getMessage() );
            return null;
        }
    }

    public void updateInput( String inputEntered ) {
        if( isCommandInput( inputEntered ) ) {
            processCommand( inputEntered ) ;
        }
        else {
            appendToExpression( inputEntered ) ;
            refreshOutput();
        }
    }

    private void appendToExpression( String inputEntered ) {
        if( inputEntered.equals( lexer.sqrt+"" ) ) {
            expression.append( inputEntered );
            expression.append( lexer.lbracket );
        }
        else {
            expression.append( inputEntered );
        }
    }

}
