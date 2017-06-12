package com.ksleong.android.visitorapplication;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * Created by Winter Leong on 12/6/2017.
 */

public class SlidePagerAdapter extends PagerAdapter {

    private ArrayList<Integer> images;
    private int[] images2 = {R.drawable.sunu_new_morning,
            R.drawable.sunu_night,
            R.drawable.sunu_home};
    private Context context;
    private LayoutInflater inflater;

    public SlidePagerAdapter(Context context, String activityName) {
        retrieveResource(activityName);
        this.context = context;
    }

    @Override
    public int getCount() {
        return images2.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.slide_show, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.slideImage);
        imageView.setImageResource(images2[position]);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.refreshDrawableState();
    }

    public void retrieveResource(String activityName) {
        switch (activityName) {
            case "home":
                //images.add(R.drawable.sunu_home);
                //images.add(R.drawable.common_google_signin_btn_icon_dark_focused);
                //images.add(R.drawable.common_google_signin_btn_icon_dark_normal_background);
                break;
            case "info":
                //images.add(R.drawable.sunu_home);
                //images.add(R.drawable.common_google_signin_btn_icon_dark_focused);
                //images.add(R.drawable.common_google_signin_btn_icon_dark_normal_background);
                break;
        }

    }
}
