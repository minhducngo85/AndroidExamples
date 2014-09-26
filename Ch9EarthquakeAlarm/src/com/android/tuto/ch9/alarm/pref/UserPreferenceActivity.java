package com.android.tuto.ch9.alarm.pref;

import java.util.List;

import com.android.tuto.ch9.alarm.R;

import android.preference.PreferenceActivity;

public class UserPreferenceActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.preference.PreferenceActivity#isValidFragment(java.lang.String)
	 */
	@Override
	protected boolean isValidFragment(String fragmentName) {
		// return super.isValidFragment(fragmentName);
		if (UserPreferenceFragment.class.getName().equals(fragmentName)) {
			return true;
		}
		return false;
	}

}