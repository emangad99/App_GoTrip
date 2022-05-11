package com.mayv.gotrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment {


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                addPolyLines(googleMap);
            }
        }
    };

    private void addPolyLines(GoogleMap googleMap) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED);
        ArrayList<PastTrip> pastTrips = databaseAdapter.getPastTrips();
        for(PastTrip pastTrip : pastTrips){
            if(pastTrip.getStatus() == PastTrip.IS_DONE){
                String[] startSplit = pastTrip.getStartLatLng().split(",,");
                String[] endSplit = pastTrip.getEndLatLng().split(",,");
                LatLng startLatLng = new LatLng(Double.parseDouble(startSplit[0]), Double.parseDouble(startSplit[1]));
                LatLng endLatLng = new LatLng(Double.parseDouble(endSplit[0]), Double.parseDouble(endSplit[1]));
                options.add(startLatLng, endLatLng);
            }
        }
        googleMap.addPolyline(options);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}