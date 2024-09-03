package com.example.ourtodoist.setting;

//import com.example.ourtodoist.object.DateUtility;

import com.example.ourtodoist.R;
import com.example.ourtodoist.androidobject.AndroidSound;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SoundAdapter extends RecyclerView.Adapter<SoundViewHolder> {
    private ArrayList<AndroidSound> soundData;
    private AndroidSound selectedSound;
    private Context context;
    public SoundAdapter(Context context, ArrayList<AndroidSound> soundData){
        this.soundData = soundData;
        this.context = context;
    }
    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basic, null, false);
        return new SoundViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        AndroidSound sound = soundData.get(position);
        holder.soundNameTxt.setText(sound.getTitle());
        holder.soundDescTxt.setText(sound.getAuthor());
        holder.itemView.setOnClickListener(view -> {
            if(selectedSound != null)
                selectedSound.stopSound();
            selectedSound = sound;
            selectedSound.playSound(context, 10000);
        });
    }
    @Override
    public int getItemCount() {
        if(soundData != null) return soundData.size();
        return 0;
    }
    public AndroidSound getSelectedSound(){
        return selectedSound;
    }
}

class SoundViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public TextView soundNameTxt;
    public TextView soundDescTxt;
    public SoundViewHolder(@NonNull View view) {
        super(view);
        soundNameTxt = view.findViewById(R.id.nameFieldTxt);
        soundDescTxt = view.findViewById(R.id.valueFieldTxt);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {}
}
