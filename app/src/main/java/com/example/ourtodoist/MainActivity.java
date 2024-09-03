package com.example.ourtodoist;

import com.example.ourtodoist.androidobject.DBWrapper;
import com.example.ourtodoist.androidobject.ReminderManager;
import com.example.ourtodoist.androidobject.SharedSetting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.SearchManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ourtodoist.javaobject.DateUtility;
import com.example.ourtodoist.javaobject.Task;
import com.example.ourtodoist.search.SearchFragment;
import com.example.ourtodoist.setting.SettingFragment;
import com.example.ourtodoist.task.TaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Fragment activeFragment = null;
    private TaskFragment taskFragment = null;
    private SearchFragment searchFragment = null;
    private SettingFragment settingFragment = null;
    private BroadcastReceiver changeUsernameBroadcast = null;
    private BroadcastReceiver quietTaskBroadcast = null;
    private FragmentManager fragmentManager = null;

    private void createAndRegisterBroadcast() {
        IntentFilter intentFilter = null;
        if (changeUsernameBroadcast == null) changeUsernameBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                taskFragment.setUsername(SharedSetting.getUsername());
            }
        };
        intentFilter = new IntentFilter(AC.CHANGE_USERNAME_BROADCAST);
        ContextCompat.registerReceiver(this, changeUsernameBroadcast, intentFilter, ContextCompat.RECEIVER_EXPORTED);
        if (quietTaskBroadcast == null) quietTaskBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.v("BROADCAST", "BROADCAST");
                int taskId = intent.getIntExtra("requestCode", -1);
                DBWrapper.quietTaskById(taskId);
                taskFragment.notifyListChanged();
                searchFragment.notifyListChanged();
            }
        };
        intentFilter = new IntentFilter(AC.QUIET_TASK_BROADCAST);
        ContextCompat.registerReceiver(this, quietTaskBroadcast, intentFilter, ContextCompat.RECEIVER_EXPORTED);
    }

    private void initializeUI() {
        fragmentManager = getSupportFragmentManager();
        taskFragment = new TaskFragment();
        searchFragment = new SearchFragment();
        settingFragment = new SettingFragment();
        fragmentManager.beginTransaction().add(R.id.frameLayout, settingFragment).commit();
        fragmentManager.beginTransaction().hide(settingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout, searchFragment).commit();
        fragmentManager.beginTransaction().hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout, taskFragment).commit();
        activeFragment = taskFragment;
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.navTasks) {
                taskFragment.notifyListChanged();
                fragmentManager.beginTransaction().hide(activeFragment).show(taskFragment).commit();
                activeFragment = taskFragment;
                return true;
            } else if (itemId == R.id.navHistory) {
                searchFragment.clearSearchList();
                fragmentManager.beginTransaction().hide(activeFragment).show(searchFragment).commit();
                activeFragment = searchFragment;
                return true;
            } else if (itemId == R.id.navSetting) {
                fragmentManager.beginTransaction().hide(activeFragment).show(settingFragment).commit();
                activeFragment = settingFragment;
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedSetting.initialize(this);
        Toast.makeText(this, "Hello, " + SharedSetting.getUsername() + "!", Toast.LENGTH_SHORT).show();
        DBWrapper.initialize(this);
        DBWrapper.updateTasks();
        for (Task task : DBWrapper.taskArrayList) {
            if (task.getStatus() != Task.NOISY) continue;
            ReminderManager.createBroadcast(this, task);
        }
        ContextColorList.initialize(this);
        initializeUI();
        createAndRegisterBroadcast();
        //FirebaseDatabase database = FirebaseDatabase.getInstance("https://ourtodoist-default-rtdb.firebaseio.com/");
        //DatabaseReference myRef = database.getReference("quangduy162003");
        //myRef.setValue("I write");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            unregisterReceiver(changeUsernameBroadcast);
            unregisterReceiver(quietTaskBroadcast);
        }
        catch(Exception ignored){}
    }
    @Override
    protected void onPause(){
        super.onPause();
        fragmentManager.beginTransaction().hide(activeFragment).commit();
        try{
            unregisterReceiver(changeUsernameBroadcast);
            unregisterReceiver(quietTaskBroadcast);
        }
        catch(Exception ignored){}
    }
    @Override
    protected void onResume() {
        super.onResume();
        DBWrapper.updateTasks();
        taskFragment.notifyListChanged();
        searchFragment.notifyListChanged();
        if(fragmentManager != null && activeFragment != null) {
            fragmentManager.beginTransaction().show(activeFragment).commit();
        }
        createAndRegisterBroadcast();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFragment.setSearchQuery(query);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AC.PICTURE_REQUEST) {
            if (data == null || data.getData() == null) return;
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(data.getData());
            } catch (Exception ignored) {
            }
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            try {
                imageStream.close();
            } catch (Exception ignored) {
            }
            if (bitmap != null) {
                taskFragment.setAvatar(bitmap);
                searchFragment.setAvatar(bitmap);
                settingFragment.setAvatar(bitmap);
                SharedSetting.setAvatar(bitmap);
            }
        } else if (requestCode == AC.TASK_ACT_ADD && resultCode == AC.TASK_ACT_SAVE) {
            int id = SharedSetting.getAndIncId();
            String name = data.getStringExtra("name");
            String day = data.getStringExtra("date");
            String clockTime = data.getStringExtra("clocktime");
            String note = data.getStringExtra("note");
            Task task = new Task(id, name, Task.NOISY, clockTime, day, note);
            ReminderManager.createBroadcast(this, task);
            DBWrapper.insertTask(task);
            taskFragment.notifyListChanged();
            searchFragment.notifyListChanged();
        }
        else if(requestCode == AC.TASK_ACT_EDIT && resultCode == AC.TASK_ACT_SAVE) {
            int id = data.getIntExtra("id", -1);
            int status = data.getIntExtra("status", Task.QUIET);
            String name = data.getStringExtra("name");
            String day = data.getStringExtra("date");
            String clockTime = data.getStringExtra("clocktime");
            String note = data.getStringExtra("note");
            Task task = new Task(id, name, status, clockTime, day, note);
            if (status == Task.NOISY && DateUtility.getDate(task.getDay(), task.getClockTime()).compareTo(new Date()) > 0){
                ReminderManager.createBroadcast(this, task);
            }
            DBWrapper.updateTask(task);
            taskFragment.notifyListChanged();
            searchFragment.notifyListChanged();
        }
        else if (requestCode == AC.RINGTONE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null) return;
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri == null){
                Toast.makeText(this, "Something wrong happened.", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedSetting.setSound(uri);
            Toast.makeText(this, "Ringtone selected!", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == AC.SOUND_CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null) return;
            Uri uri = data.getData();
            if(uri == null) {
                Toast.makeText(this, "Something wrong happened.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Ringtone selected!", Toast.LENGTH_SHORT).show();
            SharedSetting.setSound(uri);
        }
        else if (requestCode == AC.SOUND_CHOOSE_REQUEST && resultCode == AC.SOUND_ACT_SAVE) {
            if(data == null) return;
            Uri uri = data.getParcelableExtra("uri");
            if(uri == null) {
                Toast.makeText(this, "Something wrong happened.", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedSetting.setSound(uri);
            Toast.makeText(this, "Ringtone selected!", Toast.LENGTH_SHORT).show();
        }
    }
}