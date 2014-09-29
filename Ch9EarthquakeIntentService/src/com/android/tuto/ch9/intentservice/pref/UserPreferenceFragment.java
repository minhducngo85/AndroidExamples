package com.android.tuto.ch9.intentservice.pref;


import com.android.tuto.ch9.intentservice.R;

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
