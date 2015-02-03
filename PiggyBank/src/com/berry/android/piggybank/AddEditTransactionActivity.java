package com.berry.android.piggybank;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.berry.android.piggybank.data.Account;
import com.berry.android.piggybank.data.Constants;
import com.berry.android.piggybank.data.Transaction;
import com.berry.android.piggybank.database.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class AddEditTransactionActivity extends Activity {

    private static final String TAG = "PIGGY_BANK";

    static final int DATE_DIALOG_ID = 999;

    /** the date text view */
    private EditText dateEditText;

    /** The asset spinner */
    private Spinner assetSpinner;

    /** The asset spinner */
    private Spinner typeSpinner;

    /** type adapter */
    private ArrayAdapter<String> typeAdapter;

    /** asset adapter */
    private ArrayAdapter<String> assetAdapter;

    /** The amount edit text */
    private EditText amountEditText;

    /** The description edit text */
    private EditText descriptionEditText;

    /** asset list from database */
    private List<Account> assets;

    /** The simple date format */
    private final SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();

    /** The data base helper */
    private DatabaseHelper dbHelper;

    /**
     * on view create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "add new transaction");
        setContentView(R.layout.add_edit_transaction);

        sdf.applyPattern(Constants.DATE_FORMAT);
        dbHelper = DatabaseHelper.getInstance(this);

        // edit text date
        dateEditText = (EditText) findViewById(R.id.add_transaction_date);
        dateEditText.setText(sdf.format(new Date()));
        dateEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatePickerDialog().show();
            }
        });

        // asset spinner
        assets = dbHelper.convertCursorToAccounts(dbHelper.getAccounts());
        assetSpinner = (Spinner) findViewById(R.id.add_transaction_asset);
        List<String> assetNames = new ArrayList<String>();
        for (Account a : assets) {
            assetNames.add(a.getName());
        }
        assetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, assetNames);
        assetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assetSpinner.setAdapter(assetAdapter);

        // type
        typeSpinner = (Spinner) findViewById(R.id.add_transaction_type);
        typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.TRANSACTION_TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        // description
        descriptionEditText = (EditText) findViewById(R.id.add_transaction_description);
        // amount
        amountEditText = (EditText) findViewById(R.id.add_transaction_amount);

        Intent intent = getIntent();

        String type = intent.getExtras().getString(Constants.TYPE);
        if (type != null && type.equals(Constants.ACTION_EDIT)) {
            // edit the transaction
            setTitle("Edit Transaction");
            int id = intent.getExtras().getInt(Constants.ID);
            Transaction transaction = dbHelper.convertCursorToTransaction(dbHelper.findTransaction(id)).get(0);
            typeSpinner.setSelection(typeAdapter.getPosition(transaction.getType()));
            dateEditText.setText(sdf.format(transaction.getDate()));
            String selectedAsset = "";
            for (Account asset : assets) {
                if (asset.getId() == transaction.getAssetId()) {
                    selectedAsset = asset.getName();
                }
            }
            assetSpinner.setSelection(assetAdapter.getPosition(selectedAsset));
            amountEditText.setText(Float.toString(Math.abs(transaction.getAmout())));
            descriptionEditText.setText(transaction.getDescription());

            // make delete button visible
            Button deleteBtn = (Button) findViewById(R.id.delete);
            deleteBtn.setVisibility(1);
            deleteBtn.setTextColor(getResources().getColor(R.color.red));

            Button okBtn = (Button) findViewById(R.id.ok);
            okBtn.setText("Update");
        } else if (type != null) {
            // add new
            setTitle("Add Transaction");
            int pos = typeAdapter.getPosition(type);
            typeSpinner.setSelection(pos);
        }

    }

    /**
     * to save a transaction
     * 
     * @param view
     */
    public void saveTransaction(View view) {
        Transaction trans = new Transaction();
        String amountString = amountEditText.getText().toString().trim();
        float amount = 0;
        try {
            amount = Float.valueOf(amountString);
        } catch (NumberFormatException e) {
            Log.d(TAG, "Cannot convert amount to float: " + e.getMessage());
        }
        trans.setAmout(amount);
        // type
        String type = typeSpinner.getSelectedItem().toString();
        trans.setType(type);
        Date date = new Date();
        try {
            date = sdf.parse(dateEditText.getText().toString());
        } catch (ParseException e) {
            Log.d(TAG, "Cannot convert String to date: " + e.getMessage());
        }
        trans.setDate(date);
        trans.setDescription(descriptionEditText.getText().toString());

        // asset
        Account asset = assets.get(assetSpinner.getSelectedItemPosition());
        trans.setAssetId(asset.getId());

        if (trans.getAmout() == 0 || trans.getDescription() == null || trans.getDescription().isEmpty()) {
            showAlertDialog("Invalid inputs", "Please check your amount and description.");
        } else if (trans.getAmout() < 0
                && (trans.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE) || trans.getType().equals(Constants.TRANSACTION_TYPE_INCOME))) {
            showAlertDialog("Invalid inputs", "Amount must not be negative.");
        } else {
            if (trans.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
                trans.setAmout(trans.getAmout() * (-1));
            }
            if (getIntent().getExtras().getString(Constants.TYPE).equals(Constants.ACTION_EDIT)) {
                trans.setId(getIntent().getExtras().getInt(Constants.ID));
                dbHelper.updateTransaction(trans);
            } else {
                // add new
                dbHelper.saveTransaction(trans);
            }
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    /**
     * to save a transaction
     * 
     * @param view
     */
    public void cancel(View view) {
        this.setResult(RESULT_CANCELED);
        this.finish();
    }

    /**
     * to save a transaction
     * 
     * @param view
     */
    public void deleteTransaction(View view) {
        dbHelper.deleteTransaction(getIntent().getExtras().getInt(Constants.ID));
        this.setResult(RESULT_OK);
        this.finish();
    }

    /**
     * show a dialog message
     * 
     * @param title
     *            the dialog title
     * @param message
     *            the dialog message
     */
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            /**
             * click on ok button
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDlg = builder.create();
        alertDlg.show();
    }

    /**
     * show date picker dialog
     */
    private DatePickerDialog createDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(this, datePickerListener, year, month, day);
        return dlg;
    }

    /**
     * The date listener
     */
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            Calendar c = Calendar.getInstance();
            c.set(selectedYear, selectedMonth, selectedDay);
            dateEditText.setText(sdf.format(c.getTime()));
        }
    };

}
