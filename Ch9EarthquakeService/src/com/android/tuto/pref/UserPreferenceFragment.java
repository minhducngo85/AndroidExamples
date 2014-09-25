package com.android.tuto.pref;

import com.android.tuto.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * for API level 11 onwards
 * 
 * @author minhducngo
 *
 */
public class UserPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }

}
