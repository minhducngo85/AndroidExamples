package com.berry.android.piggybank;

import java.util.List;

import com.berry.android.piggybank.adapter.TabsPagerAdapter;

import android.app.ActionBar;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

/**
 * the main activity as tab view pager
 * 
 * @author minhducngo
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /** The view pager */
    private ViewPager viewPager;

    /** The action bar */
    private ActionBar actionBar;

    /** The tab pager adapter */
    private TabsPagerAdapter pagerAdapter;

    /** The tag for debug purpose */
    private static final String TAG = "PIGGY_BANK";

    /** Tab titles */
    private String[] tabs = { "Transactions", "Accounts" };

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Bind the Activity's SearchableInfo to the Search View
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchableInfo);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected position: " + position);
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.i(TAG, "onTabSelected position: " + tab.getPosition());
        viewPager.setCurrentItem(tab.getPosition());
        //refresh accounts fragment
        if (tab.getPosition() == 1) {
            updateAccountsFragmentUI();
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    /**
     * to update account fragment user interface
     * */
    private void updateAccountsFragmentUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getSupportFragmentManager();
                for (Fragment fragment : fm.getFragments()) {
                    if (fragment.getClass().equals(AccountsFragment.class)) {
                        AccountsFragment accountsFragment = (AccountsFragment) fragment;
                        accountsFragment.initData();
                    }
                }
            }
        });
    }
}
