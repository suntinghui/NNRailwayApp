package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 19/10/2016.
 */

public class TrainStationDto implements Serializable {

    private ResultDto Result;
    private TrainInfoDto DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto result) {
        Result = result;
    }

    public TrainInfoDto getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(TrainInfoDto dataInfo) {
        DataInfo = dataInfo;
    }
}
