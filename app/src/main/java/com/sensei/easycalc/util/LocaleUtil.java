package com.sensei.easycalc.util;

import android.content.Context;

import com.sensei.easycalc.R;

public class LocaleUtil {
    
    public static String convertToString( String decimalString, Context ctx ) {

        if( decimalString == null ) {
            return "";
        }

        for( char c : decimalString.toCharArray() ) {
            String strToReplace = c + "";
            switch( c ) {
                case '1':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.one ) );
                    break;

                case '2':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.two ) );
                    break;

                case '3':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.three ) );
                    break;

                case '4':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.four ) );
                    break;

                case '5':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.five ) );
                    break;

                case '6':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.six ) );
                    break;

                case '7':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.seven ) );
                    break;

                case '8':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.eight ) );
                    break;

                case '9':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.nine ) );
                    break;

                case '0':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.zero ) );
                    break;

                case '.':
                    decimalString = decimalString.replaceFirst( "\\.", ctx.getString( R.string.decimal ) );
                    break;
                
                case '-':
                    decimalString = decimalString.replaceFirst( strToReplace, ctx.getString( R.string.subtract ) );
                    break;

                default:
                    // do nothing
            }
        }
        return decimalString;
    }
    
}
