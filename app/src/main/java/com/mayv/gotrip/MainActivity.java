package com.mayv.gotrip;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    OngoingTripsFragment ongoingTripsFragment;
    TripHistoryFragment tripHistoryFragment;
    SettingsFragment settingsFragment;
    MapFragment mapFragment;
    FragmentManager manager;
    FrameLayout frameLayout;
    TextView userEmailTV;
    SharedPreferences shared;
    DatabaseAdapter databaseAdapter;
    private final int LOCATION_REQUEST_CODE = 103;
    private int lastClickedMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_main);
        initializeComponents();
        requestLocationPermission();
        lastClickedMenuItem = R.id.onGoing_trips_item;
        navigationView.setCheckedItem(R.id.onGoing_trips_item);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.onGoing_trips_item && lastClickedMenuItem != R.id.onGoing_trips_item) {
                    lastClickedMenuItem = R.id.onGoing_trips_item;
                    showOngoingTripsFragment();
                } else if (item.getItemId() == R.id.past_trips_item && lastClickedMenuItem != R.id.past_trips_item) {
                    lastClickedMenuItem = R.id.past_trips_item;
                    showTripHistoryFragment();
                } else if (item.getItemId() == R.id.map_history_item && lastClickedMenuItem != R.id.map_history_item) {
                    lastClickedMenuItem = R.id.map_history_item;
                    showMapFragment();
                } else if (item.getItemId() == R.id.settings_item && lastClickedMenuItem != R.id.settings_item) {
                    lastClickedMenuItem = R.id.settings_item;
                    showSettingsFragment();
                } else if (item.getItemId() == R.id.logout_item) {
                    logout();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    private void logout() {
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("UserLoggedBefore", false);
        editor.putBoolean("PastTripsLoad", true);
        editor.putBoolean("NeedFirebaseLoad", true);
        editor.apply();
        finish();
        FirebaseAuth.getInstance().signOut();
        databaseAdapter.deleteAllTrips(DatabaseAdapter.DatabaseHelper.TRIPS_TABLE_NAME);
        databaseAdapter.deleteAllTrips(DatabaseAdapter.DatabaseHelper.PAST_TRIPS_TABLE_NAME);
        Intent intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
        startActivity(intent);
    }

    private void showMapFragment() {
        manager = getSupportFragmentManager();
        MapFragment fragment = (MapFragment) manager.findFragmentByTag("MapFragment");
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == null) {
            mapFragment = new MapFragment();
        } else {
            mapFragment = (MapFragment) manager.findFragmentByTag("MapFragment");
        }
        transaction.replace(R.id.main_frameLayout, mapFragment);
        transaction.commit();
    }


    private void showTripHistoryFragment() {
        manager = getSupportFragmentManager();
        TripHistoryFragment fragment = (TripHistoryFragment) manager.findFragmentByTag("TripHistoryFragment");
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == null) {
            tripHistoryFragment = new TripHistoryFragment();
        } else {
            tripHistoryFragment = (TripHistoryFragment) manager.findFragmentByTag("TripHistoryFragment");
        }
        transaction.replace(R.id.main_frameLayout, tripHistoryFragment);
        transaction.commit();
    }

    private void showOngoingTripsFragment() {
        manager = getSupportFragmentManager();
        OngoingTripsFragment fragment = (OngoingTripsFragment) manager.findFragmentByTag("OngoingTripsFragment");
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == null) {
            ongoingTripsFragment = new OngoingTripsFragment();
        } else {
            ongoingTripsFragment = (OngoingTripsFragment) manager.findFragmentByTag("OngoingTripsFragment");
            transaction.detach(ongoingTripsFragment);
            transaction.attach(ongoingTripsFragment);
        }
        transaction.replace(R.id.main_frameLayout, ongoingTripsFragment);
        transaction.commit();
    }

    private void showSettingsFragment() {
        manager = getSupportFragmentManager();
        SettingsFragment fragment = (SettingsFragment) manager.findFragmentByTag("SettingsFragment");
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == null) {
            settingsFragment = new SettingsFragment();
        } else {
            settingsFragment = (SettingsFragment) manager.findFragmentByTag("SettingsFragment");
        }
        transaction.replace(R.id.main_frameLayout, settingsFragment);
        transaction.commit();
    }

    private void initializeComponents() {
        frameLayout = findViewById(R.id.main_frameLayout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shared = getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        View headerView = navigationView.getHeaderView(0);
        userEmailTV = headerView.findViewById(R.id.user_email_menu);
        userEmailTV.setText(shared.getString("UserEmail", "ExampleEmail@examp.le"));
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            } else {
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            }
            ActivityCompat.requestPermissions(MainActivity.this, permissions, LOCATION_REQUEST_CODE);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.itsTimeToStart))
                .setMessage(getString(R.string.wannaStartTrip))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startTrip();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelTrip();
                        TripReminder tripReminder = TripReminder.getTripReminder();
                        if(tripReminder != null){
                            tripReminder.cancelReminder();
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void cancelTrip() {
        Trip trip = databaseAdapter.getSpecificTrip(shared.getInt("ClickedTripId", 0));
        PastTrip pastTrip = new PastTrip(trip.getTripName(), trip.getStartPoint(), trip.getEndPoint(), trip.getStartLatLong(), trip.getEndLatLong(), getString(R.string.nothing), getString(R.string.nothing), getString(R.string.nothing), PastTrip.IS_CANCELED);
        databaseAdapter.deleteTrip(trip.getId(), DatabaseAdapter.DatabaseHelper.TRIPS_TABLE_NAME);
        databaseAdapter.deleteTripFromFirebase(trip.getTripName());
        databaseAdapter.insertPastTrip(pastTrip);
        databaseAdapter.insertPastTripFirebase(pastTrip);
    }

    private void startTrip() {
        Trip currentTrip = databaseAdapter.getSpecificTrip(shared.getInt("ClickedTripId", 0));
        TripReminder tripReminder = TripReminder.getTripReminder();
        if(tripReminder != null){
            tripReminder.cancelReminder();
        }
        Intent intent = new Intent(this, LocationJobService.class);
        intent.putExtra("StartLatLong", currentTrip.getStartLatLong());
        intent.putExtra("EndLatLong", currentTrip.getEndLatLong());
        intent.putExtra("Direction", currentTrip.getDirection());
        intent.putExtra("Repeat", currentTrip.getRepeat());
        intent.putExtra("TripId", currentTrip.getId());
        LocationJobService.enqueueWork(this, intent);
        DisplayTrack(currentTrip.getStartPoint(), currentTrip.getEndPoint());
    }

    private void DisplayTrack(String startPoint, String endPoint) {
        try {
            Uri uri = Uri.parse("http://www.google.co.in/maps/dir/" + startPoint + "/" + endPoint);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showOngoingTripsFragment();
        boolean cameFromBroadcast = shared.getBoolean("CameFromBroadcast", false);
        if(cameFromBroadcast){
            showAlertDialog();
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean("CameFromBroadcast", false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission Required");
                builder.setMessage("The Application Needs Location Permission In Order To Give You The Best Experience");
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                    }
                });
                builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
}