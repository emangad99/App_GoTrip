package com.mayv.gotrip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OngoingTripsFragment extends Fragment implements Communicator {

    View view;
    FloatingActionButton addTrip;
    ArrayList<Trip> trips;
    OngoingTripAdapter adapter;
    DatabaseAdapter databaseAdapter;
    ListView listView;
    ProgressBar progressBar;
    Handler handler;
    Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleHelper.setLanguage(getContext());
        view = inflater.inflate(R.layout.fragment_ongoing_trips, container, false);
        addTrip = view.findViewById(R.id.button_add_trip);
        progressBar = view.findViewById(R.id.progressBar);
        databaseAdapter = new DatabaseAdapter(getContext());
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                trips = databaseAdapter.getTrips();
                listView = view.findViewById(R.id.ongoing_trips_listView);
                adapter = new OngoingTripAdapter(getContext(), trips, OngoingTripsFragment.this);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        };
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTripActivity.class);
                startActivity(intent);
            }
        });
        startProgressListener();
        return view;
    }

    private void reloadTripsFirebase() {
        SharedPreferences shared = getActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        boolean needToLoad = shared.getBoolean("NeedFirebaseLoad", true);
        String uid = shared.getString("UserID", "user");
        editor.putBoolean("NeedFirebaseLoad", false);
        editor.apply();
        if (needToLoad) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Trips");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                        Trip trip = tripSnapshot.getValue(Trip.class);
                        databaseAdapter.insertTrip(trip);
                    }
                    handler.postDelayed(runnable, 100);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //
                }
            });
        } else {
            handler.postDelayed(runnable, 100);
        }
    }

    @Override
    public void deleteTripFromList(int id, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm));
        builder.setMessage(getString(R.string.areYouSure));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                databaseAdapter.deleteTrip(id, DatabaseAdapter.DatabaseHelper.TRIPS_TABLE_NAME);
                trips.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startProgressListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post((new Runnable() {
                    @Override
                    public void run() {
                        reloadTripsFirebase();
                    }
                }));
            }
        }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}