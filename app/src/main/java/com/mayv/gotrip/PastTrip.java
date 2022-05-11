package com.mayv.gotrip;

public class PastTrip {

    private int id;
    private String tripName;
    private String startPoint;
    private String endPoint;
    private String startLatLng;
    private String endLatLng;
    private String distancePerDuration;
    private String duration;
    private String averageSpeed;
    private int status;
    public final static int IS_DELETED = 304;
    public final static int IS_CANCELED = 302;
    public final static int IS_DONE = 300;

    public PastTrip(String tripName, String startPoint, String endPoint, String startLatLng, String endLatLng, String distancePerDuration, String duration, String averageSpeed, int status) {
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startLatLng = startLatLng;
        this.endLatLng = endLatLng;
        this.distancePerDuration = distancePerDuration;
        this.duration = duration;
        this.averageSpeed = averageSpeed;
        this.status = status;
    }

    public PastTrip(){

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

    public String getStartLatLng() {
        return startLatLng;
    }

    public void setStartLatLng(String startLatLng) {
        this.startLatLng = startLatLng;
    }

    public String getEndLatLng() {
        return endLatLng;
    }

    public void setEndLatLng(String endLatLng) {
        this.endLatLng = endLatLng;
    }

    public String getDistancePerDuration() {
        return distancePerDuration;
    }

    public void setDistancePerDuration(String distancePerDuration) {
        this.distancePerDuration = distancePerDuration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
