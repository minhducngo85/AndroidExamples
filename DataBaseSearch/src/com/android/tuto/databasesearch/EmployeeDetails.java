package com.android.tuto.databasesearch;

import java.util.ArrayList;
import java.util.List;

import com.android.tuto.databasesearch.data.EmployeeAction;
import com.android.tuto.databasesearch.util.DatabaseHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EmployeeDetails extends ListActivity {
    protected TextView employeeName;
    protected TextView employeeTitle;
    protected int employeeId;
    protected int managerId;

    protected List<EmployeeAction> actions;
    protected EmployeeActionAdapter adapter;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_details);
        employeeId = getIntent().getExtras().getInt("EMPLOYEE_ID", 0);

        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        Cursor cursor = helper.findEmployeeById(employeeId);
        if (cursor.getCount() == 1) {
            cursor.moveToNext();

            employeeName = (TextView) findViewById(R.id.employeeName);
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FIRST_NAME)) + " "
                    + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_LAST_NAME));
            employeeName.setText(name);

            employeeTitle = (TextView) findViewById(R.id.title);
            employeeTitle.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TITLE)));

            actions = new ArrayList<EmployeeAction>();

            String officePhone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_OFFICE_PHONE));
            if (officePhone != null) {
                actions.add(new EmployeeAction("Call office", officePhone, EmployeeAction.ACTION_CALL));
            }

            String cellPhone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CELL_PHONE));
            if (cellPhone != null) {
                actions.add(new EmployeeAction("Call mobile", cellPhone, EmployeeAction.ACTION_CALL));
                actions.add(new EmployeeAction("SMS", cellPhone, EmployeeAction.ACTION_SMS));
            }

            String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL));
            if (email != null) {
                actions.add(new EmployeeAction("Email", email, EmployeeAction.ACTION_EMAIL));
            }

            managerId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_MANAGER_ID));
            if (managerId > 0) {
                Cursor managerCursor = helper.findEmployeeById(managerId);
                if (managerCursor.getCount() == 1) {
                    managerCursor.moveToNext();
                    String managerName = managerCursor.getString(managerCursor.getColumnIndex(DatabaseHelper.COL_FIRST_NAME)) + " "
                            + managerCursor.getString(managerCursor.getColumnIndex(DatabaseHelper.COL_LAST_NAME));
                    actions.add(new EmployeeAction("View manager", managerName, EmployeeAction.ACTION_MANAGER));
                }
            }

            Cursor reportsCursor = helper.getDirectReports(employeeId);
            if (reportsCursor.getCount() > 0) {
                actions.add(new EmployeeAction("Direct reports", "(" + reportsCursor.getCount() + ")", EmployeeAction.ACTION_REPORTS));
            }

            adapter = new EmployeeActionAdapter();
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
        EmployeeAction action = actions.get(position);
        Intent intent;
        if (action.getType() == EmployeeAction.ACTION_CALL) {
            Uri callUri = Uri.parse("tel:" + action.getData().trim());
            intent = new Intent(Intent.ACTION_CALL);
            intent.setData(callUri);
            startActivity(intent);
        } else if (action.getType() == EmployeeAction.ACTION_EMAIL) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { action.getData() });
            startActivity(intent);
        } else if (action.getType() == EmployeeAction.ACTION_SMS) {
            Uri smsUri = Uri.parse("sms:" + action.getData());
            intent = new Intent(Intent.ACTION_VIEW, smsUri);
            startActivity(intent);
        } else if (action.getType() == EmployeeAction.ACTION_MANAGER) {
            intent = new Intent(this, EmployeeDetails.class);
            intent.putExtra("EMPLOYEE_ID", managerId);
            startActivity(intent);
        } else if (action.getType() == EmployeeAction.ACTION_REPORTS) {
            intent = new Intent(this, DirectReports.class);
            intent.putExtra("EMPLOYEE_ID", employeeId);
            startActivity(intent);
        }
    }

    /**
     * the array adapter
     * 
     * @author minhducngo
     *
     */
    class EmployeeActionAdapter extends ArrayAdapter<EmployeeAction> {

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EmployeeAction action = getItem(position);
            LinearLayout layoutView;
            if (convertView == null) {
                // Inflate a new view if this is not an update.
                layoutView = new LinearLayout(getContext());
                //LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater li = getLayoutInflater();
                li.inflate(R.layout.action_list_item, layoutView, true);
            } else {
                // Otherwise we'll update the existing View
                layoutView = (LinearLayout) convertView;
            }

            /** the deprecated way to inflate view */
            // LayoutInflater inflater = getLayoutInflater();
            // View view = inflater.inflate(R.layout.action_list_item, parent, false);

            TextView label = (TextView) layoutView.findViewById(R.id.label);
            label.setText(action.getLabel());

            TextView data = (TextView) layoutView.findViewById(R.id.data);
            data.setText(action.getData());
            return layoutView;
        }

        public EmployeeActionAdapter() {
            super(EmployeeDetails.this, R.layout.action_list_item, actions);
        }

    }

}
