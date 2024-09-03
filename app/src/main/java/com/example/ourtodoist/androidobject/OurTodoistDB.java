package com.example.ourtodoist.androidobject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.ourtodoist.javaobject.Task;

import java.util.ArrayList;

/* TASK
    private String name;
    private int status;
    private String clockTime;
    private String day;
    private String note;
 */

public class OurTodoistDB extends SQLiteOpenHelper {

    public OurTodoistDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists tbtask(id integer primary key, name text, status integer, clocktime text, day text, note text)";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists tbtask");
        onCreate(db);
    }
    public ArrayList<Task> getAllTask(){
        ArrayList<Task> list = new ArrayList<>();
        String sql = "select * from tbtask";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null)
            while (cursor.moveToNext())
            {
               Task task = new Task(
                       cursor.getInt(0), cursor.getString(1),
                       cursor.getInt(2), cursor.getString(3),
                       cursor.getString(4), cursor.getString(5)
                );
                list.add(task);
            }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }
    public void insertTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("id", task.getId());
        value.put("name", task.getName());
        value.put("status", task.getStatus());
        value.put("clocktime", task.getClockTime());
        value.put("day", task.getDay());
        value.put("note", task.getNote());
        db.insert("tbtask",null, value);
        db.close();
    }
    public void updateTask(Task task){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("name", task.getName());
        value.put("status", task.getStatus());
        value.put("clocktime", task.getClockTime());
        value.put("day", task.getDay());
        value.put("note", task.getNote());
        db.update("tbtask", value,"id=?",
                new String[]{Integer.toString(task.getId())});
        db.close();
    }
    public void deleteTask(Task task){
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("delete from tbtask where id=%d", task.getId()) ;
        db.execSQL(sql);
        db.close();
    }
}
