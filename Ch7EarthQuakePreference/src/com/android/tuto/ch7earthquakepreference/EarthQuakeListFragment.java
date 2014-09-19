package com.android.tuto.ch7earthquakepreference;

import java.util.ArrayList;
import java.util.List;

import com.android.tuto.ch7earthquakepreference.data.Quake;
import com.android.tuto.ch7earthquakepreference.util.FeedReader;
import com.android.tuto.ch7earthquakepreference.util.SharedPreferencesUtil;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class EarthQuakeListFragment extends ListFragment {

    /** The list adapter */
    private ArrayAdapter<Quake> adapter;

    /**
     * @return the adapter
     */
    public ArrayAdapter<Quake> getAdapter() {
        return adapter;
    }

    /** List resources */
    private List<Quake> earthquakes = new ArrayList<Quake>();

    /**
     * @return the earthquakes
     */
    public List<Quake> getEarthquakes() {
        return earthquakes;
    }

    /** String tag for debug mode */
    private static final String TAG = "EARTHQUAKE";

    /**
     * Create view once activity created
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "EarthQuakeListFragment: onActivityCreated(Bundle savedInstanceState)called");
        super.onActivityCreated(savedInstanceState);
        int layoutID = android.R.layout.simple_list_item_1;
        adapter = new ArrayAdapter<Quake>(this.getActivity(), layoutID, earthquakes);
        setListAdapter(adapter);

        /** download rss feeds */
        SharedPreferencesUtil prefsUil = new SharedPreferencesUtil(getActivity().getApplicationContext(), getResources());
        FeedReader task = new FeedReader(this, prefsUil.readMinMagnitudeValue());
        String quakeFeed = getString(R.string.quake_feed);
        task.execute(quakeFeed);
        Log.i(TAG, "Current Thread: " + Thread.currentThread().getName());
    }

}
