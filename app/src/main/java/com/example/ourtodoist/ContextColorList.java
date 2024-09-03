package com.example.ourtodoist;

import android.app.Activity;
import android.content.res.ColorStateList;

import androidx.core.content.ContextCompat;

public class ContextColorList {
    private static ColorStateList DIM_GRAY;
    private static ColorStateList GREEN;
    public static void initialize(Activity context){
        DIM_GRAY = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dim_gray));
        GREEN = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green));
    }
    public static ColorStateList getDimGray(){
        return DIM_GRAY;
    }
    public static ColorStateList getGreen() {
        return GREEN;
    }
}
