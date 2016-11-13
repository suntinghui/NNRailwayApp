package com.lkpower.railway.dto;

/**
 * Created by sth on 09/11/2016.
 *
 * 设备信息 DeviceInfo {}
 *
 */

public class DeviceInfo {

    private String ID;
    private String UserName;
    private String LoginName;
    private String PassWord;
    private String Remark;
    private String TrainNumberClassId;
    private String Phone;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getTrainNumberClassId() {
        return TrainNumberClassId;
    }

    public void setTrainNumberClassId(String trainNumberClassId) {
        TrainNumberClassId = trainNumberClassId;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
