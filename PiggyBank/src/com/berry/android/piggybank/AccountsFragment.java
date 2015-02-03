package com.berry.android.piggybank;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.berry.android.piggybank.data.Account;
import com.berry.android.piggybank.data.Constants;
import com.berry.android.piggybank.database.DatabaseHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountsFragment extends ListFragment {

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

    /** The list adapter */
    private AccountsListAdapter listAdapter;

    /** The list of accounts */
    private List<Account> accounts;

    /** The asset edit text */
    private EditText assetsEt;


    /**
     * (non-Javadoc)
     * 
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "AccountsFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        // database helper
        dbHelper = DatabaseHelper.getInstance(this.getActivity());

        // init formatter
        moneyFormat = new DecimalFormat("#,###,###,###,##0.00'" + currencySymbol + "'");
        sdf = (SimpleDateFormat) DateFormat.getInstance();
        sdf.applyPattern(Constants.DATE_FORMAT);
        // view components
        assetsEt = (EditText) rootView.findViewById(R.id.accounts_total);

        initData();
        return rootView;
    }

    /**
     * initialize data from database
     */
    public void initData() {
        float total = 0;
        accounts = dbHelper.convertCursorToAccounts(dbHelper.getAccounts());
        for (Account account : accounts) {
            account.setBalance(dbHelper.getAccountBalance(account.getId()));
            total += account.getBalance();
        }
        assetsEt.setText(moneyFormat.format(total));
        if (total >= 0) {
            assetsEt.setTextColor(getResources().getColor(R.color.blue));
        } else {
            assetsEt.setTextColor(getResources().getColor(R.color.red));
        }
        listAdapter = new AccountsListAdapter();
        setListAdapter(listAdapter);
    }

    /**
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this.getActivity(), AccountDetailsActivity.class);
        Account account = listAdapter.getItem(position);
        intent.putExtra(Constants.ID, account.getId());
        //
        Log.d(TAG, account.getName() + " - " + account.getId());
        startActivityForResult(intent, Constants.REQ_CODE_ACCOUNT_DETAILS);
    }

    /**
     * the account list adapter
     * 
     * @author minhducngo
     *
     */
    private class AccountsListAdapter extends ArrayAdapter<Account> {

        /**
         * (non-Javadoc)
         * 
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Account account = getItem(position);
            RelativeLayout layoutView;
            if (convertView == null) {
                // Inflate a new view if this is not an update.
                layoutView = new RelativeLayout(getContext());
                LayoutInflater li = getActivity().getLayoutInflater();
                li.inflate(R.layout.account_list_item, layoutView, true);
            } else {
                // Otherwise we'll update the existing View
                layoutView = (RelativeLayout) convertView;
            }
            // account name
            TextView name = (TextView) layoutView.findViewById(R.id.account_name);
            name.setText(account.getName());

            // balance
            TextView balance = (TextView) layoutView.findViewById(R.id.account_balance);
            balance.setText(moneyFormat.format(account.getBalance()));
            if (account.getBalance() >= 0) {
                balance.setTextColor(getResources().getColor(R.color.blue));
            } else {
                balance.setTextColor(getResources().getColor(R.color.red));
            }

            return layoutView;
        }

        private AccountsListAdapter(Context context, int resource, int textViewResourceId, Account[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public AccountsListAdapter() {
            super(getActivity(), R.layout.account_list_item, accounts);
        }

    }

}
