package com.sensei.easycalc.core;

import com.sensei.easycalc.MainActivity;
import com.sensei.easycalc.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Evaluator {

    private String add      = null;
    private String subtract = null;
    private String multiply = null;
    private String divide   = null;
    private String lbracket = null;
    private String rbracket = null;
    private String sqrt     = null;

    public Evaluator( MainActivity activity ) {
        add      = activity.getString( R.string.add );
        subtract = activity.getString( R.string.subtract );
        multiply = activity.getString( R.string.multiply );
        divide   = activity.getString( R.string.divide );
        lbracket = activity.getString( R.string.lbracket );
        rbracket = activity.getString( R.string.rbracket );
        sqrt     = activity.getString( R.string.sqrt );
    }

    public BigDecimal evaluate( Lexer lexer ) throws Exception{
        BigDecimal value;
        value = calculateASOp( lexer );
        value = value.setScale( 10, RoundingMode.HALF_UP );
        Token token = lexer.getNextToken();

        while ( token != null ) {

            if( token.getTokenValue().equals( add ) ||
                token.getTokenValue().equals( subtract ) ) {

                BigDecimal rightHandValue = calculateASOp( lexer );
                if( token.getTokenValue().equals( add ) ) {
                    value = value.add( rightHandValue );
                }
                else if( token.getTokenValue().equals( subtract ) ) {
                    value = value.subtract( rightHandValue );
                }
                token = lexer.getNextToken() ;
            }
            else {
                lexer.pushBackToken( token );
                break ;
            }
        }
        return value.stripTrailingZeros();
    }

    private BigDecimal calculateASOp( Lexer lexer ) throws Exception{
        BigDecimal value ;
        value = calculateMDOp( lexer );
        value = value.setScale( 10, RoundingMode.HALF_UP );
        Token token = lexer.getNextToken();

        while ( token != null ) {

            if( token.getTokenValue().equals( multiply ) ||
                    token.getTokenValue().equals( divide ) ) {

                BigDecimal rightHandValue = calculateMDOp( lexer );
                if( token.getTokenValue().equals( multiply ) ) {
                    value = value.multiply( rightHandValue );
                }
                else if( token.getTokenValue().equals( divide ) ) {
                    value = value.divide( rightHandValue, BigDecimal.ROUND_HALF_UP );
                }
                token = lexer.getNextToken() ;
            }
            else {
                lexer.pushBackToken( token );
                break ;
            }
        }
        return value;
    }

    public static BigDecimal sqrt( BigDecimal A ) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(A.doubleValue()));
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, BigDecimal.ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide( BigDecimal.valueOf( 2 ), BigDecimal.ROUND_HALF_UP);

        }
        return x1;
    }

    private BigDecimal calculateMDOp( Lexer lexer ) throws Exception{
        BigDecimal value = new BigDecimal( "0" );
        value = value.setScale( 10, RoundingMode.HALF_UP );
        Token token = lexer.getNextToken();
        int   sign = 1 ;

        if( token == null ) {
            throw new Exception( "Incomplete expression" ) ;
        }

        if( token.getTokenValue().equals( subtract ) ) {
            // We have a unary negation operator.
            sign = -1 ;
            token = lexer.getNextToken() ;
        }
        else if( token.getTokenValue().equals( add ) ) {
            // We have a unary positive operator.
            sign = 1 ;
            token = lexer.getNextToken() ;
        }

        if( token == null ) {
            throw new Exception( "Incomplete expression" ) ;
        }

        if( token.getTokenType() == Token.NUMERIC ) {
            value = new BigDecimal( token.getTokenValue() );
        }
        else if( token.getTokenValue().equals( sqrt ) ) {
            value = evaluate( lexer );
            value = sqrt( value );
        }
        else if( token.getTokenType() == Token.PARENTHESES ) {

            if( token.getTokenValue().equals( lbracket ) ) {
                value = evaluate( lexer );
                token = lexer.getNextToken();
                if( !token.getTokenValue().equals( rbracket ) ) {
                    throw new Exception( "Right bracket missing" );
                }
            }
            else if( token.getTokenValue().equals( rbracket ) ) {
                throw new Exception( "Invalid Input" );
            }
        }
        else {
            throw new Exception( "Invalid expression." ) ;
        }

        return value.multiply( BigDecimal.valueOf( sign ) );
    }
}
