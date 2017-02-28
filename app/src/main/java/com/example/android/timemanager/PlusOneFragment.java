package com.example.android.timemanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import com.example.android.timemanager.logic.DateWorker;
import com.example.android.timemanager.logic.WorkItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class PlusOneFragment extends Fragment {

    public static List<WorkItem> listWorkItems;
    public static final String POSITION = "POSITION";

    private static final int ADD_ITEM_REQUEST_CODE = 1;
    private static final int EDIT_ITEM_REQUEST_CODE = 2;
    private static final String TAG = "PlusOneFragment";

    private FloatingActionButton addBtn;
    private Calendar start;
    private Calendar end;

    DBOpenHelper mDbHelper;
    SQLiteDatabase db;

    private RecyclerView recyclerView;
    private WorkItemAdapter adapter;
    private MultiSelector mMultiSelector = new MultiSelector();
    private ActionMode actionMode;
    private ModalMultiSelectorCallback mActionModeCallback = new ModalMultiSelectorCallback(mMultiSelector) {
        //открывается наше меню
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }

        //обрабатываем нажатия на иконочки
        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_delete) {
                //проходимся по всему списку, если элемент присутствует в MultiSelector, удаляем его
                for (int i = listWorkItems.size(); i >= 0; i--) {
                    if (mMultiSelector.isSelected(i, 0)) {

                        WorkItem item = listWorkItems.get(i);
                        //TODO:удалить еще и из базы и вообще навсегда

                        mDbHelper = new DBOpenHelper(getContext());
                        db = mDbHelper.getReadableDatabase();
                        db.beginTransaction();


                        String whereClause = DBOpenHelper.DATE_YEAR + "=? and " + DBOpenHelper.DATE_MONTH + "=? and " + DBOpenHelper.DATE_DAY + "=? and "
                                + DBOpenHelper.BEGINNING_HOUR + "=? and " + DBOpenHelper.BEGINNING_MINUTE + "=? and "
                                + DBOpenHelper.ENDING_HOUR + "=? and " + DBOpenHelper.ENDING_MINUTE + "=?";

                        String[] whereArgs = new String[]{String.valueOf(item.getStart().get(Calendar.YEAR)),
                                String.valueOf(item.getStart().get(Calendar.MONTH)),
                                String.valueOf(item.getStart().get(Calendar.DAY_OF_MONTH)),
                                String.valueOf(item.getStart().get(Calendar.HOUR_OF_DAY)),
                                String.valueOf(item.getStart().get(Calendar.MINUTE)),
                                String.valueOf(item.getEnd().get(Calendar.HOUR_OF_DAY)),
                                String.valueOf(item.getEnd().get(Calendar.MINUTE)),
                        };
                        db.delete(DBOpenHelper.TABLE_NAME, whereClause, whereArgs);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        db.close();

                        listWorkItems.remove(i);
                        adapter.notifyItemRemoved(i);


                    }
                }
                actionMode.finish();
                return true;
            }

            //TODO:сделать редактирование
            if (menuItem.getItemId() == R.id.action_edit) {
                final ArrayList<Integer> indexes = (ArrayList<Integer>) mMultiSelector.getSelectedPositions();
                if (indexes.size() == 1) {
                    int index = indexes.get(0);
                    WorkItem item = listWorkItems.get(index);

//                    mDbHelper = new DBOpenHelper(getContext());
//                    db = mDbHelper.getReadableDatabase();
//                    db.beginTransaction();
//
//
//                    String whereClause = DBOpenHelper.DATE_YEAR + "=? and " + DBOpenHelper.DATE_MONTH + "=? and " + DBOpenHelper.DATE_DAY + "=? and "
//                            + DBOpenHelper.BEGINNING_HOUR + "=? and " + DBOpenHelper.BEGINNING_MINUTE + "=? and "
//                            + DBOpenHelper.ENDING_HOUR + "=? and " + DBOpenHelper.ENDING_MINUTE + "=?";
//
//                    String[] whereArgs = new String[]{String.valueOf(item.getStart().get(Calendar.YEAR)),
//                            String.valueOf(item.getStart().get(Calendar.MONTH)),
//                            String.valueOf(item.getStart().get(Calendar.DAY_OF_MONTH)),
//                            String.valueOf(item.getStart().get(Calendar.HOUR_OF_DAY)),
//                            String.valueOf(item.getStart().get(Calendar.MINUTE)),
//                            String.valueOf(item.getEnd().get(Calendar.HOUR_OF_DAY)),
//                            String.valueOf(item.getEnd().get(Calendar.MINUTE)),
//                    };
//                    db.delete(DBOpenHelper.TABLE_NAME, whereClause, whereArgs);
//                    db.setTransactionSuccessful();
//                    db.endTransaction();
//                    db.close();
                    Intent intent = new Intent(getContext(), AddItemActivity.class);
                    intent.putExtra(DBOpenHelper.BEGINNING_HOUR, String.valueOf(item.getStart().get(Calendar.HOUR_OF_DAY)));
                    intent.putExtra(DBOpenHelper.BEGINNING_MINUTE, String.valueOf(item.getStart().get(Calendar.MINUTE)));
                    intent.putExtra(DBOpenHelper.ENDING_HOUR, String.valueOf(item.getEnd().get(Calendar.HOUR_OF_DAY)));
                    intent.putExtra(DBOpenHelper.ENDING_MINUTE, String.valueOf(item.getEnd().get(Calendar.MINUTE)));
                    intent.putExtra(DBOpenHelper.DATE_YEAR, String.valueOf(item.getStart().get(Calendar.YEAR)));
                    intent.putExtra(DBOpenHelper.DATE_MONTH, String.valueOf(item.getStart().get(Calendar.MONTH)));
                    intent.putExtra(DBOpenHelper.DATE_DAY, String.valueOf(item.getStart().get(Calendar.DAY_OF_MONTH)));
                    intent.putExtra(PlusOneFragment.POSITION, index);

                    startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);

                } else {
                    Toast.makeText(getContext(), R.string.warningSelectOnlyOne, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            //если предварительно перед закрытием не почистить MultiSelector начинают баги с окрашиванием,
            // поэтому тут такой костылик, ведь его сделать проще, чем предъявлять претензии автору библиотеки
            mMultiSelector.clearSelections();
            super.onDestroyActionMode(actionMode);
        }
    };

    public static PlusOneFragment newInstance() {

        return new PlusOneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plus_one, container, false);

        listWorkItems = new ArrayList<WorkItem>();

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new WorkItemAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        this.addBtn = (FloatingActionButton) view.findViewById(R.id.add_fab);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), AddItemActivity.class), ADD_ITEM_REQUEST_CODE);
            }
        });

        getDataFromDB();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_ITEM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    WorkItem item = (WorkItem) data.getSerializableExtra("item");
                    listWorkItems.add(item);
                    adapter.notifyItemInserted(adapter.getItemCount());

                    DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
                    SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

                    db.beginTransaction();
                    ContentValues cv = new ContentValues();
                    cv.put(DBOpenHelper.BEGINNING_HOUR, item.getStart().get(Calendar.HOUR_OF_DAY));
                    cv.put(DBOpenHelper.BEGINNING_MINUTE, item.getStart().get(Calendar.MINUTE));
                    cv.put(DBOpenHelper.ENDING_HOUR, item.getEnd().get(Calendar.HOUR_OF_DAY));
                    cv.put(DBOpenHelper.ENDING_MINUTE, item.getEnd().get(Calendar.MINUTE));
                    cv.put(DBOpenHelper.DATE_DAY, item.getStart().get(Calendar.DAY_OF_MONTH));
                    cv.put(DBOpenHelper.DATE_MONTH, item.getStart().get(Calendar.MONTH));
                    cv.put(DBOpenHelper.DATE_YEAR, item.getStart().get(Calendar.YEAR));
                    db.insert(DBOpenHelper.TABLE_NAME, null, cv);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();

                }
                break;
            case EDIT_ITEM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //новое состояние элемента
                    WorkItem newItem = (WorkItem) data.getSerializableExtra("item");

                    //получили индекс того элемента, который редактировали
                    int index = data.getIntExtra(PlusOneFragment.POSITION, 0);

                    //сторое состояние этого элемента
                    WorkItem oldItem = listWorkItems.get(index);

                    DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
                    SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

                    db.beginTransaction();
                    ContentValues cv = new ContentValues();
                    cv.put(DBOpenHelper.BEGINNING_HOUR, newItem.getStart().get(Calendar.HOUR_OF_DAY));
                    cv.put(DBOpenHelper.BEGINNING_MINUTE, newItem.getStart().get(Calendar.MINUTE));
                    cv.put(DBOpenHelper.ENDING_HOUR, newItem.getEnd().get(Calendar.HOUR_OF_DAY));
                    cv.put(DBOpenHelper.ENDING_MINUTE, newItem.getEnd().get(Calendar.MINUTE));
                    cv.put(DBOpenHelper.DATE_DAY, newItem.getStart().get(Calendar.DAY_OF_MONTH));
                    cv.put(DBOpenHelper.DATE_MONTH, newItem.getStart().get(Calendar.MONTH));
                    cv.put(DBOpenHelper.DATE_YEAR, newItem.getStart().get(Calendar.YEAR));

                    String whereClause = DBOpenHelper._ID + "= ?";

                    String[] whereArgs = new String[]{String.valueOf(index + 1)};

                    db.update(DBOpenHelper.TABLE_NAME, cv, whereClause, whereArgs);
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();

                    listWorkItems.set(index, newItem);
                    adapter.notifyItemChanged(index);
                }
                break;
        }
    }

    private void getDataFromDB() {
        mDbHelper = new DBOpenHelper(getContext());
        db = mDbHelper.getReadableDatabase();
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DBOpenHelper.BEGINNING_HOUR,
                DBOpenHelper.BEGINNING_MINUTE,
                DBOpenHelper.ENDING_HOUR,
                DBOpenHelper.ENDING_MINUTE,
                DBOpenHelper.DATE_DAY,
                DBOpenHelper.DATE_MONTH,
                DBOpenHelper.DATE_YEAR,
        };

