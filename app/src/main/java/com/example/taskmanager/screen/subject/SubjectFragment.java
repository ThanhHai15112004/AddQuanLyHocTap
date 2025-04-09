package com.example.taskmanager.screen.subject;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.R;
import com.example.taskmanager.databinding.FragmentSubjectBinding;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.screen.DatabaseHelper;
import java.util.List;

public class SubjectFragment extends Fragment implements SubjectAdapter.onClickSubjectInterfaces {
    DatabaseHelper db ;
    List<Subject> listSubject;
    FragmentSubjectBinding binding;
    SubjectAdapter subjectAdapter;
    public SubjectFragment() {
    }
    public static SubjectFragment newInstance() {
        return new SubjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());
        listSubject = db.getSubjectsByUserId(PublicConstants.user.getId());

        subjectAdapter = new SubjectAdapter(listSubject);
        subjectAdapter.setInterfaces(this);
        binding.rvListSubject.setAdapter(subjectAdapter);
        binding.rvListSubject.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.btnAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubjectDialog();
            }
        });
    }

    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm Môn Học");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_subject, null);
        builder.setView(view);

        EditText nameEditText = view.findViewById(R.id.editSubjectName);
        EditText codeEditText = view.findViewById(R.id.editSubjectCode);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String code = codeEditText.getText().toString().trim();

            Subject newSubject = new Subject(PublicConstants.user.getId(), name, code, "");
            listSubject.add(newSubject);
            db.addSubject(newSubject);
            subjectAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    @Override
    public void onDeleteClick(Subject subject) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa môn học này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    listSubject.remove(subject);
                    db.deleteSubject(subject.getId());
                    subjectAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onEditClick(Subject subject) {
        showEditSubjectDialog(subject);
    }

    private void showEditSubjectDialog(Subject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa Môn Học");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_subject, null);
        builder.setView(view);

        EditText nameEditText = view.findViewById(R.id.editSubjectName);
        EditText codeEditText = view.findViewById(R.id.editSubjectCode);

        nameEditText.setText(subject.getName());
        codeEditText.setText(subject.getCode());

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            subject.setName(nameEditText.getText().toString());
            subject.setCode(codeEditText.getText().toString());

            subjectAdapter.notifyDataSetChanged();
            db.updateSubject(subject);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

}