package com.android.tuto.pref;

import com.android.tuto.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * for Android API prior Android 3.0 (API level 11)
 * 
 * @author minhducngo
 *
 */
public class PreferencesActivity extends PreferenceActivity {

	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";

	public static final String PREF_MIN_MAG = "PREF_MIN_MAG";

	public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

	@SuppressWarnings("deprecation")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
	}

}
