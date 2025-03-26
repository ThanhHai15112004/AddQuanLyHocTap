package com.example.hoctap.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.activities.EditAssignmentActivity;
import com.example.hoctap.models.Assignment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
    private Context context;
    private List<Assignment> assignments;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;

    public AssignmentAdapter(Context context) {
        this.context = context;
        this.assignments = new ArrayList<>();
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);

        holder.tvTitle.setText(assignment.getTitle());
        holder.tvSubject.setText(assignment.getSubjectName());

        // Format deadline date
        try {
            Date deadlineDate = inputFormat.parse(assignment.getDeadline());
            if (deadlineDate != null) {
                holder.tvDeadline.setText("Due: " + outputFormat.format(deadlineDate));
            } else {
                holder.tvDeadline.setText("Due: " + assignment.getDeadline());
            }
        } catch (ParseException e) {
            holder.tvDeadline.setText("Due: " + assignment.getDeadline());
        }

        // Set priority text and color
        holder.tvPriority.setText(assignment.getPriorityText());
        int priorityColor;
        switch (assignment.getPriority()) {
            case Assignment.PRIORITY_LOW:
                priorityColor = context.getResources().getColor(R.color.priority_low);
                break;
            case Assignment.PRIORITY_HIGH:
                priorityColor = context.getResources().getColor(R.color.priority_high);
                break;
            default:
                priorityColor = context.getResources().getColor(R.color.priority_medium);
        }
        holder.tvPriority.setTextColor(priorityColor);
        holder.priorityIndicator.setBackgroundColor(priorityColor);

        // Set status text and color
        holder.tvStatus.setText(assignment.getStatusText());
        int statusColor;
        switch (assignment.getStatus()) {
            case Assignment.STATUS_PENDING:
                statusColor = context.getResources().getColor(R.color.status_pending);
                break;
            case Assignment.STATUS_IN_PROGRESS:
                statusColor = context.getResources().getColor(R.color.status_in_progress);
                break;
            case Assignment.STATUS_COMPLETED:
                statusColor = context.getResources().getColor(R.color.status_completed);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.gray);
        }
        holder.tvStatus.setTextColor(statusColor);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditAssignmentActivity.class);
            intent.putExtra("assignment_id", assignment.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubject, tvDeadline, tvPriority, tvStatus;
        View priorityIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
        }
    }
}

