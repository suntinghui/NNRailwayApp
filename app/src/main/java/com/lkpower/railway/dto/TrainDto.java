package com.lkpower.railway.dto;

import android.text.StaticLayout;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 19/10/2016.
 */

public class TrainDto implements Serializable {
    private ResultDto Result;
    private List<TrainDataInfo> DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto Result) {
        Result = Result;
    }

    public List<TrainDataInfo> getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(List<TrainDataInfo> dataInfo) {
        DataInfo = dataInfo;
    }

    public static class TrainDataInfo implements Serializable{
        private String ID;
        private String TrainName;
        private String Remark;
        private StationDto StartStation;
        private StationDto EndStation;

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

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
        }

        public StationDto getStartStation() {
            return StartStation;
        }

        public void setStartStation(StationDto startStation) {
            StartStation = startStation;
        }

        public StationDto getEndStation() {
            return EndStation;
        }

        public void setEndStation(StationDto endStation) {
            EndStation = endStation;
        }
    }
}
