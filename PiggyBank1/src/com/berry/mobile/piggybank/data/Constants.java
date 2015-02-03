package com.berry.mobile.piggybank.data;

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
}
