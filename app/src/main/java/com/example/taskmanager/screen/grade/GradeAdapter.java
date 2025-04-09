package com.example.taskmanager.screen.grade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Grade;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.screen.DatabaseHelper;

import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {
    Context context;
    private List<Subject> subjectList;

    public GradeAdapter(List<Subject> subjectList, Context context) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        List<Grade> gradeList = new DatabaseHelper(context).getGradeListBySubjectId(subject.getCode());

        holder.tvSubjectName.setText(subject.getName());

        holder.scoreColumns.removeAllViews();

        for (Grade grade1 : gradeList) {
            TextView scoreTextView = new TextView(holder.itemView.getContext());
            scoreTextView.setText(String.format("%05.2f", grade1.getScore()));
            scoreTextView.setPadding(10, 0, 10, 0); // Thêm padding nếu cần
            holder.scoreColumns.addView(scoreTextView);
        }

        double average = calculateAverage(gradeList);
        holder.tvAverageScore.setText(String.format("Average: %05.2f", average));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName;
        LinearLayout scoreColumns;
        TextView tvAverageScore;

        public GradeViewHolder(View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            scoreColumns = itemView.findViewById(R.id.scoreColumns);
            tvAverageScore = itemView.findViewById(R.id.tvAverageScore);
        }
    }

    public double calculateAverage(List<Grade> listGrades) {
        if (listGrades.isEmpty()) return 0;
        double total = 0;
        int sumCount = 0;
        for (Grade grade : listGrades) {
            total+= grade.getScore();
            sumCount+= Integer.parseInt(grade.getGradeType());
        }
        return total / sumCount;
    }
}
