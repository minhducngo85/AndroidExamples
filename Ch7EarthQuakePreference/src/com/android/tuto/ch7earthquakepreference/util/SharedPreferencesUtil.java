package com.android.tuto.ch7earthquakepreference.util;

import com.android.tuto.ch7earthquakepreference.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
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

    /** the constant string to hold preference auto update */
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";

    /** the constant string to hold minimum magnitude index */
    public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";

    /** the constant string to hold update frequency index */
    public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";

    /** The android resources */
    private Resources resources;

    /**
     * 
     * @param context
     *            the application context
     */
    public SharedPreferencesUtil(Context context, Resources r) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.resources = r;
    }

    /**
     * @return minimum magnitude index
     */
    public int readMinMagnitudeIndex() {
        int index = prefs.getInt(PREF_MIN_MAG_INDEX, 0);
        if (index < 0) {
            return 0;
        }
        return index;
    }

    /**
     * @return minimum magnitude value
     */
    public int readMinMagnitudeValue() {
        int index = readMinMagnitudeIndex();
        String[] minMagValues = resources.getStringArray(R.array.magnitude_values);
        return Integer.valueOf(minMagValues[index]);
    }

    /**
     * @return minimum magnitude index
     */
    public int readUpdateFreqIndex() {
        int index = prefs.getInt(PREF_UPDATE_FREQ_INDEX, 0);
        if (index < 0) {
            return 0;
        }
        return index;
    }

    /**
     * @return minimum update frequency value
     */
    public int readUpdateFreqValue() {
        int index = readUpdateFreqIndex();
        String[] values = resources.getStringArray(R.array.update_freq_values);
        return Integer.valueOf(values[index]);
    }

    public void updatePreferences(boolean isAutoUpdate, int minMagIndex, int updateFreqIndex) {
        /**
         * writes to shared preferences
         */
        Editor editor = prefs.edit();
        editor.putBoolean(PREF_AUTO_UPDATE, isAutoUpdate);
        editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);
        editor.putInt(PREF_UPDATE_FREQ_INDEX, updateFreqIndex);
        editor.commit();
    }

    /**
     * @return auto update check box status
     */
    public boolean readAutoUpdate() {
        return prefs.getBoolean(PREF_AUTO_UPDATE, false);
    }
}
