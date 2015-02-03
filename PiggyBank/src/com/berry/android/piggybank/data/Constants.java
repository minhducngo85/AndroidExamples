package com.berry.android.piggybank.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String DATE_FORMAT = "MM-dd-yyyy";

    public static final String ACTION_EDIT = "Edit";

    public static final String TYPE = "TYPE";

    public static final String ID = "ID";

    public static final String TRANSACTION_TYPE_INCOME = "Income";

    public static final String TRANSACTION_TYPE_EXPENSE = "Expense";

    public static final String TRANSACTION_TYPE_TRANSFER = "Transfer";

    public static final List<String> TRANSACTION_TYPES = new ArrayList<String>(Arrays.asList(TRANSACTION_TYPE_INCOME, TRANSACTION_TYPE_EXPENSE));

    /** The money format constant */
    public static final String MONEY_FORMAT_CONSTANT = "#,###,###,###,##0.00";

    /** The request code to show AddTransactionActivity: prefix = 1 -> Transaction */
    public static final int REQ_CODE_ADD_EDIT_TRANSACTION = 10;

    /** The request code to show account details:  prefix = 2 -> Account */
    public static final int REQ_CODE_ACCOUNT_DETAILS = 20;
}
