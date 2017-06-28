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

    private ArrayList<Integer> images = new ArrayList<Integer>();
    private int[] images2;
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
            case "Library":
                images.add(R.drawable.library_1);
                images.add(R.drawable.library_2);
                images.add(R.drawable.library_3);
                images.add(R.drawable.library_4);
                images.add(R.drawable.library_5);
                images2 = convertIntegers(images);
                break;
            case "Cafeteria":
                images.add(R.drawable.cafeteria_1);
                images.add(R.drawable.cafeteria_2);
                images2 = convertIntegers(images);
                break;
        }
    }

    public static int[] convertIntegers(ArrayList<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
}
