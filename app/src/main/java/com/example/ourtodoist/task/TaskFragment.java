package com.example.ourtodoist.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtodoist.AC;
import com.example.ourtodoist.R;
import com.example.ourtodoist.androidobject.DBWrapper;
import com.example.ourtodoist.androidobject.ReminderManager;
import com.example.ourtodoist.javaobject.DateUtility;
import com.example.ourtodoist.javaobject.Task;
import com.example.ourtodoist.androidobject.SharedSetting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class TaskFragment extends Fragment {
    private RecyclerView taskRecyclerView;
    private ImageView avatarImgView;
    private TextView usernameTxt;
    private TextView taskInformTxt;
    public TaskFragment(){
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView todayTxt = getView().findViewById(R.id.todayTxt);
        todayTxt.setText(DateUtility.getTodayString());
        taskInformTxt = getView().findViewById(R.id.taskInformTxt);
        usernameTxt = getView().findViewById(R.id.usernameTxt1);
        usernameTxt.setText(SharedSetting.getUsername());
        avatarImgView = getView().findViewById(R.id.avatarImgView1);
        Bitmap bitmap = SharedSetting.getAvatar();
        if(bitmap != null){
            avatarImgView.setImageBitmap(bitmap);
        }
        else avatarImgView.setImageResource(R.drawable.user);
        taskRecyclerView = getView().findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        TaskAdapter adapter = new TaskAdapter(
                DBWrapper.taskArrayList,
                task -> {
                    Intent intent = new Intent(getContext(), AddTaskActivity.class);
                    intent.putExtra("id", task.getId());
                    intent.putExtra("status", task.getStatus());
                    intent.putExtra("name", task.getName());
                    intent.putExtra("clocktime", task.getClockTime());
                    intent.putExtra("day", task.getDay());
                    intent.putExtra("note", task.getNote());
                    getActivity().startActivityForResult(intent, AC.TASK_ACT_EDIT);
                });
        //registerForContextMenu(taskRecyclerView);
        taskRecyclerView.setAdapter(adapter);
        FloatingActionButton addTaskBtn = getView().findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            getActivity().startActivityForResult(intent, AC.TASK_ACT_ADD);
        });
        if(DBWrapper.taskArrayList.size() != 0){
            taskInformTxt.setText("");
        }
        else{
            taskInformTxt.setText("It is empty here.");
        }
    }
    public void setUsername(String username){
        usernameTxt.setText(username);
    }
    public void notifyListChanged(){
        if(DBWrapper.taskArrayList.size() != 0){
            taskInformTxt.setText("");
        }
        else{
            taskInformTxt.setText("It is empty here.");
        }
        taskRecyclerView.getAdapter().notifyDataSetChanged();
    }
    public void setAvatar(Bitmap bitmap){
        avatarImgView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Task selectedTask = ((TaskAdapter)taskRecyclerView.getAdapter()).getSelectedTask();
        if(id == R.id.editMenuItem){
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            intent.putExtra("id", selectedTask.getId());
            intent.putExtra("name", selectedTask.getName());
            intent.putExtra("clocktime", selectedTask.getClockTime());
            intent.putExtra("day", selectedTask.getDay());
            intent.putExtra("note", selectedTask.getNote());
            getActivity().startActivityForResult(intent, AC.TASK_ACT_EDIT);
        }
        else if(id == R.id.toggleMenuItem){
            boolean flag = selectedTask.toggleWithCheck();
            if(flag){
                if(selectedTask.getStatus() == Task.QUIET){
                    ReminderManager.cancelBroadcast(getActivity(), selectedTask);
                }
                else{
                    ReminderManager.createBroadcast(getActivity(), selectedTask);
                }
                DBWrapper.database.updateTask(selectedTask);
                taskRecyclerView.getAdapter().notifyDataSetChanged();
            }
            else Toast.makeText(getActivity(), "Can not toggle tasks in the past.", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.deleteMenuItem){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to delete task "+selectedTask.getName()+"?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                ReminderManager.cancelBroadcast(getActivity(), selectedTask);
                DBWrapper.deleteTask(selectedTask);
                taskRecyclerView.getAdapter().notifyDataSetChanged();
            });
            builder.setNegativeButton("No", (dialog, which) -> {});
            builder.create().show();
        }
        return super.onContextItemSelected(item);
    }
}
