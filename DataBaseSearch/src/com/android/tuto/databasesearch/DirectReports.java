package com.android.tuto.databasesearch;

import com.android.tuto.databasesearch.util.DatabaseHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DirectReports extends ListActivity {

    private ListAdapter adapter;

    private TextView employeeName;
    private TextView title;

    private DatabaseHelper dbHelper;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direct_reports);
        dbHelper = new DatabaseHelper(this);

        employeeName = (TextView) findViewById(R.id.directReportOfName);
        title = (TextView) findViewById(R.id.directReportOfTitle);

        int employeeId = getIntent().getExtras().getInt("EMPLOYEE_ID", 0);
        Cursor cursor = dbHelper.findEmployeeById(employeeId);
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FIRST_NAME)) + " "
                    + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_LAST_NAME));
            employeeName.setText(name);
            title.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TITLE)));

            cursor = dbHelper.getDirectReports(employeeId);
            // flags = 0, which does not observe the data changed or dat-requery
            adapter = new SimpleCursorAdapter(this, R.layout.employee_list_item, cursor, new String[] { DatabaseHelper.COL_FIRST_NAME,
                    DatabaseHelper.COL_LAST_NAME, DatabaseHelper.COL_TITLE }, new int[] { R.id.firstName, R.id.lastName, R.id.title }, 0);
            setListAdapter(adapter);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = (Cursor) adapter.getItem(position);
        Intent intent = new Intent(this, EmployeeDetails.class);
        intent.putExtra("EMPLOYEE_ID", c.getInt(c.getColumnIndex("_id")));
        startActivity(intent);
    }

}
