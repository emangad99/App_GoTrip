package com.mayv.gotrip;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class DatabaseAdapter {

    private static DatabaseHelper helper;
    private final DatabaseReference databaseReference;
    Context context;

    public DatabaseAdapter(Context context) {
        if (helper == null) {
            helper = new DatabaseHelper(context);
        }
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getUid() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("UserID", "user");
    }

    private void saveTripId(int id){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ClickedTripId", id);
        editor.apply();
    }

    public void insertTripIntoFirebase(Trip trip) {
        databaseReference.child("Users").child(getUid()).child("Trips").child(trip.getTripName()).setValue(trip);
    }

    public void deleteTripFromFirebase(String tripName) {
        databaseReference.child("Users").child(getUid()).child("Trips").child(tripName).removeValue();
    }

    public void insertPastTripFirebase(PastTrip trip) {
        databaseReference.child("Users").child(getUid()).child("Past Trips").child(trip.getTripName()).setValue(trip);
    }

    public void deletePastTripFirebase(String tripName) {
        databaseReference.child("Users").child(getUid()).child("Past Trips").child(tripName).removeValue();
    }

    public void insertTrip(Trip trip) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.TRIP_NAME, trip.getTripName());
        cv.put(DatabaseHelper.START_POINT, trip.getStartPoint());
        cv.put(DatabaseHelper.END_POINT, trip.getEndPoint());
        cv.put(DatabaseHelper.START_LATLONG, trip.getStartLatLong());
        cv.put(DatabaseHelper.END_LATLONG, trip.getEndLatLong());
        cv.put(DatabaseHelper.DATE, trip.getDate());
        cv.put(DatabaseHelper.HOUR, trip.getHour());
        cv.put(DatabaseHelper.REPEAT, trip.getRepeat());
        cv.put(DatabaseHelper.DIRECTION, trip.getDirection());
        cv.put(DatabaseHelper.NOTES, trip.getNotes());
        db.insert(DatabaseHelper.TRIPS_TABLE_NAME, null, cv);
    }

    public ArrayList<Trip> getTrips() {
        ArrayList<Trip> trips = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {DatabaseHelper.ID, DatabaseHelper.TRIP_NAME, DatabaseHelper.START_POINT, DatabaseHelper.END_POINT, DatabaseHelper.START_LATLONG, DatabaseHelper.END_LATLONG, DatabaseHelper.DATE, DatabaseHelper.HOUR, DatabaseHelper.REPEAT, DatabaseHelper.DIRECTION, DatabaseHelper.NOTES};
        Cursor c = db.query(DatabaseHelper.TRIPS_TABLE_NAME, columns, null, null, null, null, null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String tripName = c.getString(1);
            String startPoint = c.getString(2);
            String endPoint = c.getString(3);
            String startLatLong = c.getString(4);
            String endLatLong = c.getString(5);
            String date = c.getString(6);
            String hour = c.getString(7);
            String repeat = c.getString(8);
            String direction = c.getString(9);
            String notes = c.getString(10);
            Trip trip = new Trip(tripName, startPoint, endPoint, startLatLong, endLatLong, date, hour, repeat, direction, notes);
            trip.setId(id);
            saveTripId(id);
            trips.add(trip);
        }
        return trips;
    }

    public Trip getSpecificTrip(int id) {
        Trip trip = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TRIPS_TABLE_NAME + " WHERE Id = " + id + ";", null);
        while (c.moveToNext()) {
            String tripName = c.getString(1);
            String startPoint = c.getString(2);
            String endPoint = c.getString(3);
            String startLatLong = c.getString(4);
            String endLatLong = c.getString(5);
            String date = c.getString(6);
            String hour = c.getString(7);
            String repeat = c.getString(8);
            String direction = c.getString(9);
            String notes = c.getString(10);
            trip = new Trip(tripName, startPoint, endPoint, startLatLong, endLatLong, date, hour, repeat, direction, notes);
            trip.setId(id);
            saveTripId(id);
        }
        return trip;
    }

    public void updateTrip(Trip trip) {
        saveTripId(trip.getId());
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TRIP_NAME, trip.getTripName());
        values.put(DatabaseHelper.START_POINT, trip.getStartPoint());
        values.put(DatabaseHelper.END_POINT, trip.getEndPoint());
        values.put(DatabaseHelper.START_LATLONG, trip.getStartLatLong());
        values.put(DatabaseHelper.END_LATLONG, trip.getEndLatLong());
        values.put(DatabaseHelper.DATE, trip.getDate());
        values.put(DatabaseHelper.HOUR, trip.getHour());
        values.put(DatabaseHelper.REPEAT, trip.getRepeat());
        values.put(DatabaseHelper.DIRECTION, trip.getDirection());
        values.put(DatabaseHelper.NOTES, trip.getNotes());
        String whereClause = "Id = " + trip.getId();
        db.update(DatabaseHelper.TRIPS_TABLE_NAME, values, whereClause, null);
    }

    public void deleteTrip(int id, String tableName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE Id = " + id);
    }

    public void deleteAllTrips(String tableName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
    }

    public void insertPastTrip(PastTrip trip) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.TRIP_NAME, trip.getTripName());
        cv.put(DatabaseHelper.START_POINT, trip.getStartPoint());
        cv.put(DatabaseHelper.END_POINT, trip.getEndPoint());
        cv.put(DatabaseHelper.START_LATLONG, trip.getStartLatLng());
        cv.put(DatabaseHelper.END_LATLONG, trip.getEndLatLng());
        cv.put(DatabaseHelper.DISTANCE_PER_TIME, trip.getDistancePerDuration());
        cv.put(DatabaseHelper.DURATION, trip.getDuration());
        cv.put(DatabaseHelper.AVERAGE_SPEED, trip.getAverageSpeed());
        cv.put(DatabaseHelper.STATUS, trip.getStatus());
        db.insert(DatabaseHelper.PAST_TRIPS_TABLE_NAME, null, cv);
    }

    public ArrayList<PastTrip> getPastTrips() {
        ArrayList<PastTrip> trips = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {DatabaseHelper.ID, DatabaseHelper.TRIP_NAME, DatabaseHelper.START_POINT, DatabaseHelper.END_POINT, DatabaseHelper.START_LATLONG, DatabaseHelper.END_LATLONG, DatabaseHelper.DISTANCE_PER_TIME, DatabaseHelper.DURATION, DatabaseHelper.AVERAGE_SPEED, DatabaseHelper.STATUS};
        Cursor c = db.query(DatabaseHelper.PAST_TRIPS_TABLE_NAME, columns, null, null, null, null, null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String tripName = c.getString(1);
            String startPoint = c.getString(2);
            String endPoint = c.getString(3);
            String startLatLng = c.getString(4);
            String endLatLng = c.getString(5);
            String distancePerTime = c.getString(6);
            String duration = c.getString(7);
            String averageSpeed = c.getString(8);
            int status = c.getInt(9);
            PastTrip trip = new PastTrip(tripName, startPoint, endPoint, startLatLng, endLatLng, distancePerTime, duration, averageSpeed, status);
            trip.setId(id);
            trips.add(trip);
        }
        return trips;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "MyDB";
        public static final int DATABASE_VERSION = 1;
        public static final String TRIPS_TABLE_NAME = "Trips";
        public static final String PAST_TRIPS_TABLE_NAME = "PastTrips";
        public static final String ID = "Id";
        public static final String TRIP_NAME = "TripName";
        public static final String START_POINT = "StartPoint";
        public static final String END_POINT = "EndPoint";
        public static final String START_LATLONG = "StartLatLong";
        public static final String END_LATLONG = "EndLatLong";
        public static final String DATE = "Date";
        public static final String HOUR = "Hour";
        public static final String REPEAT = "Repeat";
        public static final String DIRECTION = "Direction";
        public static final String NOTES = "Notes";
        public static final String DISTANCE_PER_TIME = "DistancePerTime";
        public static final String DURATION = "Duration";
        public static final String AVERAGE_SPEED = "AverageSpeed";
        public static final String STATUS = "Status";
        public static final String CREATE_TRIPS_TABLE = "CREATE TABLE " + TRIPS_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TRIP_NAME + " VARCHAR(50), " + START_POINT + " VARCHAR(50), " + END_POINT + " VARCHAR(50), " + START_LATLONG + " VARCHAR(50), " + END_LATLONG + " VARCHAR(50), " + DATE + " VARCHAR(50), " + HOUR + " VARCHAR(50), " + REPEAT + " VARCHAR(50), " + DIRECTION + " VARCHAR(50), " + NOTES + " VARCHAR(50));";
        public static final String CREATE_PAST_TRIPS_TABLE = "CREATE TABLE " + PAST_TRIPS_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TRIP_NAME + " VARCHAR(50), " + START_POINT + " VARCHAR(50), " + END_POINT + " VARCHAR(50), " + START_LATLONG + " VARCHAR(50), " + END_LATLONG + " VARCHAR(50), " + DISTANCE_PER_TIME + " VARCHAR(50), " + DURATION + " VARCHAR(50), " + AVERAGE_SPEED + " VARCHAR(50), " + STATUS + " INTEGER);";


        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TRIPS_TABLE);
            db.execSQL(CREATE_PAST_TRIPS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PAST_TRIPS_TABLE_NAME);
            onCreate(db);
        }
    }
}
