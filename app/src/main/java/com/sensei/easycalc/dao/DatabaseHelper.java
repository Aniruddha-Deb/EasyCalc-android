package com.sensei.easycalc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper instance = null;

    private static final String name = "EC_DB";
    private static final int version = 1;

    private static final String HISTORY_TABLE = "History";
    private static final String EXPRESSION_COLUMN = "expression";
    private static final String ANSWER_COLUMN = "answer";

    public static DatabaseHelper getInstance() {
        return instance;
    }

    public static void createInstance( Context ctx ) {
        instance = new DatabaseHelper( ctx );
    }

    private DatabaseHelper( Context context ) {
        super( context, name, null, version );
    }

    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {
        sqLiteDatabase.execSQL( "CREATE TABLE " + HISTORY_TABLE + " (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                EXPRESSION_COLUMN + " TEXT NOT NULL," +
                                ANSWER_COLUMN + " TEXT );" );
    }

    public void addTransactionToDatabase( String expression, String answer ) {
        ContentValues transactionValues = new ContentValues();
        transactionValues.put( "expression", expression );
        transactionValues.put( "answer", answer );
        getWritableDatabase().insert( HISTORY_TABLE, null, transactionValues );
    }

    public void clearHistory() {
        getWritableDatabase().execSQL( "DELETE FROM " + HISTORY_TABLE );
    }

    public Cursor getHistoryCursor() {
        return getReadableDatabase().query( HISTORY_TABLE,
                                            new String[]{ "_id", EXPRESSION_COLUMN, ANSWER_COLUMN },
                                            null, null, null, null,
                                            "_id DESC");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        dropAllTables( sqLiteDatabase );
        onCreate( sqLiteDatabase );
    }

    private void dropAllTables( SQLiteDatabase db ) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }

        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            try {
                db.execSQL( dropQuery );
            } catch( Exception e ) {
                Log.d( TAG, "!Error while dropping a table!" );
            }
        }
        c.close();
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables( db );
        onCreate( db );
    }
}
