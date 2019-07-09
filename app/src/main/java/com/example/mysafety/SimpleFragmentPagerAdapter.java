package com.example.mysafety;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String tabtitles[]=new String[]{"Electrical","Instrumentation","Mechanical","General"};
    public SimpleFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        return Categories.newInstance(tabtitles[position]);
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }
    public CharSequence getPageTitle(int position){
        return tabtitles[position];
    }
}
