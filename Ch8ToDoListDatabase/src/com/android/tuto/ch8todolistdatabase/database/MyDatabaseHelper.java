package com.android.tuto.ch8todolistdatabase.database;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.android.tuto.ch8todolistdatabase.data.ToDoItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    /** The table todoitem within the database */
    private static final String TABLE_TODOITEM = "todoitem";

    /** The column id */
    public static final String KEY_ID = "_id";

    /** The column task */
    public static final String KEY_TASK = "task";

    /** The column created on */
    public static final String KEY_CREATED_ON = "created_on";

    /** The database name */
    private static final String DATABASE_NAME = "tododatabase.db";

    /** The database's version */
    private static final int DATABASE_VERSION = 1;

    /** The database create query */
    private static final String DATABASE_CREATE = "create table " + TABLE_TODOITEM + " (" + KEY_ID + " integer primary key autoincrement, "
            + KEY_TASK + " text not null, " + KEY_CREATED_ON + " long);";

    private MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * 
     * @param context
     *            the context
     * @param factory
     *            the cursor factory
     */
    public MyDatabaseHelper(Context context, CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log the version upgrade.
        Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        // The simplest case is to drop the old table and create a new one.
        db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_TODOITEM);
        // Create a new one.
        onCreate(db);
    }

    /**
     * to save new todoitem into db
     * 
     * @param item
     */
    public void addToDoItem(ToDoItem item) {
        ContentValues values = new ContentValues();
        values.put(KEY_TASK, item.getTask());
        values.put(KEY_CREATED_ON, item.getCreatedOn().getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TODOITEM, null, values);
        db.close();
    }

    public List<ToDoItem> getToDoItems() {
        List<ToDoItem> results = new ArrayList<ToDoItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODOITEM;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String task = cursor.getString(1);
                Date createdOn = new Date(cursor.getLong(2));
                ToDoItem item = new ToDoItem(task, createdOn);
                // Adding contact to list
                results.add(item);
            } while (cursor.moveToNext());
        }
        // cursor.close();
        // db.close();
        // return results
        return results;

    }
}
