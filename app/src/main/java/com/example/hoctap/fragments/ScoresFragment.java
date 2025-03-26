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
import com.example.hoctap.activities.AddScoreActivity;
import com.example.hoctap.adapters.ScoreAdapter;
import com.example.hoctap.dao.ScoreDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Score;
import com.example.hoctap.models.Subject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ScoresFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvNoScores, tvAverageScore;
    private Spinner spinnerSubject, spinnerSemester;
    private FloatingActionButton fabAdd;
    private ScoreAdapter adapter;
    private ScoreDAO scoreDAO;
    private SubjectDAO subjectDAO;
    private List<Subject> subjects;
    private List<String> semesters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scores, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvNoScores = view.findViewById(R.id.tvNoScores);
        tvAverageScore = view.findViewById(R.id.tvAverageScore);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        spinnerSemester = view.findViewById(R.id.spinnerSemester);
        fabAdd = view.findViewById(R.id.fabAdd);

        scoreDAO = new ScoreDAO(getContext());
        subjectDAO = new SubjectDAO(getContext());

        setupRecyclerView();
        setupSpinners();
        setupFab();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScoreAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        // Get all subjects and semesters
        subjectDAO.open();
        subjects = subjectDAO.getAllSubjects();
        semesters = new ArrayList<>();
        for (Subject subject : subjects) {
            if (!semesters.contains(subject.getSemester()) && subject.getSemester() != null) {
                semesters.add(subject.getSemester());
            }
        }
        subjectDAO.close();

        // Subject spinner
        List<String> subjectNames = new ArrayList<>();
        subjectNames.add("All Subjects");
        for (Subject subject : subjects) {
            subjectNames.add(subject.getSubjectName());
        }

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

        // Semester spinner
        List<String> semesterNames = new ArrayList<>();
        semesterNames.add("All Semesters");
        semesterNames.addAll(semesters);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, semesterNames);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        // Spinner listeners
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterScores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterScores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddScoreActivity.class);
            startActivity(intent);
        });
    }

    private void filterScores() {
        int subjectPosition = spinnerSubject.getSelectedItemPosition();
        int semesterPosition = spinnerSemester.getSelectedItemPosition();

        Long subjectId = null;
        if (subjectPosition > 0 && subjects.size() >= subjectPosition) {
            subjectId = subjects.get(subjectPosition - 1).getId();
        }

        String semester = null;
        if (semesterPosition > 0 && semesters.size() >= semesterPosition) {
            semester = semesters.get(semesterPosition - 1);
        }

        loadScores(subjectId, semester);
    }

    private void loadScores(Long subjectId, String semester) {
        scoreDAO.open();
        List<Score> scores;
        double averageScore;

        if (subjectId != null) {
            // Get scores for specific subject
            scores = scoreDAO.getScoresBySubject(subjectId);
            averageScore = scoreDAO.getAverageScoreBySubject(subjectId);
        } else if (semester != null) {
            // Get scores for specific semester
            scores = scoreDAO.getScoresBySemester(semester);
            averageScore = scoreDAO.getAverageScoreBySemester(semester);
        } else {
            // Get all scores
            scores = scoreDAO.getAllScores();
            averageScore = scoreDAO.getOverallAverageScore();
        }
        scoreDAO.close();

        if (scores.isEmpty()) {
            tvNoScores.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvAverageScore.setText("Average Score: N/A");
        } else {
            tvNoScores.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setScores(scores);
            tvAverageScore.setText(String.format("Average Score: %.2f", averageScore));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filterScores();
    }
}

