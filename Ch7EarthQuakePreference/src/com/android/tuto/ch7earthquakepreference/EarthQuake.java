package com.android.tuto.ch7earthquakepreference;

import com.android.tuto.ch7earthquakepreference.util.FeedReader;
import com.android.tuto.ch7earthquakepreference.util.SharedPreferencesUtil;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class EarthQuake extends Activity {

    /** Preference menu index */
    private static final int MENU_PREFERENCES = Menu.FIRST + 1;

    /** acces code to show preference activity by intent */
    private static final int SHOW_PREFERENCES = 1;

    private static final String TAG = "EARTHQUAKE";

    private SharedPreferencesUtil prefsUtil;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "EarthQuake: onCreate(Bundle savedInstanceState) called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Context context = getApplicationContext();
        prefsUtil = new SharedPreferencesUtil(context, getResources());
        readSharedPreferences();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == MENU_PREFERENCES) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivityForResult(intent, SHOW_PREFERENCES);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("EARTHQUAKE", "onActivityResult() called");
        if (requestCode == SHOW_PREFERENCES)
            if (resultCode == Activity.RESULT_OK) {
                readSharedPreferences();
                FragmentManager fm = getFragmentManager();
                EarthQuakeListFragment earthquakeList = (EarthQuakeListFragment) fm.findFragmentById(R.id.EarthQuakeListFragment);
                /** download rss feeds */
                FeedReader task = new FeedReader(earthquakeList, minimumMagnitude);
                String quakeFeed = getString(R.string.quake_feed);
                task.execute(quakeFeed);
            }
    }

    public int minimumMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

    /**
     * reads shared preferences
     */
    private void readSharedPreferences() {
        autoUpdateChecked = prefsUtil.readAutoUpdate();
        minimumMagnitude = prefsUtil.readMinMagnitudeValue();
        updateFreq = prefsUtil.readUpdateFreqValue();
    }

}
