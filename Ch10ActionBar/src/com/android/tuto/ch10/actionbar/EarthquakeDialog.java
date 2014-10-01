package com.android.tuto.ch10.actionbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.android.tuto.ch10.actionbar.data.Quake;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * The earth quake dialog
 * 
 * @author minhducngo
 *
 */
public class EarthquakeDialog extends DialogFragment {

    private static String DIALOG_STRING = "DIALOG_STRING";

    public static EarthquakeDialog newInstance(Context context, Quake quake) {
        EarthquakeDialog fragment = new EarthquakeDialog();

        Bundle args = new Bundle();
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy HH:mm:ss");
        String dateString = sdf.format(quake.getDate());
        String quakeText = dateString + "\n" + "Magnitude " + quake.getMagnitude() + "\n" + quake.getDetails() + "\n" + quake.getLink();
        args.putString(DIALOG_STRING, quakeText);

        fragment.setArguments(args);
        return fragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dl = super.onCreateDialog(savedInstanceState);
        dl.setTitle("Earthquake details");
        return dl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quake_details, container, false);
        String title = getArguments().getString(DIALOG_STRING);
        TextView textView = (TextView) view.findViewById(R.id.quakeDetailsTextView);
        textView.setText(title);

        return view;
    }

}
