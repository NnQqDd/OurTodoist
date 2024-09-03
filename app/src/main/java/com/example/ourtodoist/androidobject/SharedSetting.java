package com.example.ourtodoist.androidobject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class SharedSetting {
    private static SharedPreferences sharedPref;
    public static void initialize(Context context) {
        SharedSetting.sharedPref = context.getSharedPreferences("OurTodoist", Context.MODE_PRIVATE);

    }
    public static int getAndIncId() {
        int maxTaskId = sharedPref.getInt("MAX_TASK_ID", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("MAX_TASK_ID", maxTaskId + 1);
        editor.apply();
        return maxTaskId;
    }
    public static String getUsername() {
        return sharedPref.getString("USERNAME", "Username");
    }
    public static void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("USERNAME", username);
        editor.apply();
    }

    public static Bitmap getAvatar() {
        String avatar = sharedPref.getString("AVATAR", null);
        if(avatar == null){
            return null;
        }
        byte[] array = Base64.decode(avatar, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    public static void setAvatar(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        try{
            byteArrayOutputStream.close();
        }
        catch(Exception ignored){}
        SharedPreferences.Editor editor = sharedPref.edit();
        String avatar = Base64.encodeToString(array, Base64.DEFAULT);
        editor.putString("AVATAR", avatar);
        editor.apply();
    }
    public static int getMaxConDay() {
        return sharedPref.getInt("MAX_CON_DAY", 7);
    }

    public static void setMaxConDay(int maxConDay) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("MAX_CON_DAY", maxConDay);
        editor.apply();
    }
    public static int getPlayTime(){
        return sharedPref.getInt("PLAY_TIME", 10000);
    }
    public static void setPlayTime(int playTime){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("PLAY_TIME", playTime);
        editor.apply();
    }
    public static void setSound(Uri soundUri){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("SOUND_URI", soundUri.toString());
        editor.apply();
    }
    public static void setSound(Uri soundUri, String title){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("SOUND_URI", soundUri.toString());
        editor.putString("SOUND_TITLE", title);
        editor.apply();
    }
    public static Uri getSound(){
        return Uri.parse(sharedPref.getString("SOUND_URI", ""));
    }
}
