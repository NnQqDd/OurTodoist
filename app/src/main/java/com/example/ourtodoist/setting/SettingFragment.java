package com.example.ourtodoist.setting;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtodoist.AC;
import com.example.ourtodoist.R;
import com.example.ourtodoist.androidobject.ReminderBroadcast;
import com.example.ourtodoist.javaobject.Setting;
import com.example.ourtodoist.androidobject.SharedSetting;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class SettingFragment extends Fragment {
    private ListView settingListView;
    private ImageView avatarImgView;
    private TextView usernameTxt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }
    private void getReadMediaFilePermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
            }
        }
        else
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //getReadMediaFilePermission();
        ArrayList<Setting> settingData = new ArrayList<>();
        settingData.add(new Setting("Change avatar", "", R.drawable.user));
        settingData.add(new Setting("Change username", "", R.drawable.uppercase));
        settingData.add(new Setting("Set task conservation", SharedSetting.getMaxConDay() + " day(s)", R.drawable.conservation));
        settingData.add(new Setting("Choose ringtone", "", R.drawable.ringtone));
        settingData.add(new Setting("Set play length", SharedSetting.getPlayTime() + " millisecond(s)", R.drawable.clock));
        settingData.add(new Setting("Notify test", "", R.drawable.speaker));
        String[] chooseRingtoneOptions = {"Android ringtones", "Android files", "Sound list"};
        SettingAdapter adapter = new SettingAdapter(getActivity(), settingData);
        avatarImgView = getView().findViewById(R.id.avatarImgView3);
        Bitmap bitmap = SharedSetting.getAvatar();
        if(bitmap != null){
            avatarImgView.setImageBitmap(bitmap);
        }
        else avatarImgView.setImageResource(R.drawable.user);
        usernameTxt = getView().findViewById(R.id.usernameTxt);
        usernameTxt.setText(SharedSetting.getUsername());
        settingListView = getView().findViewById(R.id.settingListView);
        settingListView.setAdapter(adapter);
        settingListView.setOnItemClickListener((parent, view1, position, id) -> {
            if(position == 0){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, AC.PICTURE_REQUEST);
            }
            if(position == 1){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.fragment_dialog_username, null))
                        .setPositiveButton("Save", (dialog, id1) -> {
                            EditText txt = ((AlertDialog) dialog).findViewById(R.id.usernameEditText);
                            String str = txt.getText().toString();
                            if(str.trim().equals(""))
                                Toast.makeText(getActivity(), "Username can not be empty", Toast.LENGTH_SHORT).show();
                            else {
                                SharedSetting.setUsername(str);
                                usernameTxt.setText(str);
                                Intent broadcastIntent = new Intent(AC.CHANGE_USERNAME_BROADCAST);
                                getActivity().sendBroadcast(broadcastIntent);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, id12) -> {});
                builder.show();
            }
            if(position == 2){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.fragment_dialog_number, null))
                        .setPositiveButton("Save", (dialog, id1) -> {
                            EditText txt = ((AlertDialog) dialog).findViewById(R.id.numberEditText);
                            String str = txt.getText().toString();
                            if(str.trim().equals(""))
                                Toast.makeText(getActivity(), "Task conservation can not be empty", Toast.LENGTH_SHORT).show();
                            else {
                                int day;
                                try{
                                    day = Integer.parseInt(str);
                                }
                                catch(Exception ex) {
                                    Toast.makeText(getActivity(), "Invalid number", Toast.LENGTH_SHORT).show();
                                    day = 7;
                                }
                                settingData.get(position).setValue(str + " day(s)");
                                adapter.notifyDataSetChanged();
                                SharedSetting.setMaxConDay(day);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, id12) -> {});
                builder.show();
            }
            if(position == 3){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose from");
                builder.setItems(chooseRingtoneOptions, (dialog, which) -> {
                    if(which == 0){
                        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose ringtone");
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                        getActivity().startActivityForResult(intent, AC.RINGTONE_PICK_REQUEST);
                    }
                    else if(which == 1){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        getActivity().startActivityForResult(intent, AC.SOUND_CHOOSE_REQUEST);
                    }
                    else if(which == 2){
                        Intent intent = new Intent(getActivity(), ChooseSoundActivity.class);
                        getActivity().startActivityForResult(intent, AC.SOUND_CHOOSE_REQUEST);
                        getReadMediaFilePermission();
                    }
                });
                builder.show();
            }
            if(position == 4){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.fragment_dialog_number, null))
                        .setPositiveButton("Save", (dialog, id1) -> {
                            EditText txt = ((AlertDialog) dialog).findViewById(R.id.numberEditText);
                            String str = txt.getText().toString();
                            if(str.trim().equals(""))
                                Toast.makeText(getActivity(), "Play length can not be empty", Toast.LENGTH_SHORT).show();
                            else {
                                int length;
                                try{
                                    length = Integer.parseInt(str);
                                }
                                catch(Exception ex){
                                    Toast.makeText(getActivity(), "Invalid number", Toast.LENGTH_SHORT).show();
                                    length = 10000;
                                }
                                if(length < 2000){
                                    Toast.makeText(getActivity(), "Play length too short", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    settingData.get(position).setValue(str + " milisecond(s)");
                                    adapter.notifyDataSetChanged();
                                    SharedSetting.setPlayTime(length);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, id12) -> {});
                builder.show();
            }
            if(position == 5){
                if(checkAndGiveNotificationPermissions()){
                    Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show();
                    ReminderBroadcast reminderBroadcast = new ReminderBroadcast();
                    reminderBroadcast.initialize("OurTodoistExample", "OurTodoist", "Reminder by OurTodoist.",AC.WHATEVER);
                    reminderBroadcast.setTimeDisplay(LocalDateTime.now());
                    reminderBroadcast.createReminderBroadcast(requireContext());
                }
            }
        });
    }

    private boolean checkAndGiveNotificationPermissions() {
        if (NotificationManagerCompat.from(getActivity()).areNotificationsEnabled())
            return true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Turn on notification")
                .setMessage("Notification are turned off, do you want to turn it on now?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                    startActivity(intent);
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                })
                .setNegativeButton("No", (dialog, which) -> {
                })
                .setCancelable(false)
                .show();
        return false;
    }
    /*public void createBroadcast(){
        Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show();
        ReminderBroadcast reminderBroadcast1 = new ReminderBroadcast();
        reminderBroadcast1.Initialize("111", "OurTodoist", "BC 1.", 111);
        reminderBroadcast1.SetReturnActivity(MainActivity.class, "", "");
        reminderBroadcast1.SetTimeDisplay(LocalDateTime.now().plusSeconds(15));
        reminderBroadcast1.createReminderBroadcast(getActivity());


        Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show();
        ReminderBroadcast reminderBroadcast2 = new ReminderBroadcast();
        reminderBroadcast2.Initialize("222", "OurTodoist", "BC 2.", 222);
        reminderBroadcast2.SetReturnActivity(MainActivity.class, "", "");
        reminderBroadcast2.SetTimeDisplay(LocalDateTime.now().plusSeconds(8));
        reminderBroadcast2.createReminderBroadcast(getActivity());


        Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show();
        ReminderBroadcast reminderBroadcast3 = new ReminderBroadcast();
        reminderBroadcast3.Initialize("333", "OurTodoist", "BC 3.", 333);
        reminderBroadcast3.SetReturnActivity(MainActivity.class, "", "");
        reminderBroadcast3.SetTimeDisplay(LocalDateTime.now().plusSeconds(5));
        reminderBroadcast3.createReminderBroadcast(getActivity());

        ReminderBroadcast.cancelNotificationChannel(getActivity(), "222", 222);
    }*/
    public void setAvatar(Bitmap bitmap){
        avatarImgView.setImageBitmap(bitmap);
    }
}