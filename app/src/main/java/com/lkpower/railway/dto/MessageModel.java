package com.lkpower.railway.dto;

import java.io.Serializable;
import java.security.SecureRandom;

/**
 * Created by sth on 10/11/2016.
 */

public class MessageModel implements Serializable {
    private String ID;
    private String Title;
    private String Content;
    private String DutyUser;
    private String SubmitTime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDutyUser() {
        return DutyUser;
    }

    public void setDutyUser(String dutyUser) {
        DutyUser = dutyUser;
    }

    public String getSubmitTime() {
        return SubmitTime;
    }

    public void setSubmitTime(String submitTime) {
        SubmitTime = submitTime;
    }
}
