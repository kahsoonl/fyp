package com.ksleong.android.visitorapplication;

/**
 * Created by Winter Leong on 26/5/2017.
 */

public class btBeacon {
    private String uid;
    private String major;
    private String minor;
    private String locationName;
    private String description;

    public btBeacon(){}

    public btBeacon(String uid, String major, String minor, String location, String desc){
        this.uid = uid;
        this.major = major;
        this.minor = minor;
        locationName = location;
        description = desc;
    }

    public String getUID(){
        return uid;
    }

    public String getMajor(){
        return major;
    }

    public String getMinor(){
        return minor;
    }

    public String getLocationName(){
        return locationName;
    }

    public String getDescription(){
        return description;
    }
}
