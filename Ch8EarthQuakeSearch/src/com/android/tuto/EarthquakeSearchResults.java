package com.android.tuto;

import com.android.tuto.database.EarthquakeDatabaseHelper;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class EarthquakeSearchResults extends ListActivity {

    private SimpleCursorAdapter adapter;

    private EarthquakeDatabaseHelper dbHelper;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        dbHelper = EarthquakeDatabaseHelper.getInstance(this);

        String searchString = getIntent().getExtras().getString("SEARCH_STRING");
        Cursor cursor = dbHelper.searchQuakesAsRawData(searchString);
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[] { EarthquakeDatabaseHelper.COL_SUMMARY_3 },
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(adapter);
    }
}
