package com.android.tuto.data.util;

import com.android.tuto.pref.PreferencesActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class to write to and read from shared preferences
 * 
 * @author minhducngo
 *
 */
public class SharedPreferencesUtil {
    /** the preferences */
    private SharedPreferences prefs;

    /**
     * 
     * @param context
     *            the application context
     */
    public SharedPreferencesUtil(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return minimum update frequency value
     */
    public int getMinumumManitude() {
        return Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
    }

    public int getUpdateFreq() {
        return Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
    }

    public boolean getAutoUpdate() {
        return prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
    }

}
