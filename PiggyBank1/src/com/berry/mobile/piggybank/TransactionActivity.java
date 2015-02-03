/**
 * copyright 2014, wunsch85@gmail.com
 */
package com.berry.mobile.piggybank;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.berry.mobile.piggybank.data.Asset;
import com.berry.mobile.piggybank.data.Constants;
import com.berry.mobile.piggybank.data.Transaction;
import com.berry.mobile.piggybank.database.DatabaseHelper;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The transaction view
 * 
 * @author minhducngo
 *
 */
public class TransactionActivity extends ListActivity {

    /** Teh currency symbol */
    private String currencySymbol = "€";

    private static final String TAG = "PIGGY_BANK";

    /** The total income text view */
    private EditText incomeEditText;

    /** The total expense */
    private EditText expenseEditText;

    /** Your current balance */
    private EditText totalEditText;

    /** From date */
    private EditText fromDateEditText;

    /** to date */
    private EditText toDateEditText;

    /** The list adapter */
    private TransactionAdapter transactionAdapter;

    /** The data base helper */
    private DatabaseHelper dbHelper;

    /** The money format */
    private DecimalFormat moneyFormat;

    /** The list of transaction */
    private List<Transaction> transactions;

    /** The map of asset */
    private Map<Integer, Asset> assetsMap;

    /** The total income */
    private float totalIncome = 0;

    /** The total expense */
    private float totalExpense = 0;

    /** the code to show from date picker */
    private final int FROM_DATE_PICKER_CODE = 0;

    /** the code to show from date picker */
    private final int TO_DATE_PICKER_CODE = 1;

    private SimpleDateFormat sdf;

