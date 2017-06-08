package com.ksleong.android.visitorapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    private Switch btSwitch;
    private Switch apSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(R.string.menu_setting);

        btSwitch = (Switch)findViewById(R.id.switch_bt);
        apSwitch = (Switch)findViewById(R.id.switch_wifi);
    }
}
