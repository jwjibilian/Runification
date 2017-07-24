package com.example.jwjibilian.runificationandroid.controller;

/**
 * Created by jwjibilian on 7/22/17.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.security.Provider;
import java.text.DecimalFormat;
import java.util.Locale;

public class Pace implements LocationListener {
    DecimalFormat formatter = new DecimalFormat("#0.00");
    LocationManager locationManager;
    String provider;
    double prevLat = 0;
    double prevLon = 0;

    Location location;
    Location location1;
    Location location2;
    GPSTracker gps;
    Context myContext;
    EditText upText;
    Pace hi = this;

    private Handler paceTimer;
    private Runnable paceTh;

    public Pace(Context context, LocationManager lm) {
        locationManager = lm;
        myContext = context;
        // Creating an empty criteria object

        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria

        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Log.d("Provider", provider + "");
        location = locationManager.getLastKnownLocation(provider);



    }

    public double getPace() {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 0;
        }
        locationManager.requestLocationUpdates(provider, 1000, 0, this);
        location2 = location1;
        location1 = locationManager.getLastKnownLocation(provider);
        //Log.d("tick", "tock");

        if (location1 != null) {
            if (location2 != null) {

                location1 = new Location(locationManager.getLastKnownLocation(provider));

                double toUpdate = location1.distanceTo(location2);
                double update = 1/(toUpdate * 0.000621371 *12);
                return update;
            } else {
                return 0;
            }

        } else {
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
        }
        return 0;


    }
    public void getPaceUpdateText(EditText text){
        paceTimer = new Handler();
        upText = text;
        paceTh = new Runnable() {
            @Override
            public void run() {
                Log.d("Provider", provider + "");
                Log.d("LM", locationManager + "");
                Log.d("Pace", hi + "");
                Log.d("LM", location1 + "");
                double toRet = hi.getPace();
                Log.d("Dist", toRet + "");

                Log.d("Dist Mile", toRet +"");

                upText.setText(formatter.format(toRet));
                paceTimer.postDelayed(paceTh, 5000);


            }
        };

        paceTimer.postDelayed(paceTh, 5000);



    }

    @Override
    public void onLocationChanged(Location location) {

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
}
