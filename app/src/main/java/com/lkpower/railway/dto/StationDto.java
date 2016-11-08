package com.lkpower.railway.dto;

import java.io.Serializable;

/**
 * Created by sth on 19/10/2016.
 */

public class StationDto implements Serializable {

    private String ID;
    private String stationName;
    private String stationType;
    private String startTime;
    private String arrivalTime;
    private String arrivalDay;
    private String longitude;
    private String latitude;

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
        return arrivalTime;
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
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
