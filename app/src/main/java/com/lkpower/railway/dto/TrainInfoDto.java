package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 10/11/2016.
 */

public class TrainInfoDto {
    private DeviceInfo DeviceInfo;
    private List<TrainDto.TrainDataInfo> TrainInfo;

    public com.lkpower.railway.dto.DeviceInfo getDeviceInfo() {
        return DeviceInfo;
    }

    public void setDeviceInfo(com.lkpower.railway.dto.DeviceInfo deviceInfo) {
        DeviceInfo = deviceInfo;
    }

    public List<TrainDto.TrainDataInfo> getTrainInfo() {
        return TrainInfo;
    }

    public void setTrainInfo(List<TrainDto.TrainDataInfo> trainInfo) {
        TrainInfo = trainInfo;
    }
}
