/**
 * copyright 2014, wunsch85@gmail.com
 */
package com.berry.android.piggybank.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.berry.android.piggybank.data.Account;
import com.berry.android.piggybank.data.Transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The database helper class
 * 
 * @author minhducngo
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** The constant to hold database name */
    public static final String DATABASE_NAME = "berry_piggybank.db";

    /** The transaction table name */
    public static final String TABLE_TRANSACTION = "money_transaction";

    /** The asset table name */
    public static final String TABLE_ACCOUNT = "account";

    /** The column type */
    public static final String COL_TYPE = "type";

    /** The date time */
    public static final String COL_DATE = "date";

    /** The amount of money */
    public static final String COL_AMOUNT = "amount";

    /** The column name */
    public static final String COL_NAME = "name";

    /** The description column */
    public static final String COL_DESCRIPTION = "description";

    /** The search colum */
    public static final String COL_SEARCH = "search";

    /** The search colum */
    public static final String COL_ACCOUNT_ID = "account_id";

    /** The column id */
    public static final String COL_ID = "_id";

    /** The database version */
    private static final int DATABASE_VERSION = 1;

    /** The application context */
    protected Context context;

    /** The single tone object */
    private static DatabaseHelper instance = null;

    /**
     * Constructor
     * 
     * @param context
     *            the application context
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * The single tone object
     * 
     * @param contextE
     *            /AndroidRuntime( 1766): Caused by: android.database.sqlite.SQLiteException: near "transaction": syntax error (code 1): , while
     *            compiling: CREATE TABLE IF NOT EXISTS transaction (_id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR(50), amount DECIMAL(14,2),
     *            description VARCHAR(200), search VARCHAR(300))
     * 
     *            the application context
     * @return the current instance
     */
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /** creates transaction table */
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION + " (";
        // id column
        sql += COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        // type column: income, outcome
        sql += COL_TYPE + " VARCHAR(50), ";
        // amount column
        sql += COL_AMOUNT + " DECIMAL(15,2), ";
        // date column
        sql += COL_DATE + " INTEGER, ";
        // description column
        sql += COL_DESCRIPTION + " VARCHAR(200), ";
        // asset column
        sql += COL_ACCOUNT_ID + " INTEGER, ";
        // search column
        sql += COL_SEARCH + " VARCHAR(300))";
        db.execSQL(sql);

        /** creates asset table */
        sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT + " (";
        // id column
        sql += COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        // name column
        sql += COL_NAME + " VARCHAR(50))";
        db.execSQL(sql);

        sql = "INSERT INTO " + TABLE_ACCOUNT + " VALUES(1,'Cash')";
        db.execSQL(sql);
        sql = "INSERT INTO " + TABLE_ACCOUNT + " VALUES(2,'Bank Account')";
        db.execSQL(sql);
        sql = "INSERT INTO " + TABLE_ACCOUNT + " VALUES(3,'Investments')";
        db.execSQL(sql);
        sql = "INSERT INTO " + TABLE_ACCOUNT + " VALUES(4,'Credit')";
        db.execSQL(sql);
        sql = "INSERT INTO " + TABLE_ACCOUNT + " VALUES(5,'Other')";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        onCreate(db);
    }

    /**
     * converts cursor to list of assets
     * 
     * @param cursor
     *            the onject to be converted
     * @return list of asset
     */
    public List<Account> convertCursorToAccounts(Cursor cursor) {
        List<Account> assets = new ArrayList<Account>();
        if (cursor.moveToFirst()) {
            do {
                Account asset = new Account();
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                asset.setId(id);
                String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                asset.setName(name);
                assets.add(asset);
            } while (cursor.moveToNext());
        }
        return assets;
    }

    /**
     * converts cursor to list of assets
     * 
     * @param cursor
     *            the onject to be converted
     * @return list of asset
     */
    public List<Transaction> convertCursorToTransaction(Cursor cursor) {
        List<Transaction> results = new ArrayList<Transaction>();
        if (cursor.moveToFirst()) {
            do {
                Transaction trans = new Transaction();
                trans.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                trans.setAmout(cursor.getFloat(cursor.getColumnIndex(COL_AMOUNT)));
                trans.setAssetId(cursor.getInt(cursor.getColumnIndex(COL_ACCOUNT_ID)));
                trans.setDate(new Date(cursor.getLong(cursor.getColumnIndex(COL_DATE))));
                trans.setDescription(cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION)));
                trans.setType(cursor.getString(cursor.getColumnIndex(COL_TYPE)));
                results.add(trans);
            } while (cursor.moveToNext());
        }
        return results;
    }

    /**
     * search transaction from database *
     * 
     * @param searchString
     *            the search string. in case, the search string is equal to null or empty, then returns all rows
     * @return the raw data
     */
    public Cursor searchTransactions(String searchString) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (searchString == null || searchString.trim().isEmpty()) {
            return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " ORDER BY " + COL_DATE + " DESC", null);
        }
        String sql = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COL_SEARCH + " LIKE ?" + " ORDER BY " + COL_DATE + " DESC";
        Cursor cursor = db.rawQuery(sql, new String[] { "%" + searchString + "%" });
        return cursor;
    }

    /**
     * search transaction from database
     * 
     * @param searchString
     *            the search string. in case, the search string is equal to null or empty, then returns all rows
     * @return the raw data
     */
    public Cursor getTransactions(long from, long to) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COL_DATE + " >= " + from + " AND " + COL_DATE + " <= " + to + " ORDER BY "
                + COL_DATE + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    /**
     * search transaction from database
     * 
     * @param searchString
     *            the search string. in case, the search string is equal to null or empty, then returns all rows
     * @return the raw data
     */
    public Cursor getTransactions(int accountId, long from, long to) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COL_ACCOUNT_ID + " = " + accountId + " AND " + COL_DATE + " >= " + from
                + " AND " + COL_DATE + " <= " + to + " ORDER BY " + COL_DATE + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    /**
     * find transaction by id
     * 
     * @param id
     *            the row id
     * @return cursor type
     */
    public Cursor findTransaction(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COL_ID + " = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    /**
     * 
     * @return all assets from database
     */
    public Cursor getAccounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT, null);
    }

    /**
     * to execute a read query
     * 
     * @return cursor
     */
    public Cursor executeReadQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    /**
     * to save an entry into db
     * 
     * @param item
     */
    public void saveTransaction(Transaction item) {
        if (item.getDescription() != null && item.getDescription().length() > 200) {
            item.setDescription(item.getDescription().substring(0, 200));
        }
        ContentValues values = new ContentValues();
        values.put(COL_DATE, item.getDate().getTime());
        values.put(COL_TYPE, item.getType());
        values.put(COL_AMOUNT, item.getAmout());
        values.put(COL_DESCRIPTION, item.getDescription());
        values.put(COL_ACCOUNT_ID, item.getAssetId());
        values.put(COL_SEARCH, item.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TRANSACTION, null, values);
        db.close();
    }

    /**
     * to save an entry into db
     * 
     * @param item
     */
    public void updateTransaction(Transaction item) {
        if (item.getDescription() != null && item.getDescription().length() > 200) {
            item.setDescription(item.getDescription().substring(0, 200));
        }
        ContentValues values = new ContentValues();
        values.put(COL_DATE, item.getDate().getTime());
        values.put(COL_TYPE, item.getType());
        values.put(COL_AMOUNT, item.getAmout());
        values.put(COL_DESCRIPTION, item.getDescription());
        values.put(COL_ACCOUNT_ID, item.getAssetId());
        values.put(COL_SEARCH, item.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_TRANSACTION, values, COL_ID + " = " + item.getId(), null);
        db.close();
    }

    public void deleteTransaction(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, COL_ID + " = " + id, null);
        db.close();
    }

    /**
     * gets accoutn balance
     * 
     * @param accountId
     *            the account id
     * @return the current balance
     */
    public float getAccountBalance(int accountId) {
        float balance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT sum(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTION + " WHERE " + COL_ACCOUNT_ID + " = " + accountId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            balance = cursor.getFloat(0);
        }
        return balance;
    }
}
