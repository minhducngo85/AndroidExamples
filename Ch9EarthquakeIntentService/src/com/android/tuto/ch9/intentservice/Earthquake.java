package com.android.tuto.ch9.intentservice;

import com.android.tuto.ch9.intentservice.pref.PreferencesActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

public class Earthquake extends Activity {

    /** Preference menu index */
    private static final int MENU_PREFERENCES = Menu.FIRST + 1;

    /** acces code to show preference activity by intent */
    private static final int SHOW_PREFERENCES = 1;

    private static final int SHOW_SEARCH_RESULTS = 2;

    private static final String TAG = "EARTHQUAKE_SERVICE";

    private MyBroadcastReceiver myBroadcastReceiver;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "EarthQuake: onCreate(Bundle savedInstanceState) called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Bind the Activity's SearchableInfo to the Search View
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchableInfo);

        // update form
        updateFromPreferences();
        // register receiver
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter_update = new IntentFilter(EarthquakeUpdateService.ACTION_UPDATE);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter_update);

        // start service
        Earthquake.startService(EarthquakeUpdateService.class, this, "Update");

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
            earthquakeListFrag.refreshQuakes();

            // restart service
            if (autoUpdateChecked) {
                Earthquake.startService(EarthquakeUpdateService.class, this, "No_Update");
            } else {
                // stop alarm if Auto_Update = false
                stopAlarm();
            }
        }
    }

    public boolean autoUpdateChecked = false;

    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
    }

    /*
     * private MyBroadcastReceiver myBroadcastReceiver; (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    /**
     * to start service if it is not running
     * 
     * @param extraInfo
     *            the extra information
     * @param class1
     *            the service class
     * @param context
     *            the package context
     */
    private static void startService(Class<?> class1, Context context, String extraInfo) {
        if (isServiceRunning(class1, context)) {
            Log.d("EARTHQUAKE_SERVICE", "service started: " + class1.getName().toString());
            return;
        }
        Log.d("EARTHQUAKE_SERVICE", "start servie: " + class1.getName());
        Intent intent = new Intent(context, class1);
        intent.putExtra(EarthquakeUpdateService.EXTRA_KEY_INPUT, extraInfo);
        context.startService(intent);
    }

    private static boolean isServiceRunning(Class<?> class1, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (class1.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * to stop alarm
     */
    private void stopAlarm() {
        Log.d("EARTHQUAKE_SERVICE", "stopAlarm()");
        // cancel alarm intent
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String ALARM_ACTION = EarthquakeAlarmReceiver.ACTION_REFRESH_QUAKE_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentToFire, 0);
        alarmManager.cancel(alarmIntent);
    }

    /**
     * the receiver to update back the result after finishing db update
     * 
     * @author minhducngo
     *
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(EarthquakeUpdateService.EXTRA_KEY_UPDATE);
            Log.i(TAG, result);
            FragmentManager fm = getFragmentManager();
            EarthquakeListFragment earthquakeListFrag = (EarthquakeListFragment) fm.findFragmentById(R.id.EarthquakeListFragment);
            earthquakeListFrag.refreshQuakes();
        }
    }

}
