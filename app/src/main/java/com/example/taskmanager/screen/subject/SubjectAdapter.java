package com.example.taskmanager.screen.subject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.R;
import com.example.taskmanager.model.Subject;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private onClickSubjectInterfaces interfaces;
    private List<Subject> subjectList;

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.subjectName.setText(subject.getName());
        holder.subjectCode.setText(subject.getCode());

        holder.btnDeleteSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaces.onDeleteClick(subject);
            }
        });

        holder.btnEditSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaces.onEditClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, subjectCode;
        Button btnEditSubject, btnDeleteSubject;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            subjectCode = itemView.findViewById(R.id.subjectCode);
            btnEditSubject = itemView.findViewById(R.id.btnEditSubject);
            btnDeleteSubject = itemView.findViewById(R.id.btnDeleteSubject);
        }
    }

    public void setInterfaces(onClickSubjectInterfaces interfaces) {
        this.interfaces = interfaces;
    }

    public interface onClickSubjectInterfaces{
        void onDeleteClick(Subject subject);
        void onEditClick(Subject subject);
    }
}
