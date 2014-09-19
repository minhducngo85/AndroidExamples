package com.android.tuto.ch7earthquakepreference;

import com.android.tuto.ch7earthquakepreference.util.SharedPreferencesUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class PreferencesActivity extends Activity {

    /** The auto-update check box */
    private CheckBox autoUpdate;

    /** The spinner for update frequency */
    private Spinner updateFreqSpinner;

    /** The spinner for magnitude lower limit */
    private Spinner magnitudeSpinner;

    /** The shared preferences util */
    private SharedPreferencesUtil prefsUtil;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        prefsUtil = new SharedPreferencesUtil(getApplicationContext(), getResources());

        updateFreqSpinner = (Spinner) findViewById(R.id.spinner_update_freq);
        magnitudeSpinner = (Spinner) findViewById(R.id.spinner_quake_mag);
        autoUpdate = (CheckBox) findViewById(R.id.checkbox_auto_update);

        populateSpinners();
        updateUiFromPrefs();

        // listeners for ok and cancel buttons
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            /**
             * @{inheritDoc
             */
            @Override
            public void onClick(View v) {
                saveSharedPrefs();
                PreferencesActivity.this.setResult(RESULT_OK);
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 
             */
            @Override
            public void onClick(View v) {
                PreferencesActivity.this.setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * to populate the spinners in the view.
     */
    private void populateSpinners() {
        // populate the update frequency spinner
        ArrayAdapter<CharSequence> fAdapter = ArrayAdapter
                .createFromResource(this, R.array.update_freq_options, android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateFreqSpinner.setAdapter(fAdapter);

        // populate the minimum magnitude spinner
        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this, R.array.magnitude_options, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magnitudeSpinner.setAdapter(mAdapter);
    }

    /**
     * to save preferences
     */
    private void saveSharedPrefs() {
        int updateFreqIndex = updateFreqSpinner.getSelectedItemPosition();
        int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
        boolean isAutoUpdate = autoUpdate.isChecked();
        /**
         * updates shared preferences
         */
        prefsUtil.updatePreferences(isAutoUpdate, minMagIndex, updateFreqIndex);
    }

    /**
     * updates ui by using preferences at the start.
     */
    private void updateUiFromPrefs() {
        // auto update value
        boolean autoChecked = prefsUtil.readAutoUpdate();
        autoUpdate.setChecked(autoChecked);
        updateFreqSpinner.setSelection(prefsUtil.readUpdateFreqIndex());
        magnitudeSpinner.setSelection(prefsUtil.readMinMagnitudeIndex());
    }
}
