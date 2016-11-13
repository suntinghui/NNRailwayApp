package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 10/11/2016.
 */

public class DeviceDetailInfoDto {

    private ResultDto Result;
    private DeviceDetailInfoDataInfo DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto result) {
        Result = result;
    }

    public DeviceDetailInfoDataInfo getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(DeviceDetailInfoDataInfo dataInfo) {
        DataInfo = dataInfo;
    }

    public static class DeviceDetailInfoDataInfo implements Serializable {
        private DeviceInfo DeviceInfo;
        private List<TrainInfo> TrainInfo;

        public com.lkpower.railway.dto.DeviceInfo getDeviceInfo() {
            return DeviceInfo;
        }

        public void setDeviceInfo(com.lkpower.railway.dto.DeviceInfo deviceInfo) {
            DeviceInfo = deviceInfo;
        }

        public List<com.lkpower.railway.dto.TrainInfo> getTrainInfo() {
            return TrainInfo;
        }

        public void setTrainInfo(List<com.lkpower.railway.dto.TrainInfo> trainInfo) {
            TrainInfo = trainInfo;
        }
    }
}
