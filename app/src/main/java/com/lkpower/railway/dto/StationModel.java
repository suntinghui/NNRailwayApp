package com.lkpower.railway.dto;

import java.io.Serializable;

/**
 * Created by sth on 19/10/2016.
 */

public class StationModel implements Serializable {

    private String ID;
    private String stationName;
    private String stationType;
    private String startTime;
    private String arrivalTime;
    private String arrivalDay;
    private String longitude;
    private String latitude;
    private String prioritySet;  // 1时间  2距离
    private String aheadTime;
    private String distance;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        stationName = stationName;
    }

    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    public String getArrivalTime() {
        return arrivalTime.trim();
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalDay() {
        return arrivalDay;
    }

    public void setArrivalDay(String arrivalDay) {
        this.arrivalDay = arrivalDay;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStartTime() {
        return startTime.trim();
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPrioritySet() {
        return prioritySet;
    }

    public void setPrioritySet(String prioritySet) {
        this.prioritySet = prioritySet;
    }

    public String getAheadTime() {
        return aheadTime.trim();
    }

    public void setAheadTime(String aheadTime) {
        this.aheadTime = aheadTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
