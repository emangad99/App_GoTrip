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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripHistoryFragment extends Fragment {

    View view;
    DatabaseAdapter databaseAdapter;
    ArrayList<PastTrip> pastTrips;
    ProgressBar progressBar;
    Handler handler;
    Runnable runnable;
    TripHistoryAdapter adapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleHelper.setLanguage(getContext());
        view = inflater.inflate(R.layout.fragment_trip_history, container, false);
        databaseAdapter = new DatabaseAdapter(getContext());
        progressBar = view.findViewById(R.id.past_progressBar);
        listView = view.findViewById(R.id.trip_history_listView);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                pastTrips = databaseAdapter.getPastTrips();
                adapter = new TripHistoryAdapter(getActivity().getApplicationContext(), pastTrips);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        };
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.confirm));
                builder.setMessage(getString(R.string.areYouSureHistory));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        PastTrip clickedTrip = pastTrips.get(position);
                        databaseAdapter.deleteTrip(clickedTrip.getId(), DatabaseAdapter.DatabaseHelper.PAST_TRIPS_TABLE_NAME);
                        databaseAdapter.deletePastTripFirebase(clickedTrip.getTripName());
                        pastTrips.remove(position);
                        adapter.notifyDataSetChanged();
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
                return true;
            }
        });
        startProgressListener();
        return view;
    }

    private void reloadTripsFirebase() {
        SharedPreferences shared = getActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        boolean needToLoad = shared.getBoolean("PastTripsLoad", true);
        String uid = shared.getString("UserID", "user");
        editor.putBoolean("PastTripsLoad", false);
        editor.apply();
        if (needToLoad) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Past Trips");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                        PastTrip trip = tripSnapshot.getValue(PastTrip.class);
                        databaseAdapter.insertPastTrip(trip);
                    }
                    handler.postDelayed(runnable, 100);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Canceled
                }
            });
        } else {
            handler.postDelayed(runnable, 100);
        }
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