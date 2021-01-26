package com.example.florim.thirremjeshtrin;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private double locLong = -1;
    private double locLat = -1;
    private LocationManager mLocationManager;
    private Location mLocation;
    private AccountManager am;
    ConnectivityManager cm;
    com.roughike.bottombar.BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mBottomBar = com.roughike.bottombar.BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.menu_main);
        mBottomBar.setDefaultTabPosition(1);

        mBottomBar.setOnMenuTabClickListener(new com.roughike.bottombar.OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.profile) {

                    Intent i = new Intent(MainActivity.this, UserInfo.class);
                    i.putExtra("isUser", true);
                    startActivity(i);

                } else if (menuItemId == R.id.inbox) {

                    Intent i = new Intent(MainActivity.this, UserList.class);
                    startActivity(i);

                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });

        mBottomBar.mapColorForTab(0, "#F44346");
        mBottomBar.mapColorForTab(1, "#795548");


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (am == null) {
            am = AccountManager.get(this);
        }
        if (Authenticator.findAccount(am, this) == null) {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(Location loc) {
        locLat = loc.getLatitude();
        locLong = loc.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void onImageClick(View v) {
        if (PermissionUtils.connectivityCheck(cm)) {
            getLocation();
            if (locLat != -1 && locLong != -1) {
                int category = Integer.valueOf(v.getTag().toString());

                Intent i = new Intent(this, MapActivity.class);
                i.putExtra("category", category);
                i.putExtra("lat", locLat);
                i.putExtra("lon", locLong);
                startActivity(i);
            }
        } else {
            Toast.makeText(this, R.string.no_connectivity, Toast.LENGTH_LONG).show();
        }
    }

    private void getLocation() {

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (mLocation == null) {
                Criteria c = new Criteria();
                c.setAccuracy(Criteria.ACCURACY_COARSE);
                c.setPowerRequirement(Criteria.POWER_LOW);
                String bestProvider = mLocationManager.getBestProvider(c, true);
                mLocation = mLocationManager.getLastKnownLocation(bestProvider);
                mLocationManager.requestLocationUpdates(bestProvider, 5000, 100, this);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
            }
            if (mLocation != null) {
                locLat = mLocation.getLatitude();
                locLong = mLocation.getLongitude();

            } else {
                Toast.makeText(this, R.string.no_provider_error, Toast.LENGTH_SHORT).show();

            }
        }


    }


    private boolean checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            return false;
        }
        return true;

    }
}

