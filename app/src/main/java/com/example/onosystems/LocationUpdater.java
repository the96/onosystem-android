package com.example.onosystems;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationUpdater extends LocationCallback{
    private static final int LOCATION_REQUEST_CODE = 1;
    private FusedLocationProviderClient locationClient;
    private Context context;
    private LocationResultListener listener;

    public interface LocationResultListener {
        void locationResult(LocationResult location);
    }

    LocationUpdater(Context context, LocationResultListener listener) {
        locationClient = LocationServices.getFusedLocationProviderClient(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        listener.locationResult(locationResult);
    }

    public void run() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, LOCATION_REQUEST_CODE);
            System.out.println("please permit GPS");
            return;
        }

        if (!isEnabledGPS()) {
            System.out.println("not enabled GPS");
            return;
        }

        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationClient.requestLocationUpdates(request, this, null);
    }

    public void stopUpdateLocation() {
        locationClient.removeLocationUpdates(this);
    }

    public boolean isEnabledGPS() {
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

}
