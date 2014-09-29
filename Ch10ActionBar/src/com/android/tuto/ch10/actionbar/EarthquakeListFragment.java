package com.android.tuto.ch10.actionbar;

import com.android.tuto.ch10.actionbar.pref.PreferencesActivity;
import com.android.tuto.ch10.actionbar.util.DatabaseHelper;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class EarthquakeListFragment extends ListFragment {
	/** The list adapter */
	private SimpleCursorAdapter adapter;

	/** String tag for debug mode */
	private static final String TAG = "EARTHQUAKE";

	/**
	 * Create view once activity created
	 */
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG,
				"EarthQuakeListFragment: onActivityCreated(Bundle savedInstanceState)called");
		super.onActivityCreated(savedInstanceState);
		// refreshQuakes();
		adapter = new SimpleCursorAdapter(this.getActivity(),
				android.R.layout.simple_list_item_1, null,
				new String[] { DatabaseHelper.COL_SUMMARY_3 },
				new int[] { android.R.id.text1 }, 0);
		setListAdapter(adapter);
	}

	/**
	 * reads quakes from db and update ui
	 */
	public void refreshQuakes() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		int minMagnitude = Integer.parseInt(prefs.getString(
				PreferencesActivity.PREF_MIN_MAG, "3"));

		DatabaseHelper dbHelper = DatabaseHelper.getInstance(this.getActivity()
				.getApplicationContext());
		Cursor c = dbHelper.searchQuakesAsRawData("", minMagnitude);
		adapter = new SimpleCursorAdapter(this.getActivity(),
				android.R.layout.simple_list_item_1, c,
				new String[] { DatabaseHelper.COL_SUMMARY_3 },
				new int[] { android.R.id.text1 }, 0);
		setListAdapter(adapter);
		// adapter.notifyDataSetChanged();
	}
}
