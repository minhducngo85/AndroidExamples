package com.berry.android.piggybank;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.berry.android.piggybank.adapter.TransactionListAdapter;
import com.berry.android.piggybank.data.Account;
import com.berry.android.piggybank.data.Constants;
import com.berry.android.piggybank.data.Transaction;
import com.berry.android.piggybank.database.DatabaseHelper;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class AccountDetailsActivity extends ListActivity {

    /** The currency symbol */
    private String currencySymbol = "€";

    /** The tag for debug purpose */
    private static final String TAG = "PIGGY_BANK";

    /** Teh simple date format */
    private SimpleDateFormat sdf;

    /** The money format */
    private DecimalFormat moneyFormat;

    /** The data base helper */
    private DatabaseHelper dbHelper;

    /** The account id */
    private int accountId = 0;

    /** The current account */
    private Account account;

    /** The balance text view */
    private TextView balanceTv;

    /** the from date text view */
    private EditText fromDateEt;

    /** the to date text view */
    private EditText toDateEt;

    /** the total income text view */
    private TextView totalIncomeTv;

    /** the total income text view */
    private TextView totalExpenseTv;
    
    /** The list adapter*/
    private TransactionListAdapter listAdapter;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressLint("ClickableViewAccessibility")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details);
        // database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // init formatter
        moneyFormat = new DecimalFormat("#,###,###,###,##0.00'" + currencySymbol + "'");
        sdf = (SimpleDateFormat) DateFormat.getInstance();
        sdf.applyPattern(Constants.DATE_FORMAT);

        // get account details from db
        accountId = getIntent().getExtras().getInt(Constants.ID, 0);
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_ACCOUNT + " WHERE " + DatabaseHelper.COL_ID + " = " + accountId;
        account = dbHelper.convertCursorToAccounts(dbHelper.executeReadQuery(query)).get(0);
        account.setBalance(dbHelper.getAccountBalance(accountId));
        setTitle(account.getName());

        // view components
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        toDateEt = (EditText) findViewById(R.id.account_details_to_date);
        toDateEt.setText(sdf.format(new Date()));
        toDateEt.setInputType(InputType.TYPE_NULL);
        toDateEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createDatePickerDialog(toDateEt).show();
                    return true;
                }
                return false;
            }
        });

        fromDateEt = (EditText) findViewById(R.id.account_details_from_date);
        fromDateEt.setText(sdf.format(calendar.getTime()));
        fromDateEt.setInputType(InputType.TYPE_NULL);
        fromDateEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createDatePickerDialog(fromDateEt).show();
                    return true;
                }
                return false;
            }
        });

        totalIncomeTv = (TextView) findViewById(R.id.account_details_income);
        totalExpenseTv = (TextView) findViewById(R.id.account_details_expense);

        balanceTv = (TextView) findViewById(R.id.account_details_balance);
        initData();
    }

    /**
     * to initialize the data
     */
    public void initData() {
        balanceTv.setText(moneyFormat.format(account.getBalance()));
        initListView();
    }

    /**
     * to initialize the list view and its related views
     */
    public void initListView() {
        float totalIncome = 0;
        float totalExpense = 0;
        Date fromDate = new Date();
        Date toDate = new Date();
        try {
            fromDate = sdf.parse(fromDateEt.getText().toString());
            toDate = sdf.parse(toDateEt.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Transaction> transactions = dbHelper.convertCursorToTransaction(dbHelper.getTransactions(accountId, fromDate.getTime(), toDate.getTime()));
        
        totalIncomeTv.setText(moneyFormat.format(totalIncome));
        totalExpenseTv.setText(moneyFormat.format(totalExpense));
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
        DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerListener(component), year, month, day);
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
            initListView();
        }
    }
}
