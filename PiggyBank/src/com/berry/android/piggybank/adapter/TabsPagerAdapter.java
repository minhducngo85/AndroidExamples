package com.berry.android.piggybank.adapter;

import com.berry.android.piggybank.AccountsFragment;
import com.berry.android.piggybank.TransactionsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * tab pager listener
 * 
 * @author minhducngo
 *
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
        case 0:
            return new TransactionsFragment();
        case 1:
            return new AccountsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
