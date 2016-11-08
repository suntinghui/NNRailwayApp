package com.lkpower.railway.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by sth on 08/10/16.
 */

@Entity
public class Train {
    @Id
    private Long id;

    private String num;
    private String start;
    private String end;
    @Generated(hash = 1378820672)
    public Train(Long id, String num, String start, String end) {
        this.id = id;
        this.num = num;
        this.start = start;
        this.end = end;
    }
    @Generated(hash = 2030346186)
    public Train() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNum() {
        return this.num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    public String getStart() {
        return this.start;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public String getEnd() {
        return this.end;
    }
    public void setEnd(String end) {
        this.end = end;
    }

}
