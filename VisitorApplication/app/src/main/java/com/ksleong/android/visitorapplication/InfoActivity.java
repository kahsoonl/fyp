package com.ksleong.android.visitorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    private ImageView infoImage;
    private TextView infoText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        infoImage = (ImageView)findViewById(R.id.info_image);
        infoImage.setImageResource(R.drawable.sunu_home);
        infoText = (TextView)findViewById(R.id.info_text);

        Intent fromIntent = getIntent();

        if(fromIntent.hasExtra("btInfoBundle")){
            Bundle btInfoBundle = fromIntent.getBundleExtra("btInfoBundle");
            infoText.setText(btInfoBundle.getString("Description"));
            setTitle(btInfoBundle.getString("LocationName"));
        }
    }
}
