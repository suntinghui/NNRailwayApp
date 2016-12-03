package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 10/11/2016.
 */

public class TrainInfo implements Serializable {

    private String ID;
    private String TrainName;
    private String SerialNumber;
    private String InstanceId;
    private String Remark;
    private StationModel StartStation;
    private StationModel EndStation;
    private ArrayList<StationModel> StationInfo;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTrainName() {
        return TrainName;
    }

    public void setTrainName(String trainName) {
        TrainName = trainName;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getInstanceId() {
        return InstanceId;
    }

    public void setInstanceId(String instanceId) {
        InstanceId = instanceId;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public StationModel getStartStation() {
        return StartStation;
    }

    public void setStartStation(StationModel startStation) {
        StartStation = startStation;
    }

    public StationModel getEndStation() {
        return EndStation;
    }

    public void setEndStation(StationModel endStation) {
        EndStation = endStation;
    }

    public ArrayList<StationModel> getStationInfo() {
        return StationInfo;
    }

    public void setStationInfo(ArrayList<StationModel> stationInfo) {
        this.StationInfo = stationInfo;
    }
}
