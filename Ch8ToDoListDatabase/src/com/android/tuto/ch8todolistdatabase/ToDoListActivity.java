package com.android.tuto.ch8todolistdatabase;

import java.util.ArrayList;
import java.util.List;

import com.android.tuto.ch8todolistdatabase.data.ToDoItem;
import com.android.tuto.ch8todolistdatabase.database.MyDatabaseHelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class ToDoListActivity extends Activity implements NewItemFragment.OnNewItemAddedListener {
    private ToDoItemAdapter aa;
    private ArrayList<ToDoItem> todoItems;

    MyDatabaseHelper helper = new MyDatabaseHelper(this, null);

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
        readItems();

    }

    @Override
    public void onNewItemAdded(String newItem) {
        /** Save into data base */
        ToDoItem item = new ToDoItem(newItem);
        helper.addToDoItem(item);
        readItems();
    }

    public void readItems() {
        List<ToDoItem> items = helper.getToDoItems();
        todoItems.clear();
        todoItems.addAll(items);
        aa.notifyDataSetChanged();
    }

}
