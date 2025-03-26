package com.example.hoctap.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.dao.TimetableDAO;
import com.example.hoctap.models.Subject;
import com.example.hoctap.models.TimetableEntry;

import java.util.List;

public class EditTimetableEntryActivity extends AppCompatActivity {
    private Spinner spinnerSubject, spinnerDay;
    private TextView tvStartTime, tvEndTime;
    private EditText etLocation;
    private Button btnSelectStartTime, btnSelectEndTime, btnSave, btnDelete;

    private SubjectDAO subjectDAO;
    private TimetableDAO timetableDAO;
    private List<Subject> subjects;
    private TimetableEntry entry;
    private long entryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timetable_entry);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get entry ID from intent
        entryId = getIntent().getLongExtra("entry_id", -1);
        if (entryId == -1) {
            Toast.makeText(this, "Error: Invalid entry", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerDay = findViewById(R.id.spinnerDay);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        etLocation = findViewById(R.id.etLocation);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Initialize DAOs
        subjectDAO = new SubjectDAO(this);
        timetableDAO = new TimetableDAO(this);

        // Load entry data
        loadEntry();

        // Setup spinners
        setupSubjectSpinner();
        setupDaySpinner();

        // Setup time pickers
        btnSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        btnSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        // Setup save button
        btnSave.setOnClickListener(v -> saveEntry());

        // Setup delete button
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadEntry() {
        timetableDAO.open();
        entry = timetableDAO.getTimetableEntry(entryId);
        timetableDAO.close();

        if (entry == null) {
            Toast.makeText(this, "Error: Entry not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial values
        tvStartTime.setText(entry.getStartTime());
        tvEndTime.setText(entry.getEndTime());
        etLocation.setText(entry.getLocation());
    }

    private void setupSubjectSpinner() {
        subjectDAO.open();
        subjects = subjectDAO.getAllSubjects();
        subjectDAO.close();

        ArrayAdapter<Subject> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);

        // Set selected subject
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getId() == entry.getSubjectId()) {
                spinnerSubject.setSelection(i);
                break;
            }
        }
    }

    private void setupDaySpinner() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);

        // Set selected day
        spinnerDay.setSelection(entry.getDayOfWeek());
    }

    private void showTimePickerDialog(final boolean isStartTime) {
        // Parse current time
        String timeStr = isStartTime ? entry.getStartTime() : entry.getEndTime();
        int hour = 8, minute = 0; // Default values

        if (timeStr != null && !timeStr.isEmpty()) {
            String[] parts = timeStr.split(":");
            if (parts.length == 2) {
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        if (isStartTime) {
                            tvStartTime.setText(time);
                        } else {
                            tvEndTime.setText(time);
                        }
                    }
                },
                hour, minute, true);

        timePickerDialog.show();
    }

    private void saveEntry() {
        // Validate inputs
        if (tvStartTime.getText().toString().isEmpty() ||
                tvEndTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select start and end times", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected subject
        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();

        // Update entry
        entry.setSubjectId(selectedSubject.getId());
        entry.setDayOfWeek(spinnerDay.getSelectedItemPosition());
        entry.setStartTime(tvStartTime.getText().toString());
        entry.setEndTime(tvEndTime.getText().toString());
        entry.setLocation(etLocation.getText().toString());

        // Save to database
        timetableDAO.open();
        int result = timetableDAO.updateTimetableEntry(entry);
        timetableDAO.close();

        if (result > 0) {
            Toast.makeText(this, "Class updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update class", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    timetableDAO.open();
                    timetableDAO.deleteTimetableEntry(entryId);
                    timetableDAO.close();
                    Toast.makeText(EditTimetableEntryActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
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

