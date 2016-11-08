package com.lkpower.railway.dto;

import java.io.Serializable;

/**
 * Created by sth on 20/10/2016.
 */

public class TaskDetailDto implements Serializable {

    private ResultDto Result;
    private TaskDto.TaskListInfoDto DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto result) {
        Result = result;
    }

    public TaskDto.TaskListInfoDto getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(TaskDto.TaskListInfoDto dataInfo) {
        DataInfo = dataInfo;
    }
}
