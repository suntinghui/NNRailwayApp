package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 10/11/2016.
 */

public class TrainDetailDto implements Serializable {
    private ResultDto Result;
    private List<TrainInfoDto> DataInfo = null;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto result) {
        Result = result;
    }

    public List<TrainInfoDto> getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(List<TrainInfoDto> dataInfo) {
        DataInfo = dataInfo;
    }
}
