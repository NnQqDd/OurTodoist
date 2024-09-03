package com.example.ourtodoist.androidobject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AndroidSound {
    private String title;
    private String author;
    private Uri uri;
    private MediaPlayer player;
    private static String getSoundName(String str){
        String[] parts = str.split("/");
        return (parts.length > 0)?parts[parts.length - 1]:"";
    }
    public AndroidSound(String uriString) {
        this.title = getSoundName(uriString);
        this.author = "";
        this.uri = Uri.parse(uriString);
    }
    public AndroidSound(String title, String author, String uriString) {
        this.title = title;
        this.author = author;
        this.uri = Uri.parse(uriString);
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor(){
        return author;
    }
    public Uri getUri(){
        return this.uri;
    }
    public void playSound(Context context, int length) {
        player = new MediaPlayer();
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try{
            player.setDataSource(context, this.uri);
            player.prepare();
        }
        catch(Exception ex){
            Log.v("ANDROID SOUND", "CAN'T PLAY ANDROID SOUND: " + ex.getMessage());
            return;
        }
        player.start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    player.stop();
                }
            }, length);
    }
    public void stopSound(){
        if(player != null)
            player.stop();
    }
    public static  ArrayList<AndroidSound> listAndroidSound(Context context){
        ArrayList<AndroidSound> androidSounds = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songUriIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String currentUriStr = songCursor.getString(songUriIndex);
                String currentTitle = songCursor.getString(songTitle);
                if(currentTitle == null || currentTitle.length() == 0)
                    currentTitle = getSoundName(currentUriStr);
                String currentArtist = songCursor.getString(songArtist);
                androidSounds.add(new AndroidSound(currentTitle, currentArtist, currentUriStr));
            }while(songCursor.moveToNext());
        }
        return androidSounds;
    }
}
