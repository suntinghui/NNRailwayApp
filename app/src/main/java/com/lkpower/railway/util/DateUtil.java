package com.lkpower.railway.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	public static String getCurrentDate(){
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(today);
	}

	public static String getCurrentDateTime(){
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(today);
	}

	public static String getData(long time){
		Date date = new Date(time);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

}
