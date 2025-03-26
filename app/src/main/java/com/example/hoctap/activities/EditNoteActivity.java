package com.example.hoctap.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.NoteDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Note;
import com.example.hoctap.models.Subject;

import java.util.List;

public class EditNoteActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private Spinner spinnerSubject;
    private Button btnSave, btnDelete;

    private SubjectDAO subjectDAO;
    private NoteDAO noteDAO;
    private List<Subject> subjects;
    private Note note;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId == -1) {
            showToast("Error: Invalid note");
            finish();
            return;
        }

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        subjectDAO = new SubjectDAO(this);
        noteDAO = new NoteDAO(this);

        if (!loadNote()) {
            finish();
            return;
        }

        setupSubjectSpinner();

        btnSave.setOnClickListener(v -> saveNote());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private boolean loadNote() {
        try {
            noteDAO.open();
            note = noteDAO.getNote(noteId);
            if (note == null) {
                showToast("Error: Note not found");
                return false;
            }
            etTitle.setText(note.getTitle());
            etDescription.setText(note.getDescription());
            return true;
        } catch (Exception e) {
            showToast("Error loading note: " + e.getMessage());
            return false;
        } finally {
            noteDAO.close();
        }
    }

    private void setupSubjectSpinner() {
        try {
            subjectDAO.open();
            subjects = subjectDAO.getAllSubjects();
        } catch (Exception e) {
            showToast("Error loading subjects: " + e.getMessage());
            finish();
            return;
        } finally {
            subjectDAO.close();
        }

        if (subjects == null || subjects.isEmpty()) {
            showToast("No subjects available. Please add subjects first.");
            finish();
            return;
        }

        ArrayAdapter<Subject> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);

        // Set selected subject
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getId() == note.getSubjectId()) {
                spinnerSubject.setSelection(i);
                break;
            }
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            showToast("Please enter a title");
            return;
        }

        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();
        if (selectedSubject == null) {
            showToast("Please select a subject");
            return;
        }

        note.setTitle(title);
        note.setDescription(description);
        note.setSubjectId(selectedSubject.getId());

        try {
            noteDAO.open();
            int result = noteDAO.updateNote(note);
            if (result > 0) {
                showToast("Note updated successfully");
                finish();
            } else {
                showToast("Failed to update note");
            }
        } catch (Exception e) {
            showToast("Error updating note: " + e.getMessage());
        } finally {
            noteDAO.close();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNote())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote() {
        try {
            noteDAO.open();
            noteDAO.deleteNote(noteId);
            showToast("Note deleted");
            finish();
        } catch (Exception e) {
            showToast("Error deleting note: " + e.getMessage());
        } finally {
            noteDAO.close();
        }
    }

    private void showToast(String message) {
        if (!isFinishing()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteDAO != null) noteDAO.close();
        if (subjectDAO != null) subjectDAO.close();
    }
}