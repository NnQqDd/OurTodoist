package com.example.ourtodoist.setting;

import com.example.ourtodoist.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.ourtodoist.javaobject.Setting;
import java.util.ArrayList;

public class SettingAdapter extends ArrayAdapter<Setting> {
    private Activity activity;
    private ArrayList<Setting> settingData;
    public SettingAdapter(Activity activity, ArrayList<Setting> settingData) {
        super(activity, R.layout.item_basic, settingData);
        this.activity = activity;
        this.settingData = settingData;
    }
    @Override
    public int getCount() {
        return settingData.size();
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view;
        boolean isOne = settingData.get(position).getValue().equals("");
        if(!isOne)
            view = inflater.inflate(R.layout.item_basic, null, true);
        else view = inflater.inflate(R.layout.item_one, null, true);
        ImageView imgView = view.findViewById(R.id.imgViewField);
        TextView settingNameTxt = view.findViewById(R.id.nameFieldTxt);
        TextView settingValueTxt = null;
        if(!isOne) settingValueTxt = view.findViewById(R.id.valueFieldTxt);
        settingNameTxt.setText(settingData.get(position).getName());
        if(!isOne) settingValueTxt.setText(settingData.get(position).getValue());
        imgView.setImageDrawable(ContextCompat.getDrawable(activity, settingData.get(position).getImageId()));
        return view;
    }
}
