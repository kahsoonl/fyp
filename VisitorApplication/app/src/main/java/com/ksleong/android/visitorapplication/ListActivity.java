package com.ksleong.android.visitorapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ListActivity extends AppCompatActivity implements ListDataAdapter.ListDataAdapterOnClickHandler {

    private RecyclerView mCycleView;
    private ListDataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] placeName;
    private String[] desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent fromIntent = getIntent();

        if (fromIntent.hasExtra("info")) {
            Bundle bundle = fromIntent.getBundleExtra("info");
            placeName = bundle.getStringArray("placeName");
            desc = bundle.getStringArray("desc");
        }

        mCycleView = (RecyclerView) findViewById(R.id.list_cycler);
        mLayoutManager = new LinearLayoutManager(this);
        mCycleView.setLayoutManager(mLayoutManager);
        mCycleView.setHasFixedSize(true);
        mAdapter = new ListDataAdapter(this);
        mAdapter.setPlaceName(placeName);
        mCycleView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(String pName) {
        Context context = this;
        Class destinationClass = InfoActivity.class;
        Intent infoIntent = new Intent(context, destinationClass);
        Bundle infoBundle = new Bundle();

        for (int i = 0; i < placeName.length; i++) {
            if (placeName[i].equals(pName)) {
                infoBundle.putString("LocationName", placeName[i]);
                infoBundle.putString("Description", desc[i]);
            }
        }

        infoIntent.putExtra("btInfoBundle", infoBundle);
        startActivity(infoIntent);
    }
}
