package com.android.tuto.ch10.actionbar;

import com.android.tuto.ch10.actionbar.pref.PreferencesActivity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.view.View;
import android.widget.SearchView;

public class Earthquake extends Activity {
    /** Preference menu index */
    private static final int MENU_PREFERENCES = Menu.FIRST + 1;

    /** access code to show preference activity by intent */
    private static final int SHOW_PREFERENCES = 1;

    private static final String TAG = "EARTHQUAKE_SERVICE";

    /** The constant key for extra information of intent to indicate it should update the list */
    public static final String UPDATE_LIST = "UPDATE_LIST";

    private MyBroadcastReceiver myBroadcastReceiver;

    /** The list tab listener */
    private TabListener<EarthquakeListFragment> listTabListener;

    /** The map tab listener */
    private TabListener<EarthquakeMapFragment> mapTabListener;

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

        /** tab navigation */
        ActionBar actionBar = getActionBar();
        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);

        // use tablet navigation
        boolean tabletLayout = fragmentContainer == null;
        if (!tabletLayout) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);
            // creates and adds tab and its listener
            Tab listTab = actionBar.newTab();
            listTabListener = new TabListener<EarthquakeListFragment>(this, EarthquakeListFragment.class, R.id.EarthquakeFragmentContainer);
            listTab.setText("List").setContentDescription("List of earthquakes").setTabListener(listTabListener);
            actionBar.addTab(listTab);

            Tab mapTab = actionBar.newTab();
            mapTabListener = new TabListener<EarthquakeMapFragment>(this, EarthquakeMapFragment.class, R.id.EarthquakeFragmentContainer);
            mapTab.setText("Map").setContentDescription("Map of earthquakes").setTabListener(mapTabListener);
            actionBar.addTab(mapTab);
        }

        // register receiver
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter_update = new IntentFilter(EarthquakeUpdateService.ACTION_UPDATE);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter_update);

        // start service
        Earthquake.startService(EarthquakeUpdateService.class, this, UPDATE_LIST);

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

            // restart service
            Earthquake.startService(EarthquakeUpdateService.class, this, UPDATE_LIST);

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
     * the receiver to update back the result after finishing db update
     * 
     * @author minhducngo
     *
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // in this case, we don't need the extra output key info.
            String result = intent.getStringExtra(EarthquakeUpdateService.EXTRA_KEY_OUTPUT);
            Log.i(TAG, result);

            View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);
            boolean tabletLayout = fragmentContainer == null;

            FragmentManager fm = getFragmentManager();
            if (!tabletLayout) {
                // Find the recreated Fragments and assign them to their associated Tab Listeners.
                EarthquakeListFragment listFragment = (EarthquakeListFragment) fm.findFragmentByTag(EarthquakeListFragment.class.getName());
                listFragment.refreshQuakes();
            } else {
                EarthquakeListFragment earthquakeListFrag = (EarthquakeListFragment) fm.findFragmentById(R.id.EarthquakeListFragment);
                earthquakeListFrag.refreshQuakes();
            }
        }
    }

    private static String ACTION_BAR_INDEX = "ACTION_BAR_INDEX";

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout = fragmentContainer == null;
        if (!tabletLayout) {
            // save current tab
            int actionBarIndex = getActionBar().getSelectedTab().getPosition();
            SharedPreferences.Editor editor = getPreferences(Activity.MODE_PRIVATE).edit();
            editor.putInt(ACTION_BAR_INDEX, actionBarIndex);
            editor.apply();

            // detach fragments
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (mapTabListener.fragment != null) {
                ft.detach(mapTabListener.fragment);
            }
            if (listTabListener.fragment != null) {
                ft.detach(listTabListener.fragment);
            }
            ft.commit();
        }

        super.onSaveInstanceState(outState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout = fragmentContainer == null;
        if (!tabletLayout) {
            SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex = sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout = fragmentContainer == null;

        if (!tabletLayout) {
            // Find the recreated Fragments and assign them to their associated Tab Listeners.
            listTabListener.fragment = getFragmentManager().findFragmentByTag(EarthquakeListFragment.class.getName());
            mapTabListener.fragment = getFragmentManager().findFragmentByTag(EarthquakeMapFragment.class.getName());

            // Restore the previous Action Bar tab selection.
            SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex = sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }

    /**
     * the navigation support between the list and map
     * 
     * @author minhducngo
     *
     * @param <T>
     */
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {

        /** The fragment */
        private Fragment fragment;

        /** The activity */
        private Activity activity;

        /** The fragment class */
        private Class<T> fragmentClass;

        /** The fragment container id */
        private int fragmentContainer;

        /**
         * The constructor
         * 
         * @param activity
         * @param fragmentClass
         * @param fragmentContainer
         */
        public TabListener(Activity activity, Class<T> fragmentClass, int fragmentContainer) {
            super();
            this.activity = activity;
            this.fragmentClass = fragmentClass;
            this.fragmentContainer = fragmentContainer;
        }

        /**
         * when a tab has been selected
         */
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (fragment == null) {
                String fragmentName = fragmentClass.getName();
                fragment = Fragment.instantiate(activity, fragmentName);
                ft.add(fragmentContainer, fragment, fragmentName);
            } else {
                ft.attach(fragment);
            }

        }

        /**
         * called on the currently selected tab when a different tab is selected
         */
        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (fragment != null) {
                ft.detach(fragment);
            }

        }

        /**
         * called when the selected tab is selected
         */
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            if (fragment != null) {
                ft.attach(fragment);
            }

        }
    }
}
