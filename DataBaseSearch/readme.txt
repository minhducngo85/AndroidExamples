Tuto link: http://coenraets.org/blog/androidtutorial/

1) Create search.xml and main.xml layouts
2) create the EmployeeList.java as:
package com.android.tuto.databasesearch;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListAdapter;

public class EmployeeList extends ListActivity {

	private EditText searchText;
	
	protected ListAdapter adapter;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		searchText = (EditText) findViewById(R.id.searchText);
	}

}


3) Register EmployeeList as default activity
4) create DatabaseHelper class
