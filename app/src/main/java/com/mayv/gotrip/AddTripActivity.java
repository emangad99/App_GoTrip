package com.mayv.gotrip;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity {
    Spinner repeatSpinner, directionSpinner;
    TextInputEditText tripNameET, startPointET, endPointET, dateEt, hourEt;
    Button addTripBtn;
    Calendar calendar;
    private String API_KEY;
    private String startLatLong = "";
    private String endLatLong = "";
    private boolean editing = false;
    private boolean startPoint = false;
    private int currentTripId;
    private Trip currentEditingTrip;
    DatabaseAdapter databaseAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;
    AlarmManager alarmManager;
    TripReminder tripReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_add_trip);
        initializeComponents();
        if (editing) {
            addTripBtn.setText(R.string.save);
            currentEditingTrip = databaseAdapter.getSpecificTrip(currentTripId);
            setEditingValues();
        }
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(year < calendar.get(Calendar.YEAR) || month < calendar.get(Calendar.MONTH) || dayOfMonth < calendar.get(Calendar.DAY_OF_MONTH)){
                    Toast.makeText(AddTripActivity.this, "Invalid Date", Toast.LENGTH_LONG).show();
                }else {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    dateEt.setText(simpleDateFormat.format(calendar.getTime()));
                }
            }

        };
        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddTripActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        hourEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay < calendar.get(Calendar.HOUR_OF_DAY)){
                            Toast.makeText(AddTripActivity.this, "Invalid Time", Toast.LENGTH_LONG).show();
                        }else {
                            hourEt.setText(hourOfDay + ":" + minute);
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                        }
                    }
                }, hour, min, false);
                mTimePicker.show();
            }
        });
        startPointET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPoint = true;
                autocompleteIntent();
            }
        });
        endPointET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPoint = false;
                autocompleteIntent();
            }
        });
        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfEmpty()) {
                    addToDatabase();
                    setReminder();
                    finish();
                } else {
                    Toast.makeText(AddTripActivity.this, "Please Fill All Fields", Toast.LENGTH_LONG).show();
                }
            }
        });
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            LatLng latLng = place.getLatLng();
                            if(startPoint){
                                startPointET.setText(place.getName());
                                startLatLong = latLng.latitude + ",," + latLng.longitude;
                            }else{
                                endPointET.setText(place.getName());
                                endLatLong = latLng.latitude + ",," + latLng.longitude;
                            }
                        }
                    }
                });
    }

    private int repeatSpinnerSelection(String string){
        switch (string) {
            case "Once":
                return 0;
            case "Daily":
                return 1;
            case "Weekly":
                return 2;
            default:
                return 3;
        }
    }

    private void setEditingValues() {
        if(editing){
            tripNameET.setText(currentEditingTrip.getTripName());
            startPointET.setText(currentEditingTrip.getStartPoint());
            endPointET.setText(currentEditingTrip.getEndPoint());
            dateEt.setText(currentEditingTrip.getDate());
            hourEt.setText(currentEditingTrip.getHour());
            repeatSpinner.setSelection(repeatSpinnerSelection(currentEditingTrip.getRepeat()));
            if(currentEditingTrip.getDirection().equals("One Direction")){
                directionSpinner.setSelection(0);
            }else{
                directionSpinner.setSelection(1);
            }
        }
    }

    private int getSpinnerRepeat(){
        if(repeatSpinner.getSelectedItemId() == 0){
            return TripReminder.ONCE;
        }else if(repeatSpinner.getSelectedItemId() == 1){
            return TripReminder.DAILY;
        }else if(repeatSpinner.getSelectedItemId() == 2){
            return TripReminder.WEEKLY;
        }else{
            return TripReminder.MONTHLY;
        }
    }

    private void setReminder() {
        tripReminder = new TripReminder(this, calendar, alarmManager);
        tripReminder.setReminder(getSpinnerRepeat());
        TripReminder.setTripReminder(tripReminder);
    }

    private void addToDatabase() {
        String tripName = tripNameET.getText().toString();
        String startPoint = startPointET.getText().toString();
        String endPoint = endPointET.getText().toString();
        String date = dateEt.getText().toString();
        String hour = hourEt.getText().toString();
        String repeat = repeatSpinner.getSelectedItem().toString();
        String direction = directionSpinner.getSelectedItem().toString();
        Trip trip = new Trip(tripName, startPoint, endPoint, startLatLong, endLatLong, date, hour, repeat, direction, "");
        if (editing) {
            trip.setId(currentTripId);
            databaseAdapter.updateTrip(trip);
        } else {
            databaseAdapter.insertTrip(trip);
            databaseAdapter.insertTripIntoFirebase(trip);
        }
    }

    private boolean checkIfEmpty() {
        return tripNameET.getText().toString().isEmpty() || startPointET.getText().toString().isEmpty() ||
                endPointET.getText().toString().isEmpty() || dateEt.getText().toString().isEmpty() || hourEt.getText().toString().isEmpty();
    }

    private void autocompleteIntent() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG))
                .build(AddTripActivity.this);
        activityResultLauncher.launch(intent);
    }

    private void initializeComponents() {
        repeatSpinner = findViewById(R.id.spinner_repeat);
        directionSpinner = findViewById(R.id.spinner_direction);
        tripNameET = findViewById(R.id.add_trip_name);
        startPointET = findViewById(R.id.add_trip_start);
        endPointET = findViewById(R.id.add_trip_end);
        addTripBtn = findViewById(R.id.button_add);
        dateEt = findViewById(R.id.add_trip_date);
        hourEt = findViewById(R.id.add_trip_hour);
        calendar = Calendar.getInstance();
        API_KEY = getString(R.string.google_maps_key);
        databaseAdapter = new DatabaseAdapter(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        editing = getIntent().getBooleanExtra("Edit", false);
        currentTripId = getIntent().getIntExtra("CurrentId", -1);
    }
}