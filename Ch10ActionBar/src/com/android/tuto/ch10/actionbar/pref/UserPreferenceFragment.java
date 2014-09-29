package com.android.tuto.ch10.actionbar.pref;



import com.android.tuto.ch10.actionbar.R;

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
