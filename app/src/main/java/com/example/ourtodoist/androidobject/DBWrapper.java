package com.example.ourtodoist.androidobject;

import static java.time.temporal.ChronoUnit.DAYS;

import android.app.Activity;
import android.util.Log;

import androidx.core.text.HtmlCompat;

import com.example.ourtodoist.javaobject.DateUtility;
import com.example.ourtodoist.javaobject.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DBWrapper {
    // Do not call new() on any of this after init.
    static public OurTodoistDB database;
    static public ArrayList<Task> taskArrayList;
    public static void initialize(Activity act){
        database = new OurTodoistDB(act, "OurTodoistDB", null, 1);
        taskArrayList = database.getAllTask();
        Collections.sort(taskArrayList);
    }
    public static ArrayList<Task> search(String keyword){
        ArrayList<Task> searchList = new ArrayList<>();
        for(Task t: taskArrayList){
            String noteStr = HtmlCompat.fromHtml(t.getNote(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
            if(noteStr.toLowerCase().contains(keyword.toLowerCase())){
                searchList.add(t);
            }
            else if(t.getName().toLowerCase().contains(keyword.toLowerCase())){
                searchList.add(t);
            }
            else if(t.getDay().equalsIgnoreCase(keyword)){
                searchList.add(t);
            }
            else if(t.getClockTime().equalsIgnoreCase(keyword)){
                searchList.add(t);
            }
        }
        return searchList;
    }
    public static Task updateTask(Task task){
        boolean reSort = false;
        Task oldTask = null;
        for(Task t: taskArrayList){
            if(t.getId() == task.getId()){
                oldTask = t;
                t.setName(task.getName());
                t.setStatus(task.getStatus());
                if(t.compareTo(task) != 0)
                    reSort = true;
                t.setDay(task.getDay());
                t.setClockTime(task.getClockTime());
                t.setNote(task.getNote());
                break;
            }
        }
        if(reSort){
            Collections.sort(taskArrayList);
        }
        database.updateTask(task);
        return oldTask;
    }
    public static void insertTask(Task task){
        boolean flag = false;
        for(int i = 0; i < taskArrayList.size(); i++){
            if(task.compareTo(taskArrayList.get(i)) < 0){
                taskArrayList.add(i, task);
                flag = true;
                break;
            }
        }
        if(!flag) taskArrayList.add(task);
        database.insertTask(task);
    }
    public static Task deleteTask(Task task){
        Task oldTask = null;
        for(int i = 0; i < taskArrayList.size(); i++){
            oldTask = taskArrayList.get(i);
            if(task.getId() == taskArrayList.get(i).getId()){
                taskArrayList.remove(i);
                break;
            }
        }
        database.deleteTask(task);
        return oldTask;
    }
    public static void updateTasks(){ //Should be only called once
        //Log.v("UPDATE TASK", "CALLED");
        Date date = new Date();
        Instant instant = date.toInstant();
        for(int i = taskArrayList.size() - 1; i >= 0; i--){
            Task task = taskArrayList.get(i);
            Date taskDate = DateUtility.getDate(task.getDay(), task.getClockTime());
            Instant taskInstant = taskDate.toInstant();
            if(DAYS.between(taskInstant, instant) >= SharedSetting.getMaxConDay()){
                taskArrayList.remove(i);
                database.deleteTask(task);
            }
            else if(taskDate.compareTo(date) < 0){
                taskArrayList.get(i).setStatus(Task.QUIET);
                database.updateTask(task);
            }
        }
    }
    public static void quietTaskById(int id){
        for(Task task: taskArrayList){
            if(task.getId() == id){
                task.setStatus(Task.QUIET);
                database.updateTask(task);
                //Log.v("QUIET TASK BY ID", task.getName()+" - "+task.getId()+" - "+task.getStatus());
                break;
            }
        }
    }
}
