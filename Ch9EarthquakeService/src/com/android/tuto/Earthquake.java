package com.android.tuto;

import com.android.tuto.pref.PreferencesActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Earthquake extends Activity {
    /** Preference menu index */
    private static final int MENU_PREFERENCES = Menu.FIRST + 1;

    /** acces code to show preference activity by intent */
    private static final int SHOW_PREFERENCES = 1;

    private static final int SHOW_SEARCH_RESULTS = 2;

    private static final String TAG = "EARTHQUAKE";

    SharedPreferences prefs;

    private TextView searchView;

    Intent serviceIntent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "EarthQuake: onCreate(Bundle savedInstanceState) called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        searchView = (TextView) findViewById(R.id.searchText);
        searchView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    openSearchResults();
                    return true;
                }
                return false;
            }
        });

        Context context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        updateFromPreferences();
        serviceIntent = new Intent(this, EarthquakeUpdateService.class);
        startService(serviceIntent);
    }

    private void openSearchResults() {
        String searchString = searchView.getText().toString().trim();
        Intent intent = new Intent(this, EarthquakeSearchResults.class);
        intent.putExtra("SEARCH_STRING", searchString);
        startActivityForResult(intent, SHOW_SEARCH_RESULTS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
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
            /**
             * defines to use preference fragment(>= API level 11) or preference activity
             */
            // Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
            // PreferencesActivity.class : UserPreferenceActivity.class;
            // Intent intent = new Intent(this, c);
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivityForResult(intent, SHOW_PREFERENCES);
            return true;
        }
        return false;
    }

    /**
     * deals with result returned back from intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("EARTHQUAKE", "onActivityResult() called");
        if (requestCode == SHOW_PREFERENCES) {
            updateFromPreferences();
            FragmentManager fm = getFragmentManager();
            EarthquakeListFragment earthquakeListFrag = (EarthquakeListFragment) fm.findFragmentById(R.id.EarthquakeListFragment);
            /** download rss feeds */
            earthquakeListFrag.refreshQuakes();
            // restart service

            stopService(serviceIntent);
            startService(serviceIntent);

        } else if (requestCode == SHOW_SEARCH_RESULTS) {
            searchView.setText("");
        }
    }

    public int minimumMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

    private void updateFromPreferences() {
        minimumMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
    }

}
