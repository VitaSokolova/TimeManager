package com.example.android.timemanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.timemanager.logic.DatePickerFragment;
import com.example.android.timemanager.logic.DateWorker;
import com.example.android.timemanager.logic.WorkItem;

import java.text.DecimalFormat;
import java.util.Calendar;


public class CountFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    LinearLayout startDateBtn;
    LinearLayout endDateBtn;
    LinearLayout priceBtn;

    TextView startDateTxt;
    TextView endDateTxt;
    TextView priceTxt;
    TextView resultSumTxt;

    Button countBtn;

    Calendar dateBeginning = Calendar.getInstance();
    Calendar dateEnding = Calendar.getInstance();

    private boolean beginDate = false;
    private boolean endDate = false;

    private float pricePerHour = 150;
    public static final String MYPREFS = "timeManagerSharedPreferences";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_count, container, false);

        pricePerHour = loadPrefs();
        dateBeginning = Calendar.getInstance();
        dateBeginning.set(Calendar.MONTH, dateBeginning.get(Calendar.MONTH)-1);
        dateBeginning.set(Calendar.HOUR_OF_DAY, 0);
        dateBeginning.set(Calendar.MINUTE, 0);

        dateEnding = Calendar.getInstance();

        startDateTxt = (TextView) view.findViewById(R.id.start_date_fragment_txt);
        endDateTxt = (TextView) view.findViewById(R.id.end_date_fragment_txt);
        priceTxt = (TextView) view.findViewById(R.id.money_fragment_txt);
        priceTxt.setText(String.valueOf(pricePerHour));

        resultSumTxt = (TextView) view.findViewById(R.id.result_fragment_count);

        startDateBtn = ( LinearLayout) view.findViewById(R.id.begin_layout);
        endDateBtn = ( LinearLayout) view.findViewById(R.id.end_layout);
        priceBtn = ( LinearLayout) view.findViewById(R.id.count_layout);
        countBtn = (Button) view.findViewById(R.id.count_fragment_btn);


        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = DatePickerFragment.newInstance(dateBeginning, CountFragment.this);
                dateFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                beginDate = true;
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = DatePickerFragment.newInstance(dateEnding, CountFragment.this);
                dateFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                endDate = true;
            }
        });

        priceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PriceDialog dialog = PriceDialog.newInstance();
                dialog.setListener(new PriceDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(String text) {
                        priceTxt.setText(text);
                        pricePerHour = Integer.valueOf(text);
                        savePref();
                    }
                });
                dialog.show(getFragmentManager(), "PriceEditDialog");
            }
        });

        countBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double sum = 0;
                WorkItem item;

                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();

                for (int i = 0; i < ItemsListFragment.listWorkItems.size(); i++) {
                    item = ItemsListFragment.listWorkItems.get(i);

                    start.setTimeInMillis(item.getStart());
                    end.setTimeInMillis(item.getEnd());

                    if ((start.after(dateBeginning)) && (end.before(dateEnding))) {
//                        float hours = item.getEnd().getTime().getTime() - item.getStart().getTime().getTime();
                        float startHours = start.get(Calendar.HOUR_OF_DAY);
                        float startMinutes = start.get(Calendar.MINUTE);

                        float endHours = end.get(Calendar.HOUR_OF_DAY);
                        float endMinutes = end.get(Calendar.MINUTE);
                        float workingMinutes = endHours * 60 + endMinutes - startHours * 60 - startMinutes;

                        sum += ((pricePerHour/60.0) * workingMinutes);

                    }
                }
                String pattern = "##0";
                DecimalFormat decimalFormat = new DecimalFormat(pattern);
                String format = decimalFormat.format(sum);
                resultSumTxt.setText(format);
            }
        });

        startDateTxt.setText(DateWorker.getDateAsString(dateBeginning));
        endDateTxt.setText(DateWorker.getDateAsString(dateEnding));

        countBtn = (Button) view.findViewById(R.id.count_fragment_btn);

        return view;
    }


    public static Fragment newInstance() {
        return new CountFragment();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (beginDate) {
            dateBeginning.set(Calendar.YEAR, year);
            dateBeginning.set(Calendar.MONTH, month);
            dateBeginning.set(Calendar.DAY_OF_MONTH, day);
            startDateTxt.setText(DateWorker.getDateAsString(dateBeginning));
            beginDate = false;
        } else {
            dateEnding.set(Calendar.YEAR, year);
            dateEnding.set(Calendar.MONTH, month);
            dateEnding.set(Calendar.DAY_OF_MONTH, day);
            endDateTxt.setText(DateWorker.getDateAsString(dateEnding));
            endDate = false;
        }
    }
    //...
    protected void savePref() {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        // Use an editor to modify the shared preferences.
        SharedPreferences.Editor edit = mySharedPreferences.edit();
        // Store primitive types in the shared preferences object.
        edit.putFloat("Float", pricePerHour);
        // Commit the changes.
        edit.commit();
    }

    protected float loadPrefs() {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        float floatNum = mySharedPreferences.getFloat("Float", 150);
        return floatNum;
    }
}
