package com.android.tuto.ch8todolistdatabase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ToDoContentProvider extends ContentProvider {

    private static final String PACKAGE_STRING = "com.android.tuto.todoprovider";

    /** Content provider URI: this value should be unique */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PACKAGE_STRING + "/todoitems");

    /** The column id */
    public static final String KEY_ID = "_id";

    /** The column task */
    public static final String KEY_TASK = "task";

    /** The column created on */
    public static final String KEY_CREATED_ON = "created_on";

    /** The SQLiteOpenHelper */
    private MySQLiteOpenHelper mySQlHelper;

    /** The database name */
    private static final String DATABASE_NAME = "tododatabase.db";

    /** The database's version */
    private static final int DATABASE_VERSION = 1;

    private static final int ALLROWS = 1;

    private static final int SINGLE_ROW = 2;

    @Override
    public boolean onCreate() {
        mySQlHelper = new MySQLiteOpenHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mySQlHelper.getWritableDatabase();

        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.TABLE_TODOITEM);

        if (uriMatcher.match(uri) == SINGLE_ROW) {
            String rowId = uri.getPathSegments().get(1);
            queryBuilder.appendWhere(KEY_ID + "=" + rowId);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
        return cursor;
    }

    private static final UriMatcher uriMatcher;

    // Populate the UriMatcher object, where a URI ending in ‘todoitems’ will
    // correspond to a request for all items, and ‘todoitems/[rowID]’
    // represents a single row.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PACKAGE_STRING, "todoitems", ALLROWS);
        uriMatcher.addURI(PACKAGE_STRING, "todoitems/#", SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // Return a string that identifies the MIME type for a Content Provider URI
        switch (uriMatcher.match(uri)) {
        case ALLROWS:
            return "vnd.android.cursor.dir/com.android.tuto.todos";
        case SINGLE_ROW:
            return "vnd.android.cursor.item/com.android.tuto.todos";
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = mySQlHelper.getWritableDatabase();

        // To add empty rows to your database by passing in an empty Content Values
        // object, you must use the null column hack parameter to specify the name of
        // the column that can be set to null.
        String nullColumnHack = null;

        // Insert the values into the table
        long id = db.insert(MySQLiteOpenHelper.TABLE_TODOITEM, nullColumnHack, values);

        if (id > -1) {
            // Construct and return the URI of the newly inserted row.
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
            // Notify any observers of the change in the data set.
            getContext().getContentResolver().notifyChange(insertedId, null);
            return insertedId;
        } else
            return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mySQlHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(uri)) {
        case SINGLE_ROW:
            String rowID = uri.getPathSegments().get(1);
            selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        default:
            break;
        }

        // To return the number of deleted items, you must specify a where
        // clause. To delete all rows and return a value, pass in "1".
        if (selection == null)
            selection = "1";

        // Execute the deletion.
        int deleteCount = db.delete(MySQLiteOpenHelper.TABLE_TODOITEM, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = mySQlHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(uri)) {
        case SINGLE_ROW:
            String rowID = uri.getPathSegments().get(1);
            selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        default:
            break;
        }

        // Perform the update.
        int updateCount = db.update(MySQLiteOpenHelper.TABLE_TODOITEM, values, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    /**
     * my SQLiteOpenHelper
     * 
     * @author minhducngo
     *
     */
    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        /** The table todoitem within the database */
        private static final String TABLE_TODOITEM = "todoitem";

        /** The database create query */
        private static final String DATABASE_CREATE = "create table " + TABLE_TODOITEM + " (" + KEY_ID + " integer primary key autoincrement, "
                + KEY_TASK + " text not null, " + KEY_CREATED_ON + " long);";

        public MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Log the version upgrade.
            Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

            // Upgrade the existing database to conform to the new version. Multiple
            // previous versions can be handled by comparing oldVersion and newVersion
            // values.

            // The simplest case is to drop the old table and create a new one.
            db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_TODOITEM);
            // Create a new one.
            onCreate(db);
        }

    }

}
