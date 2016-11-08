package com.lkpower.railway.dto;

import java.io.Serializable;

/**
 * Created by sth on 19/10/2016.
 */

public class ResultDto implements Serializable{
    private int Flag;
    private String FlagInfo;

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public String getFlagInfo() {
        return FlagInfo;
    }

    public void setFlagInfo(String flagInfo) {
        FlagInfo = flagInfo;
    }
}
