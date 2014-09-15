package com.android.ch4todolist.adapter;

import java.util.ArrayList;

import com.android.ch4todolist.adapter.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ToDoListActivity extends Activity implements NewItemFragment.OnNewItemAddedListener {

    private ToDoItemAdapter aa;
    private ArrayList<ToDoItem> todoItems;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate your content
        setContentView(R.layout.main);

        // get reference to the Fragment
        FragmentManager fm = getFragmentManager();
        ToDoListFragment todoListFragment = (ToDoListFragment) fm.findFragmentById(R.id.TodoListFragment);

        // Create the array list of to do items
        todoItems = new ArrayList<ToDoItem>();

        // Create the array adapter to bind the array to the ListView
        // default item list view
        int resID = R.layout.todolist_item;
        aa = new ToDoItemAdapter(this, resID, todoItems);

        // Bind the array adapter to the listview.
        todoListFragment.setListAdapter(aa);
    }

    @Override
    public void onNewItemAdded(String newItem) {
        todoItems.add(new ToDoItem(newItem));
        aa.notifyDataSetChanged();
    }

}
