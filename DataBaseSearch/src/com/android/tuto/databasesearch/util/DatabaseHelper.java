package com.android.tuto.databasesearch.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COL_FIRST_NAME = "first_name";

    public static final String COL_LAST_NAME = "last_name";

    public static final String COL_TITLE = "title";

    public static final String COL_OFFICE_PHONE = "office_phone";
    
    public static final String COL_CELL_PHONE = "cell_phone";
    
    public static final String COL_EMAIL = "email";
    
    public static final String COL_MANAGER_ID = "manager_id";

    public static final String TABLE_EMPLOYEE = "employee";

    public static final String DATABASE_NAME = "employee.db";

    private static final int DATABASE_VERSION = 1;

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
        /*
         * Create the employee table and populate it with sample data. In step 6, we will move these hardcoded statements to an XML document.
         */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_EMPLOYEE + " (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FIRST_NAME + " TEXT, "
                + COL_LAST_NAME + " TEXT, " + COL_TITLE + " TEXT, " + COL_OFFICE_PHONE + " TEXT, " + COL_CELL_PHONE + " TEXT, " + COL_EMAIL
                + " TEXT, " + COL_MANAGER_ID + " INTEGER)";
        db.execSQL(sql);

        ContentValues values = new ContentValues();

        values.put(COL_FIRST_NAME, "John");
        values.put(COL_LAST_NAME, "Smith");
        values.put(COL_TITLE, "CEO");
        values.put(COL_OFFICE_PHONE, "617-219-2001");
        values.put(COL_CELL_PHONE, "617-456-7890");
        values.put(COL_EMAIL, "jsmith@email.com");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

        values.put(COL_FIRST_NAME, "Robert");
        values.put(COL_LAST_NAME, "Jackson");
        values.put(COL_TITLE, "VP Engineering");
        values.put(COL_OFFICE_PHONE, "617-219-3333");
        values.put(COL_CELL_PHONE, "781-444-2222");
        values.put(COL_EMAIL, "rjackson@email.com");
        values.put(COL_MANAGER_ID, "1");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

        values.put(COL_FIRST_NAME, "Marie");
        values.put(COL_LAST_NAME, "Potter");
        values.put(COL_TITLE, "VP Sales");
        values.put(COL_OFFICE_PHONE, "617-219-2002");
        values.put(COL_CELL_PHONE, "987-654-3210");
        values.put(COL_EMAIL, "mpotter@email.com");
        values.put(COL_MANAGER_ID, "1");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

        values.put(COL_FIRST_NAME, "Lisa");
        values.put(COL_LAST_NAME, "Jordan");
        values.put(COL_TITLE, "VP Marketing");
        values.put(COL_OFFICE_PHONE, "617-219-2003");
        values.put(COL_CELL_PHONE, "987-654-7777");
        values.put(COL_EMAIL, "ljordan@email.com");
        values.put(COL_MANAGER_ID, "2");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

        values.put(COL_FIRST_NAME, "Christophe");
        values.put(COL_LAST_NAME, "Coenraets");
        values.put(COL_TITLE, "Evangelist");
        values.put(COL_OFFICE_PHONE, "617-219-0000");
        values.put(COL_CELL_PHONE, "617-666-7777");
        values.put(COL_EMAIL, "ccoenrae@adobe.com");
        values.put(COL_MANAGER_ID, "2");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

        values.put(COL_FIRST_NAME, "Paula");
        values.put(COL_LAST_NAME, "Brown");
        values.put(COL_TITLE, "Director Engineering");
        values.put(COL_OFFICE_PHONE, "617-612-0987");
        values.put(COL_CELL_PHONE, "617-123-9876");
        values.put(COL_EMAIL, "pbrown@email.com");
        values.put(COL_MANAGER_ID, "2");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

        values.put(COL_FIRST_NAME, "Mark");
        values.put(COL_LAST_NAME, "Taylor");
        values.put(COL_TITLE, "Lead Architect");
        values.put(COL_OFFICE_PHONE, "617-444-1122");
        values.put(COL_CELL_PHONE, "617-555-3344");
        values.put(COL_EMAIL, "mtaylor@email.com");
        values.put(COL_MANAGER_ID, "2");
        db.insert(TABLE_EMPLOYEE, COL_LAST_NAME, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS employees");
        onCreate(db);
    }

}
