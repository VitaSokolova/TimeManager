package com.example.android.timemanager.logic;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Вита on 21.02.2017.
 */

public class WorkItem implements Serializable {
    private Calendar start;
    private Calendar end;

    public WorkItem(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }
}
