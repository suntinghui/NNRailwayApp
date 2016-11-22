package com.lkpower.railway.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Integer.parseInt;

public class DateUtil {

    public static String second2Time(long second) {
        try {
            if (second < 0)
                return "-- : -- : --";

            long hour = second / 3600;
            second = second % 3600;
            long min = second / 60;
            second = second % 60;
            long sec = second;

            String temp = String.format("%02d : %02d : %02d", hour, min, sec);
            return temp;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-- : -- : --";
    }

    public static String getCurrentDate() {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(today);
    }

    public static String getCurrentDate2() {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        return df.format(today);
    }

    public static String getCurrentDate3() {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d");
        return df.format(today);
    }

    public static String getCurrentYear() {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(today);
    }

    public static String getCurrentDay() {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM");
        return df.format(today);
    }

    public static String getCurrentDateTime() {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(today);
    }

    public static String getData(long time) {
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static Date getDate(String yyyyMd, String addDayStr, String addMinuteStr, String time) {
        int addDay = 0;
        int addMinute = 0;

        try {
            addDay = Integer.parseInt(addDayStr);
        } catch (Exception e) {
            addDay = 0;
        }

        try{
            addMinute = Integer.parseInt(addMinuteStr);
        } catch (Exception e) {
            addMinute = 0;
        }


        try {
            SimpleDateFormat sdftemp = new SimpleDateFormat("yyyy-M-d");
            Date date = sdftemp.parse(yyyyMd);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String yyyyMMdd = df.format(date);
            String dateTime = yyyyMMdd + " " + time;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            date = sdf.parse(dateTime);

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, addDay);
            calendar.add(calendar.MINUTE, -addMinute);
            Date date1 = calendar.getTime();

            return date1;

        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

}
