package com.mayv.gotrip;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;


public class LocationJobService extends JobIntentService {

    private Intent intent;
    private boolean reachedDestination = false;
    private boolean oneWay = false;
    private boolean repeat = false;
    private String startLatLong;
    private String endLatLong;
    private int tripId;
    private float distance;
    private int duration = 0;
    private Trip trip;
    private DatabaseAdapter databaseAdapter;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;



    static void enqueueWork(Context context, Intent work){
        enqueueWork(context, LocationJobService.class, 1111, work);
    }

    private void initializeParameters(Intent intent) {
        this.intent = intent;
        databaseAdapter = new DatabaseAdapter(this);
        tripId = intent.getIntExtra("TripId", 0);
        trip = databaseAdapter.getSpecificTrip(tripId);
        startLatLong = intent.getStringExtra("StartLatLong");
        endLatLong = intent.getStringExtra("EndLatLong");
        String direction = intent.getStringExtra("Direction");
        String repeatString = intent.getStringExtra("Repeat");
        if (direction.equals("One Direction")) {
            oneWay = true;
        }
        if (!repeatString.equals("Once")) {
            repeat = true;
        }
    }

    private void setLocationListener() {
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LatLng startLatLng = getLatLng(startLatLong);
                LatLng endLatLng = getLatLng(endLatLong);
                Location currentLocation = locationResult.getLastLocation();
                Location startLocation = new Location("");
                Location endLocation = new Location("");
                startLocation.setLatitude(startLatLng.latitude);
                startLocation.setLongitude(startLatLng.longitude);
                endLocation.setLatitude(endLatLng.latitude);
                endLocation.setLongitude(endLatLng.longitude);
                float distanceToEnd = currentLocation.distanceTo(endLocation);
                float distanceToStart = currentLocation.distanceTo(startLocation);
                distance = startLocation.distanceTo(endLocation);
                if(!reachedDestination){
                    if (distanceToEnd <= 1000) {
                        if (!oneWay) {
                            if (distanceToStart <= 1000) {
                                addTripToHistory(PastTrip.IS_DONE);
                                reachedDestination = true;
                            }
                        } else {
                            addTripToHistory(PastTrip.IS_DONE);
                            reachedDestination = true;
                        }
                    }
                }else{
                    stopService(intent);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider = LocationServices.getFusedLocationProviderClient(this);
            locationProvider.requestLocationUpdates(locationRequest,
                    locationCallback, this.getMainLooper());
        }
    }

    private LatLng getLatLng(String string) {
        String[] latlong = string.split(",,");
        LatLng latLng = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
        return latLng;
    }

    private float getAverageSpeed(){
        float distance = this.distance;
        return distance / duration;
    }

    private void addTripToHistory(int status){
        PastTrip pastTrip = new PastTrip(trip.getTripName(), trip.getStartPoint(), trip.getEndPoint(), trip.getStartLatLong(), trip.getEndLatLong(), "Meter/Second", duration + " Seconds", getAverageSpeed() + " M/S", status);
        if (!repeat) {
            databaseAdapter.deleteTrip(tripId, DatabaseAdapter.DatabaseHelper.TRIPS_TABLE_NAME);
            databaseAdapter.deleteTripFromFirebase(trip.getTripName());
        }
        databaseAdapter.insertPastTrip(pastTrip);
        databaseAdapter.insertPastTripFirebase(pastTrip);
    }

    public void startDurationCounter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!reachedDestination){
                    duration++;
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        initializeParameters(intent);
        startDurationCounter();
        setLocationListener();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if(!reachedDestination){
            addTripToHistory(PastTrip.IS_CANCELED);
        }
        stopService(intent);
    }
}
