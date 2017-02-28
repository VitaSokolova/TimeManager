package com.example.android.timemanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class PagerAdapter extends FragmentPagerAdapter {

    private static final int COUNT = 2;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return PlusOneFragment.newInstance();
            case 1:
                return CountFragment.newInstance();
            default:
                return PlusOneFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
