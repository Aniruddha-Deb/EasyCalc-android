package com.sensei.easycalc.core;

import com.sensei.easycalc.MainActivity;
import com.sensei.easycalc.R;
import com.sensei.easycalc.dao.DatabaseHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.sensei.easycalc.core.Symbols.ADD;
import static com.sensei.easycalc.core.Symbols.DIVIDE;
import static com.sensei.easycalc.core.Symbols.LBRACKET;
import static com.sensei.easycalc.core.Symbols.MULTIPLY;
import static com.sensei.easycalc.core.Symbols.SQRT;
import static com.sensei.easycalc.core.Symbols.SUBTRACT;
import static com.sensei.easycalc.core.Symbols.symbol;

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

    private boolean reset = false;

    public ExpressionController(MainActivity activity ) {
        this.activity = activity;
        createComponents();
    }

    private void createComponents() {
        expression = new StringBuilder();
        lexer = new Lexer( expression.toString() );
        evaluator = new Evaluator();

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
            reset = true;
            saveAnswerToHistory();
            outputAnswerOnExpressionView();
        }
    }

    private void saveAnswerToHistory() {
        if( ! ( getDisplayableAnswer().equals( "" ) || getDisplayableExpression().equals( "" ) ) ) {
            // replace whitespaces in expression and check whether they equal the answer. If yes,
            // then do not append to memory.
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
        lexer.reset( expression.toString() );
        try {
            evaluator.evaluate( lexer );
            expression = new StringBuilder( getDisplayableAnswer() );
            refreshOutput();
        } catch( Exception ex ) {
            activity.showError();
        }
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
            return null;
        }
    }

    private boolean isOperandInput( String inputEntered ) {
        return  inputEntered.equals( symbol( ADD ) ) ||
                inputEntered.equals( symbol( SUBTRACT ) ) ||
                inputEntered.equals( symbol( MULTIPLY ) ) ||
                inputEntered.equals( symbol( DIVIDE ) ) ;
    }

    private boolean isSubOperandInput( String inputEntered ) {
        return  inputEntered.equals( symbol( SQRT ) ) ||
                inputEntered.equals( symbol( LBRACKET ) ) ;
    }

    public void updateInput( String inputEntered ) {
        if( isCommandInput( inputEntered ) ) {
            processCommand( inputEntered ) ;
        }
        else {
            if( !(isOperandInput( inputEntered )) && reset ) {
                expression = new StringBuilder( inputEntered );
                refreshOutput();
            }
            else if( isSubOperandInput( inputEntered ) && expression.length() != 0 ) {
                String prevOp = expression.charAt( expression.length()-1 ) + "";
                if( !( isOperandInput( prevOp ) || isSubOperandInput( prevOp ) ) ) {
                    expression.append( symbol( MULTIPLY ) );
                    expression.append( inputEntered );
                }
                else {
                    expression.append( inputEntered );
                }
                refreshOutput();
            }
            else {
                expression.append( inputEntered );
                refreshOutput();
            }
            reset = false;
        }
    }
}