// How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(
                DBOpenHelper.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        if (c.moveToFirst()) {

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Calendar startDate = Calendar.getInstance();
                startDate.set(Calendar.YEAR, c.getInt(c.getColumnIndex(DBOpenHelper.DATE_YEAR)));
                startDate.set(Calendar.MONTH, c.getInt(c.getColumnIndex(DBOpenHelper.DATE_MONTH)));
                startDate.set(Calendar.DATE, c.getInt(c.getColumnIndex(DBOpenHelper.DATE_DAY)));
                startDate.set(Calendar.HOUR_OF_DAY, c.getInt(c.getColumnIndex(DBOpenHelper.BEGINNING_HOUR)));
                startDate.set(Calendar.MINUTE, c.getInt(c.getColumnIndex(DBOpenHelper.BEGINNING_MINUTE)));

                Calendar finishDate = Calendar.getInstance();
                finishDate.set(Calendar.YEAR, c.getInt(c.getColumnIndex(DBOpenHelper.DATE_YEAR)));
                finishDate.set(Calendar.MONTH, c.getInt(c.getColumnIndex(DBOpenHelper.DATE_MONTH)));
                finishDate.set(Calendar.DATE, c.getInt(c.getColumnIndex(DBOpenHelper.DATE_DAY)));
                finishDate.set(Calendar.HOUR_OF_DAY, c.getInt(c.getColumnIndex(DBOpenHelper.ENDING_HOUR)));
                finishDate.set(Calendar.MINUTE, c.getInt(c.getColumnIndex(DBOpenHelper.ENDING_MINUTE)));

                listWorkItems.add(new WorkItem(startDate, finishDate));
                adapter.notifyItemInserted(adapter.getItemCount());

            } while (c.moveToNext());
        } else {
            c.close();
        }
        // закрываем подключение к БД
        mDbHelper.close();

    }

    private class WorkItemViewHolder extends SwappingHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView dateTxtView;
        public TextView startTimeTxtView;
        public TextView endTimeTxtView;

        public WorkItemViewHolder(View itemView) {
            super(itemView, mMultiSelector);
            this.dateTxtView = (TextView) itemView.findViewById(R.id.date_txt_view);
            this.startTimeTxtView = (TextView) itemView.findViewById(R.id.start_txt_view);
            this.endTimeTxtView = (TextView) itemView.findViewById(R.id.finish_txt_view);

            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
            mMultiSelector.setSelected(WorkItemViewHolder.this, true);
            return true;
        }

        @Override
        public void onClick(View view) {
            if (mMultiSelector.isSelectable()) {
                // Selection is active; toggle activation
                setActivated(!isActivated());
                mMultiSelector.setSelected(WorkItemViewHolder.this, isActivated());
                if (mMultiSelector.getSelectedPositions().size() == 0) {
                    actionMode.finish();
                }
            } else {
                // Selection not active
            }
        }
    }

    private class WorkItemAdapter extends RecyclerView.Adapter<WorkItemViewHolder> {

        @Override
        public WorkItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appearance, parent, false);
            return new WorkItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(WorkItemViewHolder holder, int position) {
            WorkItem item = listWorkItems.get(position);
            holder.dateTxtView.setText(DateWorker.getDateAsString(item.getStart()));
            holder.startTimeTxtView.setText(DateWorker.getTimeAsString(item.getStart()));
            holder.endTimeTxtView.setText(DateWorker.getTimeAsString(item.getEnd()));
        }

        @Override
        public int getItemCount() {
            return listWorkItems.size();
        }


    }
}

