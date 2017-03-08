package com.example.android.timemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.example.android.timemanager.logic.DBOpenHelper;
import com.example.android.timemanager.logic.DateWorker;
import com.example.android.timemanager.logic.WorkItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ItemsListFragment extends Fragment {

    public static List<WorkItem> listWorkItems;
    public static final String POSITION = "POSITION";
    public static final String SUCCEED = "SUCCEED";
    public static final String INDEX = "INDEX";

    private static final int ADD_ITEM_REQUEST_CODE = 1;
    private static final int EDIT_ITEM_REQUEST_CODE = 2;
    private static final String TAG = "PlusOneFragment";

    private FloatingActionButton addBtn;
    private Calendar start;
    private Calendar end;

    private DBOpenHelper mDbHelper = null;

    private RecyclerView recyclerView;
    private WorkItemAdapter adapter;
    private MultiSelector mMultiSelector = new MultiSelector();
    private ActionMode actionMode;
    private Handler deletingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int index = msg.getData().getInt(INDEX);

        }

    };
    private Handler edittingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int index = msg.getData().getInt(INDEX);
            adapter.notifyItemChanged(index);

        }
    };
    private Handler addingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int index = msg.getData().getInt(INDEX);
            adapter.notifyItemInserted(index);

        }
    };
    private Handler loadAllHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            adapter.notifyDataSetChanged();

        }
    };
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
                for (int i = listWorkItems.size(); i >=0; i--) {
                    if (mMultiSelector.isSelected(i, 0)) {

                        final WorkItem item = listWorkItems.get(i);
//TODO:удалить еще и из базы и вообще навсегда
                        final int finalI = i;

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Dao<WorkItem, Integer> workItemDao = getHelper().getWorkItemDao();
                                    workItemDao.delete(item);

                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(INDEX, finalI);
                                    msg.setData(bundle);
                                    //deletingHandler.sendMessage(msg);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    //предупредим пользователя, что добавление не состоялось
                                    Toast.makeText(getContext(), R.string.warningAddFailed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        Thread thread = new Thread(runnable);
                        thread.start();

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

                    // TODO закинуть в новую активити что надо
                    Intent intent = new Intent(getContext(), AddItemActivity.class);
                    intent.putExtra(DBOpenHelper.BEGINNING, item.getStart());
                    intent.putExtra(DBOpenHelper.ENDING, item.getEnd());
                    intent.putExtra(ItemsListFragment.POSITION, index);

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

    public static ItemsListFragment newInstance() {

        return new ItemsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_list, container, false);

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

        //TODO выбрать данные из БД

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final Dao<WorkItem, Integer> workItemDao = getHelper().getWorkItemDao();

//            mDbHelper.onCreate(getHelper().getWritableDatabase());
                    listWorkItems = workItemDao.queryForAll();
                } catch (SQLException e)

                {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_ITEM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    final WorkItem item = (WorkItem) data.getSerializableExtra("item");

                    //TODO добавление в базу
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Dao<WorkItem, Integer> workItemDao = getHelper().getWorkItemDao();
                                //This is the way to insert data into a database table
                                workItemDao.create(item);
                                listWorkItems.add(item);

                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt(INDEX, adapter.getItemCount());
                                msg.setData(bundle);
                                addingHandler.sendMessage(msg);

                            } catch (SQLException e) {
                                e.printStackTrace();
                                //предупредим пользователя, что добавление не состоялось
                                Toast.makeText(getContext(), R.string.warningAddFailed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    Thread thread = new Thread(runnable);
                    thread.start();
                }
                break;
            case EDIT_ITEM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
//новое состояние элемента
                    final WorkItem item = (WorkItem) data.getSerializableExtra("item");

//получили индекс того элемента, который редактировали
                    final int index = data.getIntExtra(ItemsListFragment.POSITION, 0);

                    //TODO сделать изменение в базе
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final Dao<WorkItem, Integer> workItemDao = getHelper().getWorkItemDao();
                                workItemDao.update(listWorkItems.get(index));
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putInt(INDEX, index);
                                msg.setData(bundle);
                                edittingHandler.sendMessage(msg);

                                listWorkItems.get(index).setStart(item.getStart());
                                listWorkItems.get(index).setEnd(item.getEnd());

                            } catch (SQLException e) {
                                e.printStackTrace();
                                //предупредим пользователя, что добавление не состоялось
                                Toast.makeText(getContext(), R.string.warningEditFailed, Toast.LENGTH_SHORT).show();
                            }
                        }

                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                    break;
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mDbHelper != null) {
            OpenHelperManager.releaseHelper();
            mDbHelper = null;
        }
    }

// так можно инициализировать mDBOpenHelper для использования в будущем

    private DBOpenHelper getHelper() {
        if (mDbHelper == null) {
            mDbHelper = OpenHelperManager.getHelper(getContext(), DBOpenHelper.class);
        }
        return mDbHelper;
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

