package com.yezi.meizhi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static String getCurrentWeek() {
        return formatWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    public static String formatWeek(int week) {
        String strWeek = null;
        if (1 == week) {
            strWeek = "天";
        } else if (2 == week) {
            strWeek = "一";
        } else if (3 == week) {
            strWeek = "二";
        } else if (4 == week) {
            strWeek = "三";
        } else if (5 == week) {
            strWeek = "四";
        } else if (6 == week) {
            strWeek = "五";
        } else if (7 == week) {
            strWeek = "六";
        }
        return strWeek;
    }

    public static String getWeek(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(time.substring(0, time.indexOf("T"))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatWeek(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static int[] getDayAndMonth(String time) {
        int[] dayAndMonth = new int[2];
        int firstLine = time.indexOf("-");
        int secondLine = time.indexOf("-", firstLine + 1);
        int letterT = time.indexOf("T");
        dayAndMonth[0] = Integer.valueOf(time.substring(secondLine + 1, letterT));
        dayAndMonth[1] = Integer.valueOf(time.substring(firstLine + 1, secondLine));
        return dayAndMonth;
    }
}
