package com.ksleong.android.visitorapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    private ImageView mHomeImage;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BT = 1;

    private DatabaseReference btReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mAuth = FirebaseAuth.getInstance();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location so that this application is able to scan and detect bluetooth beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        if(!mBluetoothAdapter.isEnabled()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Require Bluetooth");
            builder.setMessage("Please enable Bluetooth for this app to scan and detect bluetooth beacons");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener(){

                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            });
            builder.show();
        }

        if(!isLocationServiceEnabled()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Require Location");
            builder.setMessage("Please enable Location for this app to scan and detect bluetooth beacons");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener(){

                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            builder.show();
        }


        setContentView(R.layout.activity_home);
        setTitle("Welcome to Sunway University");

        mHomeImage = (ImageView)findViewById(R.id.home_image);
        mHomeImage.setImageResource(R.drawable.sunu_home);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();

        if(itemID == R.id.menu_setting){
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
        }

        if(itemID == R.id.menu_debug){
            Intent debugIntent = new Intent(this, InfoActivity.class);
            startActivity(debugIntent);
        }

        if(itemID == R.id.menu_database){
            btReference = FirebaseDatabase.getInstance().getReference().child("bluetooth");
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
            btReference.child(btBeacon.getUID()).setValue(btBeacon);
        }

        if(itemID == R.id.menu_scan){
            Intent scanIntent = new Intent(this, ScanActivity.class);
            startActivity(scanIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isLocationServiceEnabled(){
        LocationManager locationManager = null;
        boolean gps_enabled= false,network_enabled = false;

        if(locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                        } else {
                            Log.w("FirebaseUser", "signInAnonymously:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed. Please check your internet connection.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
