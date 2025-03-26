package com.example.hoctap.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.NoteDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Note;
import com.example.hoctap.models.Subject;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private Spinner spinnerSubject;
    private Button btnSave;

    private SubjectDAO subjectDAO;
    private NoteDAO noteDAO;
    private List<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnSave = findViewById(R.id.btnSave);

        subjectDAO = new SubjectDAO(this);
        noteDAO = new NoteDAO(this);

        setupSubjectSpinner();

        btnSave.setOnClickListener(v -> saveNote());
    }

    private void setupSubjectSpinner() {
        try {
            subjectDAO.open();
            subjects = subjectDAO.getAllSubjects();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            subjectDAO.close();
        }

        if (subjects == null || subjects.isEmpty()) {
            Toast.makeText(this, "Please add subjects first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayAdapter<Subject> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();
        if (selectedSubject == null) {
            Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note();
        note.setTitle(title);
        note.setDescription(description);
        note.setSubjectId(selectedSubject.getId());

        try {
            noteDAO.open();
            long result = noteDAO.insertNote(note);
            if (result > 0) {
                Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            noteDAO.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}