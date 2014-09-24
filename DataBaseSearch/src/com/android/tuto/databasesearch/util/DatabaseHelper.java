package com.android.tuto.databasesearch.util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.android.tuto.databasesearch.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COL_FIRST_NAME = "first_name";

    public static final String COL_LAST_NAME = "last_name";

    public static final String COL_TITLE = "title";

    public static final String COL_OFFICE_PHONE = "office_phone";

    public static final String COL_CELL_PHONE = "cell_phone";

    public static final String COL_EMAIL = "email";

    public static final String COL_DEPARTMENT = "department";

    public static final String COL_CITY = "city";

    public static final String COL_PICTURE = "picture";

    public static final String COL_MANAGER_ID = "manager_id";

    public static final String TABLE_EMPLOYEE = "employee";

    public static final String DATABASE_NAME = "employee.db";

    private static final int DATABASE_VERSION = 2;

    protected Context context;

    protected DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String s;
        try {
            Toast.makeText(context, "1", 2000).show();
            InputStream in = context.getResources().openRawResource(R.raw.sql);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in, null);
            NodeList statements = doc.getElementsByTagName("statement");
            for (int i = 0; i < statements.getLength(); i++) {
                s = statements.item(i).getChildNodes().item(0).getNodeValue();
                db.execSQL(s);
            }
        } catch (Throwable t) {
            Toast.makeText(context, t.toString(), 50000).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DATABASE_SEARCH", "Update db version from " + oldVersion + " to version " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS employees");
        onCreate(db);
    }

    /**
     * to find an employee from db
     * 
     * @param id
     *            the row id
     * @return the raw data
     */
    public Cursor findEmployeeById(long id) {
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_EMPLOYEE + " WHERE _id = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[] { "" + id });
        return cursor;
    }

    /**
     * search employees from db
     * 
     * @param searchString
     *            the search string. in case, the search string is equal to null or empty, then returns all rows
     * @return the raw data
     */
    public Cursor searchEmployees(String searchString) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (searchString == null || searchString.trim().isEmpty()) {
            return db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EMPLOYEE, null);
        }
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_EMPLOYEE + " WHERE first_name || ' ' || last_name LIKE ?";

        Cursor cursor = db.rawQuery(sql, new String[] { "%" + searchString + "%" });
        return cursor;
    }

    /**
     * gets direct reports of a given employee id
     * 
     * @param employeeId
     *            the employee id
     * @return raw data
     */
    public Cursor getDirectReports(int employeeId) {
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_EMPLOYEE + " WHERE " + DatabaseHelper.COL_MANAGER_ID + " = ?";
        return this.excuteReadableQuery(sql, new String[] { "" + employeeId });
    }

    /**
     * executes a readable query
     * 
     * @param query
     *            the sql query
     * @param selectionArgs
     *            the selection arguments
     * @return the raw data
     */
    public Cursor excuteReadableQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, selectionArgs);
    }

}
