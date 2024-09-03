package com.example.ourtodoist.javaobject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateUtility {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy h:m:s", Locale.US);
    private static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEEE, M/d/yyyy", Locale.US);
    public static final int MORNING_END = 11*60 + 59;
    public static final int AFTERNOON_END = 17*60+59;
    public static final int NIGHT_END = 23*60+59;
    public static int getAbsoluteTime(String clockTime){
        String[] time = clockTime.split(":");
        return Integer.parseInt(time[0])*60+ Integer.parseInt(time[1]);
    }
    public static String getDayState(String clockTime){
        int n = getAbsoluteTime(clockTime);
        if(n <= MORNING_END){
            return "Morning";
        }
        if(n <= AFTERNOON_END){
            return "Afternoon";
        }
        return "Night";
    }
    public static Date getDate(String mdy, String clockTime){
        Date date = null;
        try {
            date = dateFormat.parse(mdy + " " + clockTime + ":0");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String getTodayString(){
        return dateFormat2.format(new Date());
    }
    // No need to add 1 to month in LocalDateTime.
    public static int[] getDateArray(Date date){
        LocalDateTime local = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        int month = local.getMonthValue();
        int day = local.getDayOfMonth();
        int year = local.getYear();
        int hour = local.getHour();
        int minute = local.getMinute();
        return new int[]{year, month, day, hour, minute};
    }
    public static String getMDYString(Date date){
        LocalDateTime local = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        int month = local.getMonthValue();
        int day = local.getDayOfMonth();
        int year = local.getYear();
        return month+"/"+day+"/"+year;
    }
    public static String getClockTimeString(Date date){
        LocalDateTime local = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        int hour = local.getHour();
        int minute = local.getMinute();
        return String.format("%d:%02d", hour, minute);
    }
}
