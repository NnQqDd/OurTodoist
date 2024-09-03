package com.example.ourtodoist.task;

import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.example.ourtodoist.AC;
import com.example.ourtodoist.MainActivity;
import com.example.ourtodoist.R;
import com.example.ourtodoist.javaobject.DateUtility;
import com.example.ourtodoist.javaobject.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    private EditText nameEditTxt;
    private TextView clockTimeTxt;
    private TextView dateTxt;
    private EditText noteEditTxt;
    ArrayList<ImageButton> imageBtnLst;
    private int taskId;
    private int taskStatus;
    private void removeUnnecessaryItem(Menu menu){
        for (int i = menu.size() - 1; i >= 0; i--) {
            MenuItem menuItem = menu.getItem(i);
            String itemTitle = menuItem.getTitle().toString();
            if(itemTitle.equals("Translate") || itemTitle.equals("Share") || itemTitle.equals("Message") || itemTitle.equals("Call") || itemTitle.equals("Autofill"))
                menu.removeItem(menuItem.getItemId());
        }
    }
    private void toggleImageBtnVis(){
        for(ImageButton btn: imageBtnLst){
            if(btn.getVisibility() == View.VISIBLE)
                btn.setVisibility(View.GONE);
            else btn.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        nameEditTxt = findViewById(R.id.taskNameEditTxt);
        clockTimeTxt = findViewById(R.id.clockTimeTxt);
        dateTxt = findViewById(R.id.dateTxt);
        noteEditTxt = findViewById(R.id.noteEditTxt);
        imageBtnLst = new ArrayList<>();
        imageBtnLst.add(findViewById(R.id.boldBtn));
        imageBtnLst.add(findViewById(R.id.italicBtn));
        imageBtnLst.add(findViewById(R.id.underlineBtn));
        imageBtnLst.add(findViewById(R.id.uppercaseBtn));
        toggleImageBtnVis();
        noteEditTxt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                removeUnnecessaryItem(menu);
                return false;}
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {return false;}
            public void onDestroyActionMode(ActionMode mode) {}
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                removeUnnecessaryItem(menu);
                return true;
            }
        });
        noteEditTxt.setOnFocusChangeListener((v, hasFocus) -> {
            toggleImageBtnVis();
        });
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            Bundle bundle = intent.getExtras();
            taskId = bundle.getInt("id");
            taskStatus = bundle.getInt("status");
            String name = bundle.getString("name");
            String clockTime = bundle.getString("clocktime");
            String day = bundle.getString("day");
            String note = bundle.getString("note");
            nameEditTxt.setText(name);
            clockTimeTxt.setText(clockTime);
            dateTxt.setText(day);
            noteEditTxt.setText(HtmlCompat.fromHtml(note, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        else{
            Date date = new Date();
            dateTxt.setText(DateUtility.getMDYString(date));
            clockTimeTxt.setText(DateUtility.getClockTimeString(date));
            taskId = -1;
            taskStatus = Task.NOISY;
        }
    }

    public void pickClockTime(View view) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(this,
                (view1, hour1, minute1) -> {
                    clockTimeTxt.setText(String.format("%d:%02d",hour1, minute1));
                }, hour, minute, false);
        picker.show();
    }
    public void pickDateTime(View view){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(this,
                (view1, year1, month1, day1)->{
                    dateTxt.setText((month1+1)+"/"+day1+"/"+year1);
                }, year, month, day);
        picker.show();
    }
    public void makingBoldText(View view){
        int startSelection = noteEditTxt.getSelectionStart();
        int endSelection = noteEditTxt.getSelectionEnd();
        if (startSelection != -1 && endSelection != -1) {
            Spannable spannable = noteEditTxt.getText();
            StyleSpan[] styleSpans = spannable.getSpans(startSelection, endSelection, StyleSpan.class);
            boolean isSelectionBold = false;
            for (StyleSpan styleSpan : styleSpans) {
                if (styleSpan.getStyle() == Typeface.BOLD) {
                    isSelectionBold = true;
                    break;
                }
            }
            if (!isSelectionBold) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                for (StyleSpan styleSpan : styleSpans) {
                    if (styleSpan.getStyle() == Typeface.BOLD) {
                        spannable.removeSpan(styleSpan);
                    }
                }
            }
            noteEditTxt.setText(spannable);
            noteEditTxt.setSelection(startSelection, endSelection);
        }
    }
    public void makingItalicText(View view){
        int startSelection = noteEditTxt.getSelectionStart();
        int endSelection = noteEditTxt.getSelectionEnd();
        if (startSelection != -1 && endSelection != -1) {
            Spannable spannable = noteEditTxt.getText();
            StyleSpan[] styleSpans = spannable.getSpans(startSelection, endSelection, StyleSpan.class);
            boolean isSelectionItalic = false;
            for (StyleSpan styleSpan : styleSpans) {
                if (styleSpan.getStyle() == Typeface.ITALIC) {
                    isSelectionItalic = true;
                    break;
                }
            }
            if (!isSelectionItalic) {
                spannable.setSpan(new StyleSpan(Typeface.ITALIC), startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                for (StyleSpan styleSpan : styleSpans) {
                    if (styleSpan.getStyle() == Typeface.ITALIC) {
                        spannable.removeSpan(styleSpan);
                    }
                }
            }
            noteEditTxt.setText(spannable);
            noteEditTxt.setSelection(startSelection, endSelection);
        }
    }
    public void makingUnderlineText(View view){
        int startSelection = noteEditTxt.getSelectionStart();
        int endSelection = noteEditTxt.getSelectionEnd();
        if (startSelection != -1 && endSelection != -1) {
            Spannable spannable = noteEditTxt.getText();
            UnderlineSpan[] underlineSpans = spannable.getSpans(startSelection, endSelection, UnderlineSpan.class);
            boolean isSelectionUnderlined = underlineSpans.length > 0;
            if (!isSelectionUnderlined) {
                spannable.setSpan(new UnderlineSpan(), startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                for (UnderlineSpan underlineSpan : underlineSpans) {
                    spannable.removeSpan(underlineSpan);
                }
            }
            noteEditTxt.setText(spannable);
            noteEditTxt.setSelection(startSelection, endSelection);
        }
    }
    public void makingUppercase(View view){
        int startSelection = noteEditTxt.getSelectionStart();
        int endSelection = noteEditTxt.getSelectionEnd();
        if (startSelection != -1 && endSelection != -1) {
            Editable editableText = noteEditTxt.getText();
            String selectedText = editableText.toString().substring(startSelection, endSelection);
            String upperCaseText = selectedText.toUpperCase();
            if(selectedText.equals(upperCaseText)){
                editableText.replace(startSelection, endSelection, selectedText.toLowerCase());
            }
            else editableText.replace(startSelection, endSelection, upperCaseText);
            noteEditTxt.setSelection(startSelection, endSelection);
        }
    }
    public void saveBtnClick(View view){
        String name = nameEditTxt.getText().toString();
        String clockTime = clockTimeTxt.getText().toString();
        String date = dateTxt.getText().toString();
        String note = HtmlCompat.toHtml(noteEditTxt.getText(), HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        if(name.trim().equals("") || clockTime.trim().equals("") || clockTime.trim().equals("")){
            Toast.makeText(this, "Please fill all task information.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(taskId == -1 && DateUtility.getDate(date, clockTime).compareTo(new Date()) <= 0){
            Toast.makeText(this, "Please choose the date at the near future.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
        intent.putExtra("id", taskId);
        intent.putExtra("status", taskStatus);
        intent.putExtra("name", name);
        intent.putExtra("clocktime", clockTime);
        intent.putExtra("date", date);
        intent.putExtra("note", note);
        setResult(AC.TASK_ACT_SAVE, intent);
        finish();
    }
    public void backBtnClick(View view){
        setResult(AC.TASK_ACT_CANCEL, null);
        finish();
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
    }
}
