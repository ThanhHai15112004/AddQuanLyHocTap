package com.example.taskmanager.screen.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Schedule;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private onLongClickInterfaces interfaces;

    private List<Schedule> scheduleList;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void updateData(List<Schedule> newList) {
        scheduleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.title.setText(schedule.getTitle());
        holder.time.setText(schedule.getStartTime() + " - " + schedule.getEndTime());
        holder.location.setText(schedule.getLocation());
        holder.lnItemview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interfaces.onLongClick(schedule);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView title, time, location;
        LinearLayout lnItemview;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            time = itemView.findViewById(R.id.textTime);
            location = itemView.findViewById(R.id.textLocation);
            lnItemview = itemView.findViewById(R.id.item_view);
        }
    }

    public interface onLongClickInterfaces{
        void onLongClick(Schedule schedule);
    }

    public void setInterfaces(onLongClickInterfaces interfaces) {
        this.interfaces = interfaces;
    }

}

