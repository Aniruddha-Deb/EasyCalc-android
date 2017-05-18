package com.sensei.easycalc.core;

import android.content.Context;

import com.sensei.easycalc.R;

import java.util.ArrayList;
import java.util.Stack;

public class Lexer {

    private String input      = null;
    private int    currentPos = 0;

    private Stack<Token> pushedBackTokens = new Stack<>() ;

    public char add      = (char)0;
    public char subtract = (char)0;
    public char multiply = (char)0;
    public char rbracket = (char)0;
    public char divide   = (char)0;
    public char lbracket = (char)0;
    public char sqrt     = (char)0;

    public Lexer( String input, Context ctx ) {
        this.input = input;

        add      = ctx.getString( R.string.add ).charAt(0);
        subtract = ctx.getString( R.string.subtract ).charAt(0);
        multiply = ctx.getString( R.string.multiply ).charAt(0);
        divide   = ctx.getString( R.string.divide ).charAt(0);
        lbracket = ctx.getString( R.string.lbracket ).charAt(0);
        rbracket = ctx.getString( R.string.rbracket ).charAt(0);
        sqrt     = ctx.getString( R.string.sqrt ).charAt( 0 );
    }

    public ArrayList<Token> getAllTokens() {
        ArrayList<Token> tokens = new ArrayList<>() ;
        Token token = getNextToken() ;
        while( token != null ) {
            tokens.add( token ) ;
            token = getNextToken() ;
        }
        return tokens ;
    }

    public void reset( String input ) {
        this.input = input;
        currentPos = 0;
        pushedBackTokens = new Stack<>();
    }

    public Token getNextToken() {

        if( !pushedBackTokens.isEmpty() ) {
            return pushedBackTokens.pop() ;
        }

        for( ; currentPos<input.length(); currentPos++ ) {
            char ch = input.charAt( currentPos );
            if( isOperator( ch ) ) {
                currentPos++ ;
                if( ch == '-' ) {
                    return new Token( Token.OPERATOR, subtract );
                }
                return new Token( Token.OPERATOR, ch );
            }
            else if( isParenthesis( ch ) ) {
                currentPos++ ;
                return new Token( Token.PARENTHESES, ch );
            }
            else if( isPartOfNumToken( ch ) ) {
                Token token = null ;
                while( isPartOfNumToken( ch ) ) {
                    if( token == null ) {
                        token = new Token( Token.NUMERIC, ch );
                    }
                    else {
                        token.append( ch );
                    }

                    currentPos++ ;
                    if( currentPos >= input.length() ) {
                        break ;
                    }
                    else {
                        ch = input.charAt( currentPos ) ;
                    }
                }
                return token ;
            }
        }
        return null;
    }

    public void pushBackToken( Token t ) {
        pushedBackTokens.push( t ) ;
    }

    private boolean isParenthesis( char ch ) {
        return ch == lbracket || ch == rbracket;
    }

    private boolean isOperator( char ch ) {
        return ch == add || ch == subtract || ch == multiply || ch == divide || ch == '-' ||
               ch == sqrt;
    }

    private boolean isPartOfNumToken( char ch ) {
        return ch == '.' ||
               Character.isDigit( ch ) ;
    }
}
