package com.example.android.timemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android.timemanager.logic.DatePickerFragment;
import com.example.android.timemanager.logic.DateWorker;
import com.example.android.timemanager.logic.TimePickerFragment;
import com.example.android.timemanager.logic.WorkItem;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Комп on 21.02.2017.
 */

public class AddItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView beginningTextView;
    private TextView endingTextView;
    private TextView dateTextView;

    private LinearLayout startImageBtn;
    private LinearLayout endImageBtn;
    private LinearLayout dateImageBtn;

    private Button addButton;

    private Calendar timeBeginning;
    private Calendar timeEnding;
    private boolean beginTime = false;
    private boolean endTime = false;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        timeBeginning = Calendar.getInstance();
        timeEnding = Calendar.getInstance();

        beginningTextView = (TextView) findViewById(R.id.start_dialog_txt);
        endingTextView = (TextView) findViewById(R.id.end_dialog_txt);
        dateTextView = (TextView) findViewById(R.id.start_date_dialog);

        addButton = (Button) findViewById(R.id.add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("item", new WorkItem(timeBeginning, timeEnding));
                intent.putExtra(PlusOneFragment.POSITION, index);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        //если есть какие-то вложения, значит активити вызывалось для редактирования элемента
        if (intent.getExtras() != null) {
            initEditView(intent);
            index = intent.getIntExtra(PlusOneFragment.POSITION, 0);

        }

        initPickers();

    }


    private void initEditView(Intent intent) {
        this.addButton.setText(R.string.edit);

        timeBeginning.set(Calendar.HOUR_OF_DAY, Integer.valueOf(intent.getStringExtra(DBOpenHelper.BEGINNING_HOUR)));
        timeBeginning.set(Calendar.MINUTE, Integer.valueOf(intent.getStringExtra(DBOpenHelper.BEGINNING_MINUTE)));

        timeEnding.set(Calendar.HOUR_OF_DAY, Integer.valueOf(intent.getStringExtra(DBOpenHelper.ENDING_HOUR)));
        timeEnding.set(Calendar.MINUTE, Integer.valueOf(intent.getStringExtra(DBOpenHelper.ENDING_MINUTE)));

        timeBeginning.set(Calendar.YEAR, Integer.valueOf(intent.getStringExtra(DBOpenHelper.DATE_YEAR)));
        timeBeginning.set(Calendar.MONTH, Integer.valueOf(intent.getStringExtra(DBOpenHelper.DATE_MONTH)));
        timeBeginning.set(Calendar.DAY_OF_MONTH, Integer.valueOf(intent.getStringExtra(DBOpenHelper.DATE_DAY)));

        timeEnding.set(Calendar.YEAR, Integer.valueOf(intent.getStringExtra(DBOpenHelper.DATE_YEAR)));
        timeEnding.set(Calendar.MONTH, Integer.valueOf(intent.getStringExtra(DBOpenHelper.DATE_MONTH)));
        timeEnding.set(Calendar.DAY_OF_MONTH, Integer.valueOf(intent.getStringExtra(DBOpenHelper.DATE_DAY)));

    }

    private void initPickers() {
        beginningTextView.setText(DateWorker.getTimeAsString(timeBeginning));
        endingTextView.setText(DateWorker.getTimeAsString(timeEnding));
        dateTextView.setText(DateWorker.getDateAsString(timeBeginning));

        startImageBtn = (LinearLayout) findViewById(R.id.begin_add_layout);
        startImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timeFragment = TimePickerFragment.newInstance(timeBeginning);
                timeFragment.show(getSupportFragmentManager(), "calendar1");
                beginTime = true;

            }
        });
        endImageBtn = (LinearLayout) findViewById(R.id.end_add_layout);
        endImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timeFragment = TimePickerFragment.newInstance(timeEnding);
                timeFragment.show(getSupportFragmentManager(), "calendar2");
                endTime = true;
                endingTextView.setText(DateWorker.getTimeAsString(timeEnding));
            }
        });
        dateImageBtn = (LinearLayout) findViewById(R.id.date_add_layout);
        dateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = DatePickerFragment.newInstance(timeBeginning, AddItemActivity.this);
                dateFragment.show(getSupportFragmentManager(), "datePicker");
                dateTextView.setText(DateWorker.getDateAsString(timeBeginning));
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        timeBeginning.set(Calendar.YEAR, year);
        timeBeginning.set(Calendar.MONTH, month);
        timeBeginning.set(Calendar.DAY_OF_MONTH, day);
        timeEnding.set(Calendar.YEAR, year);
        timeEnding.set(Calendar.MONTH, month);
        timeEnding.set(Calendar.DAY_OF_MONTH, day);

        dateTextView.setText(DateWorker.getDateAsString(timeBeginning));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        if (beginTime) {
            timeBeginning.set(Calendar.HOUR_OF_DAY, hour);
            timeBeginning.set(Calendar.MINUTE, minute);
            beginningTextView.setText(DateWorker.getTimeAsString(timeBeginning));
            beginTime = false;
        } else {
            timeEnding.set(Calendar.HOUR_OF_DAY, hour);
            timeEnding.set(Calendar.MINUTE, minute);
            endingTextView.setText(DateWorker.getTimeAsString(timeEnding));
            endTime = false;
        }
    }
}
