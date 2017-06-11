package com.ksleong.android.visitorapplication;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Winter Leong on 19/5/2017.
 */

public class scanService extends Application implements BootstrapNotifier,BeaconConsumer,RangeNotifier{

    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private HomeActivity rangingActivity = null;
    private List<btBeacon> beaconList = new ArrayList<>();
    BeaconManager beaconManager;

    private DatabaseReference btBeaconReference;

    public void onCreate() {
        super.onCreate();


        //bluetooth beacon scan
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        Region region = new Region("backgroundRegion", /*Identifier.parse("e2c56db5-dffb-48d2-b060-d0f5a71096e0")*/null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        beaconManager.setBackgroundBetweenScanPeriod(10000);
        beaconManager.setForegroundBetweenScanPeriod(10000);
        beaconManager.bind(this);

        //firebase
        btBeaconReference = FirebaseDatabase.getInstance().getReference().child("bluetooth");
        getBeaconList();
    }

    public void getBeaconList(){

        btBeaconReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot btSnapshot: dataSnapshot.getChildren()){
                    btBeacon beacon = btSnapshot.getValue(btBeacon.class);
                    beaconList.add(beacon);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE",databaseError.getMessage());
            }
        });
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "did enter region.");
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        }
        catch (RemoteException e) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Can't start ranging");
        }
    }

    @Override
    public void didExitRegion(Region region) {
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.d(TAG,"I have just switched from seeing/not seeing beacons: " + state);
    }

    private void sendNotification(btBeacon btb) {

        Uri notiSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bundle btInformation = new Bundle();
        btInformation.putString("LocationName",btb.getLocationName());
        btInformation.putString("Description",btb.getDescription());

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("btBeacon Reference Application")
                        .setContentText("A point of interest is nearby!")
                        .setSound(notiSound)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSmallIcon(R.mipmap.ic_launcher_round);

        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(new long[0]);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, InfoActivity.class).putExtra("btInfoBundle",btInformation));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
            //System.out.println(beaconList.size());
            for (Beacon b : beacons) {
                for(btBeacon btb : beaconList) {

                    /* debugging
                    System.out.println(btb.getUID());
                    System.out.println(b.getId1());
                    System.out.println(btb.getMajor());
                    System.out.println(b.getId2());
                    System.out.println(btb.getMinor());
                    System.out.println(b.getId3());
                    System.out.println(btb.getLocationName());
                    System.out.println(b.getId1().toString().equals(btb.getUID()));
                    System.out.println(b.getId2().toString().equals(btb.getMajor()));
                    System.out.println(b.getId3().toString().equals(btb.getMajor()));
                    System.out.println(b.getDistance() < 2);
                    */

                    if (b.getId1().toString().equals(btb.getUID())
                     && b.getId2().toString().equals(btb.getMajor())
                     && b.getId3().toString().equals(btb.getMinor())
                     && b.getDistance() < 5) {
                        Log.e(TAG, "btBeacon with my Instance ID found!");
                        sendNotification(btb);
                    }
                }
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(this);
    }
}