    /** The access code to show AddTransactionActivity */
    private static final int SHOW_ADD_TRANSACTION_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "main activity onCreate()");
        setContentView(R.layout.transaction);
        dbHelper = DatabaseHelper.getInstance(this);
        // formaters
        moneyFormat = new DecimalFormat("#,###,###,###,##0.00'" + currencySymbol + "'");
        sdf = (SimpleDateFormat) DateFormat.getInstance();
        sdf.applyPattern(Constants.DATE_FORMAT);
        // view components
        incomeEditText = (EditText) findViewById(R.id.income);
        expenseEditText = (EditText) findViewById(R.id.expense);
        totalEditText = (EditText) findViewById(R.id.total);

        // from and to date
        toDateEditText = (EditText) findViewById(R.id.to_date);
        toDateEditText.setText(sdf.format(new Date()));
        toDateEditText.setInputType(InputType.TYPE_NULL);
        toDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createDatePickerDialog(TO_DATE_PICKER_CODE).show();
                    return true;
                }
                return false;
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        fromDateEditText = (EditText) findViewById(R.id.from_date);
        fromDateEditText.setText(sdf.format(calendar.getTime()));
        fromDateEditText.setInputType(InputType.TYPE_NULL);
        fromDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createDatePickerDialog(FROM_DATE_PICKER_CODE).show();
                    return true;
                }
                return false;
            }
        });

        // init data from db
        assetsMap = new TreeMap<Integer, Asset>();
        List<Asset> assets = dbHelper.convertCursorToAssets(dbHelper.getAssets());
        for (Asset a : assets) {
            assetsMap.put(a.getId(), a);
        }
        intData();
    }

    /***
     * update overview panel
     */
    private void updateOverviewPanel() {
        totalIncome = 0;
        totalExpense = 0;
        for (Transaction trans : transactions) {
            if (trans.getType().equals(Constants.TRANSACTION_TYPE_INCOME)) {
                totalIncome += trans.getAmout();
            } else if (trans.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
                totalExpense += trans.getAmout();
            }
        }
        incomeEditText.setText(moneyFormat.format(totalIncome));
        expenseEditText.setText(moneyFormat.format(totalExpense));
        totalEditText.setText(moneyFormat.format(totalIncome - Math.abs(totalExpense)));
    }

    /**
     * to load content for the view
     */
    private void intData() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getTransactions(sdf.parse(fromDateEditText.getText().toString()).getTime(),
                    sdf.parse(toDateEditText.getText().toString()).getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        transactions = dbHelper.convertCursorToTransaction(cursor);
        transactionAdapter = new TransactionAdapter();
        setListAdapter(transactionAdapter);
        updateOverviewPanel();
    }

    /**
     * add new transaction
     * 
     * @param view
     */
    public void addExpense(View view) {
        Intent intent = new Intent(this, AddEditTransactionActivity.class);
        intent.putExtra(Constants.TYPE, Constants.TRANSACTION_TYPE_EXPENSE);
        startActivityForResult(intent, SHOW_ADD_TRANSACTION_ACTIVITY);
    }

    /**
     * add new transaction
     * 
     * @param view
     */
    public void addIncome(View view) {
        Intent intent = new Intent(this, AddEditTransactionActivity.class);
        intent.putExtra(Constants.TYPE, Constants.TRANSACTION_TYPE_INCOME);
        startActivityForResult(intent, SHOW_ADD_TRANSACTION_ACTIVITY);
    }

    /**
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_ADD_TRANSACTION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                intData();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The list adapter using pojo object
     * 
     * @author minhducngo
     *
     */
    private class TransactionAdapter extends ArrayAdapter<Transaction> {

        /**
         * (non-Javadoc)
         * 
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Transaction transaction = getItem(position);
            LinearLayout layoutView;
            if (convertView == null) {
                // Inflate a new view if this is not an update.
                layoutView = new LinearLayout(getContext());
                LayoutInflater li = getLayoutInflater();
                li.inflate(R.layout.transaction_list_item, layoutView, true);
            } else {
                // Otherwise we'll update the existing View
                layoutView = (LinearLayout) convertView;
            }

            // date format
            SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();
            sdf.applyPattern(Constants.DATE_FORMAT);

            TextView dateLabel = (TextView) layoutView.findViewById(R.id.transaction_item_date);
            dateLabel.setText(sdf.format(transaction.getDate()));

            TextView ammount = (TextView) layoutView.findViewById(R.id.transaction_item_amount);
            ammount.setText(moneyFormat.format(transaction.getAmout()));
            if (transaction.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
                ammount.setTextColor(getResources().getColor(R.color.red));
            } else if (transaction.getType().equals(Constants.TRANSACTION_TYPE_INCOME)) {
                ammount.setTextColor(getResources().getColor(R.color.green));
            }

            TextView assetTextView = (TextView) layoutView.findViewById(R.id.transaction_item_asset);
            Asset asset = assetsMap.get(transaction.getAssetId());
            assetTextView.setText(asset.getName());

            TextView descriptionTextView = (TextView) layoutView.findViewById(R.id.transaction_item_description);
            if (transaction.getDescription() != null) {
                descriptionTextView.setText(transaction.getDescription());
            }
            return layoutView;
        }

        public TransactionAdapter() {
            super(TransactionActivity.this, R.layout.transaction_list_item, transactions);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Transaction transaction = transactionAdapter.getItem(position);
        Intent intent = new Intent(this, AddEditTransactionActivity.class);
        intent.putExtra(Constants.TYPE, Constants.ACTION_EDIT);
        intent.putExtra(Constants.ID, transaction.getId());
        startActivityForResult(intent, SHOW_ADD_TRANSACTION_ACTIVITY);
    }

    /**
     * show date picker dialog
     */
    private DatePickerDialog createDatePickerDialog(int dlgCode) {
        Date date = new Date();
        try {
            if (dlgCode == FROM_DATE_PICKER_CODE) {
                date = sdf.parse(fromDateEditText.getText().toString());
            } else if (dlgCode == TO_DATE_PICKER_CODE) {
                date = sdf.parse(toDateEditText.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerListener(dlgCode), year, month, day);
        return dlg;
    }

    /**
     * date picker listener
     * 
     * @author minhducngo
     *
     */
    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {
        /** The dialog code */
        int dlgCode = 0;

        /** the constructor */
        public DatePickerListener(int dlgCode) {
            this.dlgCode = dlgCode;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);
            if (dlgCode == FROM_DATE_PICKER_CODE) {
                fromDateEditText.setText(sdf.format(c.getTime()));
            } else {
                toDateEditText.setText(sdf.format(c.getTime()));
            }
            intData();
        }
    }
}
