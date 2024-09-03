package com.example.ourtodoist.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtodoist.AC;
import com.example.ourtodoist.MainActivity;
import com.example.ourtodoist.R;
import com.example.ourtodoist.androidobject.AndroidSound;

public class ChooseSoundActivity extends AppCompatActivity {
    SoundAdapter soundAdapter;
    private RecyclerView soundRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sound);
        soundRecyclerView = findViewById(R.id.soundRecyclerView);
        soundRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        soundAdapter = new SoundAdapter(this, AndroidSound.listAndroidSound(this));
        soundRecyclerView.setAdapter(soundAdapter);
    }
    @Override
    protected void onResume(){
        super.onResume();
        soundAdapter = new SoundAdapter(this, AndroidSound.listAndroidSound(this));
        soundRecyclerView.setAdapter(soundAdapter);
    }
    public void saveBtnClick(View view){
        AndroidSound sound = soundAdapter.getSelectedSound();
        if(sound == null){
            Toast.makeText(this, "No sound selected yet", Toast.LENGTH_SHORT).show();
            return;
        }
        else sound.stopSound();
        Intent intent = new Intent(ChooseSoundActivity.this, MainActivity.class);
        intent.putExtra("uri", sound.getUri());
        setResult(AC.SOUND_ACT_SAVE, intent);
        finish();
    }
    public void cancelBtnClick(View view){
        AndroidSound sound = soundAdapter.getSelectedSound();
        if(sound != null)
            sound.stopSound();
        setResult(AC.SOUND_ACT_CANCEL, null);
        finish();
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
    }
}
