package com.example.android.timemanager.logic;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Вита on 21.02.2017.
 */
@DatabaseTable(tableName = "workinghours")
public class WorkItem implements Serializable {

    public final static String ID_FIELD_NAME = "_ID";
    public final static String START_FIELD_NAME = "beginningHour";
    public final static String END_FIELD_NAME = "endingHour";

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int Id;
    @DatabaseField(canBeNull = true, dataType = DataType.LONG, columnName = START_FIELD_NAME)
    private long start;
    @DatabaseField(canBeNull = true, dataType = DataType.LONG, columnName = END_FIELD_NAME)
    private long end;

    public WorkItem() {
    }

    public WorkItem(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public WorkItem(Calendar start, Calendar end) {
        this.start = start.getTimeInMillis();
        this.end = end.getTimeInMillis();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
