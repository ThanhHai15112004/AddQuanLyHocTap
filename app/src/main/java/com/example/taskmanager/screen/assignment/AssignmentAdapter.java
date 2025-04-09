package com.example.taskmanager.screen.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Assignment;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {
    private List<Assignment> assignmentList;
    Context context;

    public AssignmentAdapter(List<Assignment> assignmentList, Context context) {
        this.assignmentList = assignmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.txtTitle.setText(assignment.getTitle());
        holder.txtDescription.setText(assignment.getDescription());
        holder.txtDueDate.setText("Hạn: " + assignment.getDueDate());
        holder.txtPriority.setText("Ưu tiên: " + assignment.getPriorityLevel());
        holder.txtStatus.setText("Trạng thái: " + assignment.getStatus());
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtDueDate, txtPriority, txtStatus;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtDescription = itemView.findViewById(R.id.tvDescription);
            txtDueDate = itemView.findViewById(R.id.tvDueDate);
            txtPriority = itemView.findViewById(R.id.tvPriority);
            txtStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

