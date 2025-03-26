package com.example.hoctap.fragments;

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
import com.example.hoctap.adapters.NoteAdapter;
import com.example.hoctap.dao.NoteDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Note;
import com.example.hoctap.models.Subject;

import java.util.ArrayList;
import java.util.List;

public class NotesListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvNoNotes;
    private Spinner spinnerSubject;
    private NoteAdapter adapter;
    private NoteDAO noteDAO;
    private SubjectDAO subjectDAO;
    private List<Subject> subjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvNoNotes = view.findViewById(R.id.tvNoNotes);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);

        noteDAO = new NoteDAO(getContext());
        subjectDAO = new SubjectDAO(getContext());

        setupRecyclerView();
        setupSubjectSpinner();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NoteAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupSubjectSpinner() {
        subjectDAO.open();
        subjects = subjectDAO.getAllSubjects();
        subjectDAO.close();

        List<String> subjectNames = new ArrayList<>();
        subjectNames.add("All Subjects");
        for (Subject subject : subjects) {
            subjectNames.add(subject.getSubjectName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, subjectNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(spinnerAdapter);

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterNotes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterNotes(int position) {
        noteDAO.open();
        List<Note> notes;

        if (position == 0) {
            // All subjects
            notes = noteDAO.getAllNotes();
        } else {
            // Specific subject
            long subjectId = subjects.get(position - 1).getId();
            notes = noteDAO.getNotesBySubject(subjectId);
        }

        noteDAO.close();

        if (notes.isEmpty()) {
            tvNoNotes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoNotes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setNotes(notes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (spinnerSubject != null) {
            filterNotes(spinnerSubject.getSelectedItemPosition());
        }
    }
}

