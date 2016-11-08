package com.lkpower.railway.util;

import android.support.annotation.Nullable;

/**
 * Created by sth on 10/10/2016.
 */

public enum GDLocationType {

    GPS(1, "GPS定位结果"),BEFORE(2,"前次定位结果"),CACHE(4, "缓存定位结果"),WIFI(5, "Wifi定位结果"), GPRS(6, "基站定位结果"), OFFLINE(8, "离线定位结果");

    private int index;
    private String desc;

    private GDLocationType(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    @Nullable
    public static String getDesc(int index) {
        for (GDLocationType c : GDLocationType.values()) {
            if (c.getIndex() == index) {
                return c.desc;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }
}
