package com.berry.android.piggybank.adapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.berry.android.piggybank.R;
import com.berry.android.piggybank.data.Account;
import com.berry.android.piggybank.data.Constants;
import com.berry.android.piggybank.data.Transaction;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {

    /** The money format*/
    private DecimalFormat moneyFormat;

    /** The context*/
    private Context context;

    /** The simple date format */
    private SimpleDateFormat sdf;
    
    /** The map of account */
    private Map<Integer, Account> accountsMap;

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
            LayoutInflater li = ((Activity) context).getLayoutInflater();
            li.inflate(R.layout.transaction_list_item, layoutView, true);
        } else {
            // Otherwise we'll update the existing View
            layoutView = (LinearLayout) convertView;
        }
        // TextView: date
        TextView dateLabel = (TextView) layoutView.findViewById(R.id.transaction_item_date);
        dateLabel.setText(sdf.format(transaction.getDate()));

        // TextView: amount
        TextView ammount = (TextView) layoutView.findViewById(R.id.transaction_item_amount);
        ammount.setText(moneyFormat.format(transaction.getAmout()));
        if (transaction.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
            ammount.setTextColor(context.getResources().getColor(R.color.red));
        } else if (transaction.getType().equals(Constants.TRANSACTION_TYPE_INCOME)) {
            ammount.setTextColor(context.getResources().getColor(R.color.blue));
        }

        // TextView: category
        TextView category = (TextView) layoutView.findViewById(R.id.transaction_item_category);
        category.setText("Blance Modify");

        // Text View Account
        TextView assetTextView = (TextView) layoutView.findViewById(R.id.transaction_item_account);
        Account account = accountsMap.get(transaction.getAssetId());
        assetTextView.setText(account.getName());

        // TextView: description
        TextView descriptionTextView = (TextView) layoutView.findViewById(R.id.transaction_item_description);
        if (transaction.getDescription() != null) {
            descriptionTextView.setText(transaction.getDescription());
        }
        return layoutView;
    }

    public TransactionListAdapter(Context context, List<Transaction> objects, Map<Integer, Account> accountsMap, DecimalFormat moneyFormat) {
        super(context, R.layout.transaction_list_item, objects);
        this.context = context;
        sdf = (SimpleDateFormat) DateFormat.getInstance();
        sdf.applyPattern(Constants.DATE_FORMAT);
        this.accountsMap = accountsMap;
        this.moneyFormat = moneyFormat;
    }

}
