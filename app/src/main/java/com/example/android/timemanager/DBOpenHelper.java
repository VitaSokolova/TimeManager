package com.example.android.timemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Комп on 22.02.2017.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "test";

    public static final String _ID = "_id";
    public static final String TABLE_NAME = "workinghours";
    public static final String BEGINNING_HOUR = "beginningHour";
    public static final String ENDING_HOUR = "endingHour";
    public static final String BEGINNING_MINUTE = "beginningMinute";
    public static final String ENDING_MINUTE = "endingMinute";
    public static final String DATE_DAY = "dateDay";
    public static final String DATE_MONTH = "dateMonth";
    public static final String DATE_YEAR = "dateYear";

    //, " +  + " INT
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " + _ID + " integer primary key autoincrement, " +
            " INT, " + BEGINNING_HOUR + " INT, " + ENDING_HOUR + " INT, " + BEGINNING_MINUTE + " INT, " + ENDING_MINUTE + " INT, "
            + DATE_DAY + " INT," + DATE_MONTH + " INT," + DATE_YEAR + " INT);";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void onDelete(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean deleteSingleRow(SQLiteDatabase sqLiteDatabase, String rowId) {
        return sqLiteDatabase.delete(TABLE_NAME, _ID + "=" + rowId, null) > 0;
    }
}
