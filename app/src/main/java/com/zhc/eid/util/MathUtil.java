package com.zhc.eid.util;

/**
 * Created by sth on 4/14/16.
 */
public class MathUtil {

    /**
     * (数据类型)(最小值+Math.random()*(最大值-最小值+1))
     *
     * @return
     */
    public static int getRandomInt(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }
}
