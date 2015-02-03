package com.berry.android.piggybank;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.berry.android.piggybank.adapter.TransactionListAdapter;
import com.berry.android.piggybank.data.Account;
import com.berry.android.piggybank.data.Constants;
import com.berry.android.piggybank.data.Transaction;
import com.berry.android.piggybank.database.DatabaseHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

public class TransactionsFragment extends ListFragment {

    /** The currency symbol */
    private String currencySymbol = "€";

    /** The tag for debug purpose */
    private static final String TAG = "PIGGY_BANK";

    /** The simple date format */
    private SimpleDateFormat sdf;

    /** The money format */
    private DecimalFormat moneyFormat;

    /** The data base helper */
    private DatabaseHelper dbHelper;

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
    private TransactionListAdapter transactionAdapter;

    /** The list of transaction */
    private List<Transaction> transactions;

    /** The map of account */
    private Map<Integer, Account> accountsMap;

    /** The total income */
    private float totalIncome = 0;

    /** The total expense */
    private float totalExpense = 0;

    /** The add income button */
    private Button addIncomeBtn;

    /** Teh add expense button */
    private Button addExpenseBtn;

    /**
     * (non-Javadoc)
     * 
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "TransactionsFragment onCreateView...");
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);

        dbHelper = DatabaseHelper.getInstance(this.getActivity());
        // formaters
        moneyFormat = new DecimalFormat(Constants.MONEY_FORMAT_CONSTANT + "'" + currencySymbol + "'");
        sdf = (SimpleDateFormat) DateFormat.getInstance();
        sdf.applyPattern(Constants.DATE_FORMAT);
        // view components
        incomeEditText = (EditText) rootView.findViewById(R.id.income);
        expenseEditText = (EditText) rootView.findViewById(R.id.expense);
        totalEditText = (EditText) rootView.findViewById(R.id.total);

        // from and to date
        toDateEditText = (EditText) rootView.findViewById(R.id.to_date);
        toDateEditText.setText(sdf.format(new Date()));
        toDateEditText.setInputType(InputType.TYPE_NULL);
        toDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createDatePickerDialog(toDateEditText).show();
                    return true;
                }
                return false;
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        fromDateEditText = (EditText) rootView.findViewById(R.id.from_date);
        fromDateEditText.setText(sdf.format(calendar.getTime()));
        fromDateEditText.setInputType(InputType.TYPE_NULL);
        fromDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createDatePickerDialog(fromDateEditText).show();
                    return true;
                }
                return false;
            }
        });

        // init data from db
        accountsMap = new TreeMap<Integer, Account>();
        List<Account> accounts = dbHelper.convertCursorToAccounts(dbHelper.getAccounts());
        for (Account a : accounts) {
            accountsMap.put(a.getId(), a);
        }
        intData();

        // register listeners to buttons
        addIncomeBtn = (Button) rootView.findViewById(R.id.addIncomeButton);
        addIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncome(v);
            }
        });

        addExpenseBtn = (Button) rootView.findViewById(R.id.addExpenseButton);
        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense(v);
            }
        });

        return rootView;
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
            e.printStackTrace();
        }
        transactions = dbHelper.convertCursorToTransaction(cursor);
        transactionAdapter = new TransactionListAdapter(this.getActivity(), transactions, accountsMap, moneyFormat);
        setListAdapter(transactionAdapter);
        updateOverviewPanel();
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
     * add new transaction
     * 
     * @param view
     */
    public void addExpense(View view) {
        Intent intent = new Intent(this.getActivity(), AddEditTransactionActivity.class);
        intent.putExtra(Constants.TYPE, Constants.TRANSACTION_TYPE_EXPENSE);
        startActivityForResult(intent, Constants.REQ_CODE_ADD_EDIT_TRANSACTION);
    }

    /**
     * add new transaction
     * 
     * @param view
     */
    private void addIncome(View view) {
        Intent intent = new Intent(this.getActivity(), AddEditTransactionActivity.class);
        intent.putExtra(Constants.TYPE, Constants.TRANSACTION_TYPE_INCOME);
        startActivityForResult(intent, Constants.REQ_CODE_ADD_EDIT_TRANSACTION);
    }

    /**
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQ_CODE_ADD_EDIT_TRANSACTION) {
            if (resultCode == Activity.RESULT_OK) {
                intData();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Transaction transaction = transactionAdapter.getItem(position);
        Intent intent = new Intent(this.getActivity(), AddEditTransactionActivity.class);
        intent.putExtra(Constants.TYPE, Constants.ACTION_EDIT);
        intent.putExtra(Constants.ID, transaction.getId());
        startActivityForResult(intent, Constants.REQ_CODE_ADD_EDIT_TRANSACTION);
    }

    /**
     * show date picker dialog
     */
    private DatePickerDialog createDatePickerDialog(EditText component) {
        Date date = new Date();
        try {
            date = sdf.parse(component.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(this.getActivity(), new DatePickerListener(component), year, month, day);
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
        EditText component;

        /** the constructor */
        public DatePickerListener(EditText component) {
            this.component = component;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);
            component.setText(sdf.format(c.getTime()));
            intData();
        }
    }
}
