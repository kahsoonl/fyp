package com.ksleong.android.visitorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    private TextView infoText;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private String locationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        infoText = (TextView) findViewById(R.id.info_text);
        mPager = (ViewPager) findViewById(R.id.info_pager);

        Intent fromIntent = getIntent();

        if (fromIntent.hasExtra("btInfoBundle")) {
            Bundle btInfoBundle = fromIntent.getBundleExtra("btInfoBundle");
            locationName = btInfoBundle.getString("LocationName");
            infoText.setText(btInfoBundle.getString("Description"));
            setTitle(btInfoBundle.getString("LocationName"));
        }

        mPagerAdapter = new SlidePagerAdapter(this, locationName);
        mPager.setAdapter(mPagerAdapter);


    }
}
