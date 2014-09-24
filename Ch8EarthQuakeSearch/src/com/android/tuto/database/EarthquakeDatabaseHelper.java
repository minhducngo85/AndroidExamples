package com.android.tuto.database;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.android.tuto.data.Quake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class EarthquakeDatabaseHelper extends SQLiteOpenHelper {

    // Column Names
    public static final String COL_ID = "_id";
    public static final String COL_DATE_1 = "date";
    public static final String COL_DETAILS_2 = "details";
    public static final String COL_SUMMARY_3 = "summary";
    public static final String COL_LOCATION_LAT_4 = "latitude";
    public static final String COL_LOCATION_LNG_5 = "longitude";
    public static final String COL_MAGNITUDE_6 = "magnitude";
    public static final String COL_LINK_7 = "link";

    private static final String DATABASE_NAME = "earthquakes.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_EARTHQUAKE = "earthquakes";

    private static final String DATABASE_CREATE = "create table " + TABLE_EARTHQUAKE + " (" + COL_ID + " integer primary key autoincrement, "
            + COL_DATE_1 + " INTEGER, " + COL_DETAILS_2 + " TEXT, " + COL_SUMMARY_3 + " TEXT, " + COL_LOCATION_LAT_4 + " FLOAT, "
            + COL_LOCATION_LNG_5 + " FLOAT, " + COL_MAGNITUDE_6 + " FLOAT, " + COL_LINK_7 + " TEXT);";

    private static final String TAG = "EARTHQUAKE";

    /**
     * 
     * @param context
     *            the application context
     * @param factorykey
     *            the cursor factory
     */
    public EarthquakeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EARTHQUAKE);
        onCreate(db);
    }

    /**
     * converts a not-null coursor to Quake object
     * 
     * @param cursor
     * @return
     */
    private Quake convertCursor(Cursor cursor) {
        Date date = new Date(cursor.getLong(1));
        String details = cursor.getString(2);
        Location location = new Location("dummyGPS");
        location.setLatitude(cursor.getDouble(4));
        location.setLongitude(cursor.getDouble(5));
        double magnitude = cursor.getDouble(6);
        String link = cursor.getString(7);
        Quake quake = new Quake(date, details, location, magnitude, link);
        quake.setId(cursor.getLong(0));
        Log.i(TAG, quake.toString());
        return quake;
    }

    /**
     * to find a quake by id
     * 
     * @param id
     * @return
     */
    public Quake findQuakeById(long id) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EARTHQUAKE + " WHERE " + COL_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return convertCursor(cursor);
        }
        return null;
    }

    /**
     * to find a quake by date
     * 
     * @param createdOn
     * @return
     */
    public Quake findQuakeByDate(long createdOn) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EARTHQUAKE + " WHERE " + COL_DATE_1 + " = " + createdOn;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return convertCursor(cursor);
        }
        return null;
    }

    public List<Quake> getAllQuakes() {
        List<Quake> quakes = new ArrayList<Quake>();
        String query = "SELECT * FROM " + TABLE_EARTHQUAKE + " ORDER BY " + COL_DATE_1 + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                quakes.add(convertCursor(cursor));
            } while (cursor.moveToNext());
        }
        return quakes;
    }

    public List<Quake> searchQuakes(String searchString) {
        List<Quake> quakes = new ArrayList<Quake>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EARTHQUAKE + " WHERE " + COL_SUMMARY_3 + " LIKE '%" + searchString + "%'" + " ORDER BY " + COL_DATE_1
                + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                quakes.add(convertCursor(cursor));
            } while (cursor.moveToNext());
        }
        return quakes;
    }

    /**
     * to save an entry into db
     * 
     * @param item
     */
    public void addQuake(Quake item) {
        ContentValues values = new ContentValues();
        values.put(COL_DATE_1, item.getDate().getTime());
        values.put(COL_DETAILS_2, item.getDetails());
        values.put(COL_LINK_7, item.getLink());
        values.put(COL_LOCATION_LAT_4, item.getLocation().getLatitude());
        values.put(COL_LOCATION_LNG_5, item.getLocation().getLongitude());
        values.put(COL_SUMMARY_3, item.toString());
        values.put(COL_MAGNITUDE_6, item.getMagnitude());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_EARTHQUAKE, null, values);
        db.close();
    }

}
