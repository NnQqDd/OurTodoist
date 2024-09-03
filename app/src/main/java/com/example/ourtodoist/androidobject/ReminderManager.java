package com.example.ourtodoist.androidobject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.ourtodoist.javaobject.DateUtility;
import com.example.ourtodoist.javaobject.Task;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ReminderManager {
    public static void createBroadcast(Context context, Task task){
        ReminderBroadcast reminder = new ReminderBroadcast();
        reminder.initialize("OurTodoistTask"+task.getId(), "OurTodoist",
                "You have "+task.getName()+" right now, "+SharedSetting.getUsername()+".", task.getId());
        LocalDateTime localDate = LocalDateTime.ofInstant(DateUtility.getDate(task.getDay(), task.getClockTime()).toInstant().plusSeconds(-1), ZoneId.systemDefault());
        reminder.setTimeDisplay(localDate);
        reminder.createReminderBroadcast(context);
    }
    public static void cancelBroadcast(Context context, Task task){
        cancelBroadcast(context, task.getId());
    }
    public static void cancelBroadcast(Context context, int id){
        String chanelId = "OurTodoistTask"+id;
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelId);
        if(notificationChannel != null){
            notificationManager.deleteNotificationChannel(chanelId);
            notificationManager.cancel(chanelId, 0);
            Intent myIntent = new Intent(context, ReminderBroadcast.class);
            PendingIntent.getBroadcast(context, id, myIntent,
                    PendingIntent.FLAG_IMMUTABLE).cancel();
        }
        notificationManager.deleteNotificationChannel(chanelId);
        notificationManager.cancel(chanelId, 0);
        Intent myIntent = new Intent(context, ReminderBroadcast.class);
        PendingIntent.getBroadcast(context, id, myIntent,
                PendingIntent.FLAG_IMMUTABLE).cancel();
    }
}
