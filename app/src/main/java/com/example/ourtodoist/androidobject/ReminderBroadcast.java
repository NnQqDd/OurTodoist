package com.example.ourtodoist.androidobject;
import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ourtodoist.AC;
import com.example.ourtodoist.R;

import java.time.LocalDateTime;
import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {
    private String channelId;
    private String contentTitle;
    private String contentText;
    //private Class<?> returnActivity = MainActivity.class;
    //private String DATA_KEY_SEND = "DATA_KEY_SEND";
    //private String DATA_VALUE_SEND = "Welcome back!";
    private int requestCode;
    private long timeAppear;
/*
    public void setReturnActivity(Class<?> returnActivity, String dataKey, String dataValue){

        this.returnActivity = returnActivity;
        DATA_KEY_SEND = dataKey;
        DATA_VALUE_SEND = dataValue;
    }
*/
    public void initialize(String channelId, String contentTitle, String contentText, int requestCode) {
        this.channelId = channelId;
        this.contentTitle = contentTitle;
        this.contentText = contentText;
        this.requestCode = requestCode;
    }
    public void setTimeDisplay(@NonNull LocalDateTime dateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dateTime.getYear());
        calendar.set(Calendar.MONTH, dateTime.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dateTime.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHour());
        calendar.set(Calendar.MINUTE, dateTime.getMinute());
        calendar.set(Calendar.SECOND, dateTime.getSecond());
        this.timeAppear = calendar.getTimeInMillis();
    }

    public void createReminderBroadcast(Context context){
        Intent intent = new Intent(context, ReminderBroadcast.class);
        intent.putExtra("channelId", channelId);
        intent.putExtra("contentTitle", contentTitle);
        intent.putExtra("contentText", contentText);
        //intent.putExtra("returnActivity", returnActivity);
        //intent.putExtra("DATA_KEY_SEND", DATA_KEY_SEND);
        //intent.putExtra("DATA_VALUE_SEND", DATA_VALUE_SEND);;
        //Log.v("INTENT REQUESTCODE", requestCode+"");
        intent.putExtra("requestCode", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAppear,
                pendingIntent);
    }
    // Do not use any property value inside this class!
    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        String currentChannelId = intent.getStringExtra("channelId");
        int currentRequestCode = intent.getIntExtra("requestCode", -1);
        //Intent serviceIntent = new Intent(context, OurNotificationListenerService.class);
        //context.startService(serviceIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, currentChannelId)
                .setSmallIcon(R.drawable.task)
                .setContentTitle(intent.getStringExtra("contentTitle"))
                .setContentText(intent.getStringExtra("contentText"))
                .setSound(null)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        /*PendingIntent pendingIntent =
                setNotificationAction(
                        context,
                        (Class<?>)intent.getSerializableExtra("returnActivity"),
                        intent.getStringExtra("DATA_KEY_SEND"),
                        intent.getStringExtra("DATA_VALUE_SEND"),
                        intent.getIntExtra("requestCode",999));
        builder.setContentIntent(pendingIntent);*/
        //createNotificationChannel(builder, context, chanelId);
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(currentChannelId);
        if(notificationChannel == null){
            int importance = NotificationManager.IMPORTANCE_LOW;
            notificationChannel = new NotificationChannel(currentChannelId, "This is truly our Todoist.", importance);
            //notificationChannel.setLightColor(Color.GREEN);
            //notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationChannel.setSound(null,null);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(0, builder.build());
        if(currentChannelId.contains("OurTodoistTask")){
            Intent broadcastIntent = new Intent(AC.QUIET_TASK_BROADCAST);
            //Log.v("BROADCAST REQUESTCODE", currentRequestCode+"");
            broadcastIntent.putExtra("requestCode", currentRequestCode);
            context.sendBroadcast(broadcastIntent);
        }
    }
}