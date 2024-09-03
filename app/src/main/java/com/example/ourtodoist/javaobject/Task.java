package com.example.ourtodoist.javaobject;

import java.util.Date;

public class Task implements Comparable<Task>{
    public static final int NOISY =  13513;
    public static final int QUIET = 1251;
    private int id;
    private String name;
    private int status;
    private String clockTime;
    private String day; //date ~ Day
    private String note;
    public Task(int id, String name, int status, String clockTime, String day, String note) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.clockTime = clockTime;
        this.day = day;
        this.note = note;
    }
    public int getId(){
        return this.id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getStatus(){
        return this.status;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public String getClockTime() {
        return clockTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    @Override
    public int compareTo(Task o) {
        return DateUtility.getDate(day, clockTime).compareTo(DateUtility.getDate(o.getDay(), o.getClockTime()));
    }
    public String getDayState(){
        return DateUtility.getDayState(clockTime);
    }
    public boolean toggleWithCheck(){
        if(DateUtility.getDate(day, clockTime).compareTo(new Date()) <= 0)
            return false;
        if(status == NOISY){
            status = QUIET;
        }
        else status = NOISY;
        return true;
    }
}
