package com.lkpower.railway.dto;

/**
 * Created by sth on 20/10/2016.
 */

public class ImgDataDto {

    private String ID;
    private String imgInfo;
    private String imgInfoNormalPath;
    private String imgInfoThumbPath;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImgInfo() {
        return imgInfo;
    }

    public void setImgInfo(String imgInfo) {
        this.imgInfo = imgInfo;
    }

    public String getImgInfoNormalPath() {
        return imgInfoNormalPath;
    }

    public void setImgInfoNormalPath(String imgInfoNormalPath) {
        this.imgInfoNormalPath = imgInfoNormalPath;
    }

    public String getImgInfoThumbPath() {
        return imgInfoThumbPath;
    }

    public void setImgInfoThumbPath(String imgInfoThumbPath) {
        this.imgInfoThumbPath = imgInfoThumbPath;
    }
}
