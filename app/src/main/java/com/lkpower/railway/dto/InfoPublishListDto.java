package com.lkpower.railway.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sth on 10/11/2016.
 */

public class InfoPublishListDto implements Serializable {

    private ResultDto Result;
    private List<MessageModel> DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto result) {
        Result = result;
    }

    public List<MessageModel> getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(List<MessageModel> dataInfo) {
        DataInfo = dataInfo;
    }
}
