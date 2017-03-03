package com.example.android.timemanager.logic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.timemanager.logic.WorkItem;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Комп on 22.02.2017.
 */

public class DBOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "test";

    public static final String _ID = "_id";
    public static final String TABLE_NAME = "workinghours";
    public static final String BEGINNING = "beginningHour";
    public static final String ENDING = "endingHour";


    private Dao<WorkItem, Integer> workItemDao;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( " + _ID + " integer primary key autoincrement, " +
            " INT, " + BEGINNING + " INTEGER, " + ENDING + " INTEGER);";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, WorkItem.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, WorkItem.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<WorkItem, Integer> getWorkItemDao() throws SQLException {
        if (workItemDao == null) {
            workItemDao = getDao(WorkItem.class);
        }
        return workItemDao;
    }

    @Override
    public void close() {
        workItemDao = null;
        super.close();
    }

    public void onDelete(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
