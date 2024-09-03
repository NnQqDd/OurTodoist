package com.example.ourtodoist.task;

//import com.example.ourtodoist.object.DateUtility;

import com.example.ourtodoist.ContextColorList;
import com.example.ourtodoist.R;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtodoist.javaobject.Task;
import java.util.ArrayList;




public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private ArrayList<Task> taskData;
    private ClickTaskCard clicker;
    private Task selectedTask = null;
    public TaskAdapter(ArrayList<Task> taskData,  ClickTaskCard clicker){
        this.taskData = taskData;
        this.clicker = clicker;
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, null, false);
        return new TaskViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskData.get(position);
        holder.taskNameTxt.setText(task.getName());
        holder.dayStateTxt.setText(task.getDayState());
        holder.clockTimeTxt.setText(task.getClockTime());
        holder.dayTxt.setText(task.getDay());
        holder.statusImgView.invalidate();
        if(task.getStatus() == Task.QUIET)
            ImageViewCompat.setImageTintList(holder.statusImgView, ContextColorList.getDimGray());
        else ImageViewCompat.setImageTintList(holder.statusImgView, ContextColorList.getGreen());
        holder.taskCard.setOnClickListener(view -> {
            clicker.onClickTaskCard(task);
        });
        holder.taskCard.setOnLongClickListener(view -> {
            selectedTask = task;
            return false;
        });
    }
    @Override
    public int getItemCount() {
        if(taskData != null) return taskData.size();
        return 0;
    }
    public Task getSelectedTask(){
        return selectedTask;
    }
}

class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public TextView dayStateTxt, taskNameTxt, clockTimeTxt, dayTxt;
    public ImageView statusImgView;

    public CardView taskCard;
    public TaskViewHolder(@NonNull View view) {
        super(view);
        dayStateTxt = view.findViewById(R.id.dayStateTxt);
        taskNameTxt = view.findViewById(R.id.taskNameTxt);
        clockTimeTxt = view.findViewById(R.id.clockTimeTxt);
        dayTxt = view.findViewById(R.id.dayTxt);
        statusImgView = view.findViewById(R.id.statusImgView);
        taskCard = view.findViewById(R.id.taskCard);
        taskCard.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Taskcard menu");
        //view.findViewById()
        contextMenu.add(getAdapterPosition(), R.id.editMenuItem, 0, "Edit");
        contextMenu.add(getAdapterPosition(), R.id.toggleMenuItem, 1, "Toggle");
        contextMenu.add(getAdapterPosition(), R.id.deleteMenuItem, 2, "Delete");
    }
}
