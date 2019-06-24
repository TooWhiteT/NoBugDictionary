package com.tzg.nobugdictionary.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> mFragmentlist) {
        super(fm);
        this.fragmentList = mFragmentlist;
    }
    @Override
    public int getCount() {
        return fragmentList.size();
    }
    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }
}
