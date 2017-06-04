package com.sensei.easycalc.core;

import com.sensei.easycalc.MainActivity;
import com.sensei.easycalc.R;
import com.sensei.easycalc.dao.DatabaseHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.sensei.easycalc.core.Symbols.ADD;
import static com.sensei.easycalc.core.Symbols.COS;
import static com.sensei.easycalc.core.Symbols.DIVIDE;
import static com.sensei.easycalc.core.Symbols.LBRACKET;
import static com.sensei.easycalc.core.Symbols.MULTIPLY;
import static com.sensei.easycalc.core.Symbols.PI;
import static com.sensei.easycalc.core.Symbols.SIN;
import static com.sensei.easycalc.core.Symbols.SQRT;
import static com.sensei.easycalc.core.Symbols.SUBTRACT;
import static com.sensei.easycalc.core.Symbols.TAN;
import static com.sensei.easycalc.core.Symbols.symbol;

public class ExpressionController {

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
                lexer.reset( expression.toString() );
                ArrayList<Token> tokens = lexer.getAllTokens();
                Token lastToken = tokens.get( tokens.size()-1 );

                if( lastToken.getTokenType() == Token.NUMERIC ) {
                    expression.delete( expression.length() - 1, expression.length() );
                }
                else {
                    expression.delete( expression.length() - lastToken.getTokenValue().length(),
                                       expression.length() );
                }
                refreshOutput();
            }
            catch( ArrayIndexOutOfBoundsException e ) {
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
            String ansCheck = getDisplayableAnswer().trim().replaceAll( "-", "–" );
            String exprCheck = getDisplayableExpression().trim().replaceAll( "\\s", "" );

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

    public void refreshOutput() {
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
        else if( answer.equals( BigDecimal.ZERO ) ) {
            return "0";
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
                inputEntered.equals( symbol( LBRACKET ) );
    }

    public void replaceInput( String inputEntered ) {
        if( !( isOperandInput( inputEntered ) || isSubOperandInput( inputEntered ) ) && expression.length() != 0) {
            String prevOp = expression.charAt( expression.length()-1 ) + "";
            if( !( isOperandInput( prevOp ) || isSubOperandInput( prevOp ) ) ) {
                expression = new StringBuilder( inputEntered );
                refreshOutput();
            }
            else {
                updateInput( inputEntered );
            }
        }
        else {
            updateInput( inputEntered );
        }
        reset = true;
    }

    private boolean isConstantInput( String inputEntered ) {
        return inputEntered.equals( symbol( PI ) );
    }

    public void updateInput( String inputEntered ) {
        if( isCommandInput( inputEntered ) ) {
            processCommand( inputEntered ) ;
        }
        else {

            if( expression.length() != 0 ) {

                lexer.reset( expression.toString() );
                ArrayList<Token> tokens = lexer.getAllTokens();
                String val = tokens.get( tokens.size()-1 ).getTokenValue();

                if( !( isOperandInput( inputEntered ) || isSubOperandInput( inputEntered ) )
                        && reset ) {
                    expression = new StringBuilder( inputEntered );
                    refreshOutput();
                }
                else if( isSubOperandInput( inputEntered ) || isConstantInput( inputEntered ) || isTrigonometryInput( inputEntered ) ) {
                    if( !( isOperandInput( val ) || isSubOperandInput( val ) || isTrigonometryInput( val ) ) ) {
                        expression.append( symbol( MULTIPLY ) );
                    }
                    expression.append( inputEntered );
                    if( isTrigonometryInput( inputEntered ) ) {
                        expression.append( symbol( LBRACKET ) );
                    }
                    refreshOutput();
                }
                else {
                    if( isConstantInput( val ) && isCharacterInput( inputEntered ) ) {
                        expression.append( symbol( MULTIPLY ) );
                    }
                    expression.append( inputEntered );
                    refreshOutput();
                }
            }
            else {
                expression.append( inputEntered );
                if( isTrigonometryInput( inputEntered ) ) {
                    expression.append( symbol( LBRACKET ) );
                }
                refreshOutput();
            }
            reset = false;
        }
    }

    private boolean isCharacterInput( String inputEntered ) {
        return Character.isDigit( inputEntered.charAt( 0 ) ) ;
    }

    private boolean isTrigonometryInput( String inputEntered ) {
        return inputEntered.equals( symbol( SIN ) ) ||
               inputEntered.equals( symbol( COS ) ) ||
               inputEntered.equals( symbol( TAN ) ) ;
    }
}
