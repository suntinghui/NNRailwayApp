package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 19/10/2016.
 */

public class TrainStationDto implements Serializable {

    private ResultDto Result;
    private List<StationDto> DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto Result) {
        Result = Result;
    }

    public List<StationDto> getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(List<StationDto> dataInfo) {
        DataInfo = dataInfo;
    }
}
