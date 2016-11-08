package com.lkpower.railway.dto;

import java.io.Serializable;

import javax.xml.transform.Result;

/**
 * Created by sth on 19/10/2016.
 * response:{"Result":{"Flag":"1","FlagInfo":"正常"},"DataInfo":{"ID":"644f8661-11f6-4ebd-a77d-5d80b98a5ca0","UserName":"孙大鹏","LoginName":"sdp","PassWord":"aaaaaaa","Remark":"","TrainNumberClassId":"02988309-9cf9-479f-8472-f46c9f884aa9"}}
 */

public class LoginDto implements Serializable{

    private ResultDto Result;
    private LoginDataInfoDto DataInfo;

    public ResultDto getResult() {
        return Result;
    }

    public void setResult(ResultDto Result) {
        Result = Result;
    }

    public LoginDataInfoDto getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(LoginDataInfoDto dataInfo) {
        DataInfo = dataInfo;
    }

    public static class LoginDataInfoDto implements Serializable{
        private String ID;
        private String UserName;
        private String LoginName;
        private String PassWord;
        private String Remark;
        private String TrainNumberClassId;

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
    }
}
