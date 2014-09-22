package com.android.tuto.ch8todolistdatabase;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

public class ToDoListActivity extends Activity implements NewItemFragment.OnNewItemAddedListener, LoaderCallbacks<Cursor> {
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
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onNewItemAdded(String newItem) {
        /** Save into data base*/
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ToDoContentProvider.KEY_TASK, newItem);
        cr.insert(ToDoContentProvider.CONTENT_URI, values);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this, ToDoContentProvider.CONTENT_URI, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int keyTaskIndex = data.getColumnIndexOrThrow(ToDoContentProvider.KEY_TASK);
        todoItems.clear();
        while (data.moveToNext()) {
            ToDoItem newItem = new ToDoItem(data.getString(keyTaskIndex));
            todoItems.add(newItem);
        }
        aa.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO Auto-generated method stub
    }

}
