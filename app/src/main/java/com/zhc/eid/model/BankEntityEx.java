package com.zhc.eid.model;

import com.zhc.eid.MyApplication;

import java.io.Serializable;

/**
 * Created by sth on 11/28/15.
 */
public class BankEntityEx extends BankEntity implements Serializable {

    public BankEntityEx(String name, String code, String img, String single, String day, String month) {
        this.setName(name);
        this.setCode(code);
        this.setImg(img);
        this.setSingle(Integer.parseInt(single));
        this.setDay(Integer.parseInt(day));
        this.setMonth(Integer.parseInt(month));
    }

    public String getLimitStr() {

        StringBuffer limitSB = new StringBuffer();
        if (this.getSingle() != 0) {
            limitSB.append("本卡单笔限额" + this.getSingle() + "元 ");
        }
        if (this.getDay() != 0) {
            limitSB.append("单日限额" + this.getDay() + "元 ");
        }
        if (this.getMonth() != 0) {
            limitSB.append("单月限额" + this.getMonth() + "元");
        }

        return limitSB.toString();
    }

    public int getLogoId() {
        return MyApplication.getInstance().getResources().getIdentifier(this.getImg(), "drawable", MyApplication.getInstance().getPackageName());
    }

}
