package com.android.tuto.databasesearch;

import com.android.tuto.databasesearch.util.DatabaseHelper;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class EmployeeDetails extends Activity {
    protected TextView employeeName;
    protected TextView employeeTitle;
    protected TextView officePhone;
    protected TextView cellPhone;
    protected TextView email;
    protected int employeeId;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_details);

        employeeName = (TextView) findViewById(R.id.employeeName);
        employeeTitle = (TextView) findViewById(R.id.title);
        officePhone = (TextView) findViewById(R.id.officePhone);
        cellPhone = (TextView) findViewById(R.id.cellPhone);
        email = (TextView) findViewById(R.id.email);

        employeeId = getIntent().getExtras().getInt("EMPLOYEE_ID", 0);

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EMPLOYEE + " WHERE _id = ?", new String[] { "" + employeeId });
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FIRST_NAME)) + " "
                    + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_LAST_NAME));
            employeeName.setText(name);
            employeeTitle.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TITLE)));
            officePhone.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_OFFICE_PHONE)));
            cellPhone.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CELL_PHONE)));
            email.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL)));
        }
    }

}
