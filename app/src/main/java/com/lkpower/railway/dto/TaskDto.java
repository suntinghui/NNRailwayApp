package com.lkpower.railway.dto;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 19/10/2016.
 */

public class TaskDto implements Serializable {

    private ResultDto Result;
    private List<TaskListInfoDto> DataInfo = null;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto result) {
        Result = result;
    }

    public List<TaskListInfoDto> getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(List<TaskListInfoDto> dataInfo) {
        DataInfo = dataInfo;
    }

    public static class TaskListInfoDto implements Serializable {
        private String ID;
        private String serialNumber;
        private String missionId;
        private String executor;
        private String executorName;
        private String state;
        private String remark;
        private String updateUser;
        private String updateTime;
        private String misName;
        private String misRemark;
        private String IsUploadPhoto;
        private ArrayList<ImgDataDto> imgData;

        private String stationId;
        private String stationName;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getMissionId() {
            return missionId;
        }

        public void setMissionId(String missionId) {
            this.missionId = missionId;
        }

        public String getExecutor() {
            return executor;
        }

        public void setExecutor(String executor) {
            this.executor = executor;
        }

        public String getExecutorName() {
            return executorName;
        }

        public void setExecutorName(String executorName) {
            this.executorName = executorName;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getMisName() {
            return misName;
        }

        public void setMisName(String misName) {
            this.misName = misName;
        }

        public String getMisRemark() {
            return misRemark;
        }

        public void setMisRemark(String misRemark) {
            this.misRemark = misRemark;
        }

        public String getIsUploadPhoto() {
            return IsUploadPhoto;
        }

        public void setIsUploadPhoto(String isUploadPhoto) {
            IsUploadPhoto = isUploadPhoto;
        }

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public String getStationId() {
            return stationId;
        }

        public void setStationId(String stationId) {
            this.stationId = stationId;
        }

        public ArrayList<ImgDataDto> getImgData() {
            return imgData;
        }

        public void setImgData(ArrayList<ImgDataDto> imgData) {
            this.imgData = imgData;
        }
    }
}
