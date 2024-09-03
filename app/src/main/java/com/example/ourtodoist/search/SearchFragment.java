package com.example.ourtodoist.search;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ourtodoist.AC;
import com.example.ourtodoist.R;
import com.example.ourtodoist.androidobject.DBWrapper;
import com.example.ourtodoist.androidobject.ReminderManager;
import com.example.ourtodoist.javaobject.Task;
import com.example.ourtodoist.androidobject.SharedSetting;
import com.example.ourtodoist.task.AddTaskActivity;
import com.example.ourtodoist.task.TaskAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private ArrayList<Task> searchTaskData;
    private ImageButton calendarBtn;
    private ImageView avatarImgView;
    private RecyclerView searchRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        avatarImgView = getView().findViewById(R.id.avatarImgView2);
        Bitmap bitmap = SharedSetting.getAvatar();
        if(bitmap != null){
            avatarImgView.setImageBitmap(bitmap);
        }
        else avatarImgView.setImageResource(R.drawable.user);
        calendarBtn = getView().findViewById(R.id.calendarBtn);
        calendarBtn.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view1, year1, month1, day1)->{
                        searchView.setQuery((month1+1)+"/"+day1+"/"+year1, true);
                    }, year, month, day);
            picker.show();
        });
        searchRecyclerView = getView().findViewById(R.id.recyclerView);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        searchTaskData = new ArrayList<>();
        TaskAdapter adapter = new TaskAdapter(
                searchTaskData,
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
        searchRecyclerView.setAdapter(adapter);
        searchView = getView().findViewById(R.id.searchView);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAndUpdateList(query);
                return true;
            }
        });
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Task selectedTask = ((TaskAdapter)searchRecyclerView.getAdapter()).getSelectedTask();
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
                searchRecyclerView.getAdapter().notifyDataSetChanged();
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
                for(int i = 0; i < searchTaskData.size(); i++){
                    Task task = searchTaskData.get(i);
                    if(task.getId() == selectedTask.getId()){
                        searchTaskData.remove(i);
                        break;
                    }
                }
                searchRecyclerView.getAdapter().notifyDataSetChanged();
            });
            builder.setNegativeButton("No", (dialog, which) -> {});
            builder.create().show();
        }
        //Toast.makeText(getActivity(), "Resubmit search query to fetch the newest changes.", Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }
    public void setAvatar(Bitmap bitmap){
        avatarImgView.setImageBitmap(bitmap);
    }
    public void clearSearchList(){
        searchTaskData.clear();
        searchRecyclerView.getAdapter().notifyDataSetChanged();
    }
    public void searchAndUpdateList(String query){
        searchTaskData.clear();
        if(!query.trim().equals(""))
            searchTaskData.addAll(DBWrapper.search(query));
        Toast.makeText(getContext(), String.format("Found %d task(s)", searchTaskData.size()), Toast.LENGTH_SHORT).show();
        searchRecyclerView.getAdapter().notifyDataSetChanged();
    }
    public void setSearchQuery(String query){
        searchView.setQuery(query, false);
    }
    public void notifyListChanged(){
        searchRecyclerView.getAdapter().notifyDataSetChanged();
    }
}