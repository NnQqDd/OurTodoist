package com.example.ourtodoist.androidobject;


import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class OurNotificationListenerService extends NotificationListenerService {
    private Ringtone ringtone;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Context context = getApplicationContext();
        String myPackageName = context.getPackageName();
        String notificationPackageName = sbn.getPackageName();
        if (myPackageName.equals(notificationPackageName)) {
            Uri ringtoneUri = SharedSetting.getSound();
            ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            if(ringtone == null){
                Toast.makeText(context, "The sound is not specified", Toast.LENGTH_SHORT).show();
                return;
            }
            ringtone.play();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ringtone.stop();
                }
            }, SharedSetting.getPlayTime());
        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if(ringtone != null) ringtone.stop();
    }

}