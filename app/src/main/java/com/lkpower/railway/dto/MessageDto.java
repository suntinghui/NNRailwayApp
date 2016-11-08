package com.lkpower.railway.dto;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 19/10/2016.
 */

public class MessageDto implements Serializable {
    private ResultDto Result;
    private ArrayList<Message> DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto Result) {
        Result = Result;
    }

    public ArrayList<Message> getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(ArrayList<Message> dataInfo) {
        DataInfo = dataInfo;
    }

    public static class Message {
        private String ID;//任务实例ID
        private String serialNumber;//车次流水号
        private String missionId;//任务模板ID
        private String executor;//用户ID
        private String executorName;//用户姓名
        private String state;//任务状态 1未完成 2已完成
        private String remark;//描述信息
        private String updateUser;//更新人
        private String updateTime;//更新时间
        private String misName;//任务名称
        private String misRemark;//任务描述
        private String misDistance;// 预警距离
        private String misAheadTime;//预警时间

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

        public String getMisDistance() {
            return misDistance;
        }

        public void setMisDistance(String misDistance) {
            this.misDistance = misDistance;
        }

        public String getMisAheadTime() {
            return misAheadTime;
        }

        public void setMisAheadTime(String misAheadTime) {
            this.misAheadTime = misAheadTime;
        }

        public String getMisPrioritySet() {
            return misPrioritySet;
        }

        public void setMisPrioritySet(String misPrioritySet) {
            this.misPrioritySet = misPrioritySet;
        }

        private String misPrioritySet;//预警方式

    }
}
