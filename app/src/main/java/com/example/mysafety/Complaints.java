package com.example.mysafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;



public class Complaints extends AppCompatActivity implements Categories.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        viewPager.setAdapter(adapter);//Setting viewpager for navigating between tabs by swiping
        TabLayout tabLayout=(TabLayout)findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);//tablayout for tabs

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
