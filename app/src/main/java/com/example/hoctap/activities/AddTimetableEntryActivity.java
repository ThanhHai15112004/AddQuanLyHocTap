package com.example.hoctap.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.dao.TimetableDAO;
import com.example.hoctap.models.Subject;
import com.example.hoctap.models.TimetableEntry;

import java.util.Calendar;
import java.util.List;

public class AddTimetableEntryActivity extends AppCompatActivity {
    private Spinner spinnerSubject, spinnerDay;
    private TextView tvStartTime, tvEndTime;
    private EditText etLocation;
    private Button btnSelectStartTime, btnSelectEndTime, btnSave;

    private SubjectDAO subjectDAO;
    private TimetableDAO timetableDAO;
    private List<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timetable_entry);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        // Initialize DAOs
        subjectDAO = new SubjectDAO(this);
        timetableDAO = new TimetableDAO(this);

        // Setup spinners
        setupSubjectSpinner();
        setupDaySpinner();

        // Setup time pickers
        btnSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        btnSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        // Setup save button
        btnSave.setOnClickListener(v -> saveEntry());
    }

    private void setupSubjectSpinner() {
        subjectDAO.open();
        subjects = subjectDAO.getAllSubjects();
        subjectDAO.close();

        if (subjects.isEmpty()) {
            Toast.makeText(this, "Please add subjects first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayAdapter<Subject> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);
    }

    private void setupDaySpinner() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);

        // Set default day to current day
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2; // Convert to 0-indexed (Monday = 0)
        if (dayOfWeek < 0) dayOfWeek = 6; // Sunday becomes 6
        spinnerDay.setSelection(dayOfWeek);
    }

    private void showTimePickerDialog(final boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

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
        if (tvStartTime.getText().toString().equals("Select start time") ||
                tvEndTime.getText().toString().equals("Select end time")) {
            Toast.makeText(this, "Please select start and end times", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected subject
        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();

        // Create new timetable entry
        TimetableEntry entry = new TimetableEntry();
        entry.setSubjectId(selectedSubject.getId());
        entry.setDayOfWeek(spinnerDay.getSelectedItemPosition());
        entry.setStartTime(tvStartTime.getText().toString());
        entry.setEndTime(tvEndTime.getText().toString());
        entry.setLocation(etLocation.getText().toString());

        // Save to database
        timetableDAO.open();
        long result = timetableDAO.insertTimetableEntry(entry);
        timetableDAO.close();

        if (result > 0) {
            Toast.makeText(this, "Class added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add class", Toast.LENGTH_SHORT).show();
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

