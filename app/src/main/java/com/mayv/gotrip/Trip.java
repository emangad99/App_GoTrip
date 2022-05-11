package com.mayv.gotrip;

import java.util.ArrayList;

public class Trip {

    private int id;
    private String tripName;
    private String startPoint;
    private String endPoint;
    private String startLatLong;
    private String endLatLong;
    private String date;
    private String hour;
    private String repeat;
    private String direction;
    private String notes;

    public Trip(String tripName, String startPoint, String endPoint, String startLatLong, String endLatLong, String date, String hour, String repeat, String direction, String notes) {
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startLatLong = startLatLong;
        this.endLatLong = endLatLong;
        this.date = date;
        this.hour = hour;
        this.repeat = repeat;
        this.direction = direction;
        this.notes = notes;
    }

    public Trip(){
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getStartLatLong() {
        return startLatLong;
    }

    public void setStartLatLong(String startLatLong) {
        this.startLatLong = startLatLong;
    }

    public String getEndLatLong() {
        return endLatLong;
    }

    public void setEndLatLong(String endLatLong) {
        this.endLatLong = endLatLong;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

}
