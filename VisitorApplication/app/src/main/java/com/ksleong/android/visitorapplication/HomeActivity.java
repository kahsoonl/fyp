package com.ksleong.android.visitorapplication;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class HomeActivity extends AppCompatActivity implements BootstrapNotifier, BeaconConsumer {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private Region region;
    private BackgroundPowerSaver backgroundPowerSaver;
    private List<btBeacon> beaconList = new ArrayList<>();
    private BeaconManager beaconManager;

    private DatabaseReference btBeaconReference;

    private FirebaseAuth mAuth;

    private TextView homeText;
    private Button startScanButton;
    private Button stopScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeText = (TextView) findViewById(R.id.home_text);
        startScanButton = (Button) findViewById(R.id.start_scan_button);
        stopScanButton = (Button) findViewById(R.id.stop_scan_button);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mAuth = FirebaseAuth.getInstance();

        //altbeacon initialization
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        region = new Region("backgroundRegion", /*Identifier.parse("e2c56db5-dffb-48d2-b060-d0f5a71096e0")*/null, null, null);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        beaconManager.setBackgroundBetweenScanPeriod(100000);
        beaconManager.setForegroundBetweenScanPeriod(50000);

        scanStatus();

        //firebase
        btBeaconReference = FirebaseDatabase.getInstance().getReference().child("bluetooth");
        getBeaconList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            signIn();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();

        if (itemID == R.id.app_info) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.app_info_description);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }

        if (itemID == R.id.menu_list) {
            Intent listIntent = new Intent(this, ListActivity.class);
            String[] placeName = new String[beaconList.size()];
            String[] descData = new String[beaconList.size()];
            int i = 0;

            for (btBeacon b : beaconList) {
                placeName[i] = b.getLocationName();
                descData[i] = b.getDescription();
                i++;
            }

            Bundle infoBundle = new Bundle();
            infoBundle.putStringArray("placeName", placeName);
            infoBundle.putStringArray("desc", descData);
            listIntent.putExtra("info", infoBundle);
            startActivity(listIntent);
        }

        /*
        if(itemID == R.id.menu_database){
            btBeaconReference = FirebaseDatabase.getInstance().getReference().child("bluetooth");
            btBeacon btBeacon = new btBeacon("e2c56db5-dffb-48d2-b060-d0f5a71096e0", "0","35471","Library","In 2016, the Sunway Campus Library continued to improve\n" +
                    "infrastructure and environment in the new Library in response\n" +
                    "to library user needs.\n" +
                    "As at December 2016, the Library’s total book, audio-visual and\n" +
                    "bound journal collections stood at 145,177 items. Total electronic\n" +
                    "collection available to users included 32,000 e-journals, 124\n" +
                    "e-databases and 148,844 e-books.\n" +
                    "Total membership comprised 26,028 users as at December\n" +
                    "2016, an increase of 19% from 21,900 members in 2015. The\n" +
                    "Library saw increases in usage, with total entries accounting\n" +
                    "for 1,730,000 (20% increase over 2015) and 107,770 loans (3%\n" +
                    "increase over 2015).");
            btBeaconReference.child(btBeacon.getUID()).setValue(btBeacon);
        }

        if(itemID == R.id.menu_list){
            btBeaconReference = FirebaseDatabase.getInstance().getReference().child("bluetooth");
            btBeacon btBeacon = new btBeacon("e3c83db2-dffb-69d2-b060-d0f5a71096e1","1","34321","Cafeteria","The cafeteria has been");
            btBeaconReference.child(btBeacon.getUID()).setValue(btBeacon);
        }*/

        return super.onOptionsItemSelected(item);
    }

    public boolean isLocationServiceEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled= false,network_enabled = false;

        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }
        return gps_enabled || network_enabled;
    }

    public void signIn() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseUser", "signInAnonymously:success");
                            scanStatus();
                        } else {
                            Log.w("FirebaseUser", "signInAnonymously:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed. Please check your internet connection.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void requestForAccess(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location permission so that this application is able to scan and detect bluetooth beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        if (!mBluetoothAdapter.isEnabled()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Require Bluetooth");
            builder.setMessage("Please enable Bluetooth for this app to scan and detect bluetooth beacons");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            });
            builder.show();
        }

        if (!isLocationServiceEnabled()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Require Location");
            builder.setMessage("Please enable Location service for this app to scan and detect bluetooth beacons");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            builder.show();
        }

        switch (view.getId()) {
            case R.id.start_scan_button:
                beaconManager.bind(this);
                startScanButton.setVisibility(View.GONE);
                stopScanButton.setVisibility(View.VISIBLE);
                stopScanButton.setEnabled(false);
                stopScanButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopScanButton.setEnabled(true);
                        scanStatus();
                    }
                }, 3000);
                break;
            case R.id.stop_scan_button:
                beaconManager.unbind(this);
                stopScanButton.setVisibility(View.GONE);
                startScanButton.setVisibility(View.VISIBLE);
                startScanButton.setEnabled(false);
                startScanButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startScanButton.setEnabled(true);
                        scanStatus();
                    }
                }, 3000);
                break;
        }
    }

    public void scanStatus() {

        if (!beaconManager.isAnyConsumerBound()) {
            homeText.setText(R.string.scan_status_inactive);
        } else {
            homeText.setText(R.string.scan_status_active);
        }
    }

    public void getBeaconList() {

        btBeaconReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot btSnapshot : dataSnapshot.getChildren()) {
                    btBeacon beacon = btSnapshot.getValue(btBeacon.class);
                    beaconList.add(beacon);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE", databaseError.getMessage());
            }
        });
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "did enter region.");
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
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
        Log.d(TAG, "I have just switched from seeing/not seeing beacons: " + state);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon b : beacons) {
                        for (btBeacon btb : beaconList) {

                            if (b.getId1().toString().equals(btb.getUID())
                                    && b.getId2().toString().equals(btb.getMajor())
                                    && b.getId3().toString().equals(btb.getMinor())
                                    && b.getDistance() < 5) {
                                System.out.println(b.getDistance());
                                Log.e(TAG, "btBeacon with my Instance ID found!");
                                sendNotification(btb);
                            }
                        }
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(btBeacon btb) {

        Uri notiSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bundle btInformation = new Bundle();
        btInformation.putString("LocationName", btb.getLocationName());
        btInformation.putString("Description", btb.getDescription());

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Beacon detected")
                        .setContentText("A point of interest is nearby!")
                        .setSound(notiSound)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.beacon_found);

        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(new long[0]);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(new Intent(this, InfoActivity.class).putExtra("btInfoBundle", btInformation));
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

}
