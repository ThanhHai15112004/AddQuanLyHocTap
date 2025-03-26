package com.example.hoctap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.activities.AddAssignmentActivity;
import com.example.hoctap.adapters.AssignmentAdapter;
import com.example.hoctap.dao.AssignmentDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Assignment;
import com.example.hoctap.models.Subject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvNoAssignments;
    private Spinner spinnerSubject, spinnerPriority, spinnerStatus;
    private FloatingActionButton fabAdd;
    private AssignmentAdapter adapter;
    private AssignmentDAO assignmentDAO;
    private SubjectDAO subjectDAO;
    private List<Subject> subjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvNoAssignments = view.findViewById(R.id.tvNoAssignments);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        fabAdd = view.findViewById(R.id.fabAdd);

        assignmentDAO = new AssignmentDAO(getContext());
        subjectDAO = new SubjectDAO(getContext());

        setupRecyclerView();
        setupSpinners();
        setupFab();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignmentAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        // Subject spinner
        subjectDAO.open();
        subjects = subjectDAO.getAllSubjects();
        subjectDAO.close();

        List<String> subjectNames = new ArrayList<>();
        subjectNames.add("All Subjects");
        for (Subject subject : subjects) {
            subjectNames.add(subject.getSubjectName());
        }

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

        // Priority spinner
        String[] priorities = {"All Priorities", "Low", "Medium", "High"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Status spinner
        String[] statuses = {"All Statuses", "Pending", "In Progress", "Completed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Spinner listeners
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAssignments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAssignments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAssignments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddAssignmentActivity.class);
            startActivity(intent);
        });
    }

    private void filterAssignments() {
        int subjectPosition = spinnerSubject.getSelectedItemPosition();
        int priorityPosition = spinnerPriority.getSelectedItemPosition();
        int statusPosition = spinnerStatus.getSelectedItemPosition();

        Long subjectId = null;
        if (subjectPosition > 0 && subjects.size() >= subjectPosition) {
            subjectId = subjects.get(subjectPosition - 1).getId();
        }

        Integer priority = null;
        if (priorityPosition > 0) {
            priority = priorityPosition - 1;
        }

        Integer status = null;
        if (statusPosition > 0) {
            status = statusPosition - 1;
        }

        loadAssignments(subjectId, priority, status);
    }

    private void loadAssignments(Long subjectId, Integer priority, Integer status) {
        assignmentDAO.open();
        List<Assignment> assignments = assignmentDAO.getFilteredAssignments(subjectId, priority, status);
        assignmentDAO.close();

        if (assignments.isEmpty()) {
            tvNoAssignments.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoAssignments.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setAssignments(assignments);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filterAssignments();
    }
}

