package com.example.hoctap.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.AssignmentDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Assignment;
import com.example.hoctap.models.Subject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditAssignmentActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private Spinner spinnerSubject, spinnerPriority, spinnerStatus;
    private TextView tvDeadline, tvReminder;
    private Button btnSelectDeadline, btnSelectReminder, btnSave, btnDelete;

    private SubjectDAO subjectDAO;
    private AssignmentDAO assignmentDAO;
    private List<Subject> subjects;
    private Assignment assignment;
    private long assignmentId;

    private Calendar deadlineCalendar = Calendar.getInstance();
    private Calendar reminderCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get assignment ID from intent
        assignmentId = getIntent().getLongExtra("assignment_id", -1);
        if (assignmentId == -1) {
            Toast.makeText(this, "Error: Invalid assignment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvReminder = findViewById(R.id.tvReminder);
        btnSelectDeadline = findViewById(R.id.btnSelectDeadline);
        btnSelectReminder = findViewById(R.id.btnSelectReminder);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Initialize DAOs
        subjectDAO = new SubjectDAO(this);
        assignmentDAO = new AssignmentDAO(this);

        // Load assignment data
        loadAssignment();

        // Setup spinners
        setupSubjectSpinner();
        setupPrioritySpinner();
        setupStatusSpinner();

        // Setup date/time pickers
        btnSelectDeadline.setOnClickListener(v -> showDateTimePicker(true));
        btnSelectReminder.setOnClickListener(v -> showDateTimePicker(false));

        // Setup save button
        btnSave.setOnClickListener(v -> saveAssignment());

        // Setup delete button
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadAssignment() {
        assignmentDAO.open();
        assignment = assignmentDAO.getAssignment(assignmentId);
        assignmentDAO.close();

        if (assignment == null) {
            Toast.makeText(this, "Error: Assignment not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial values
        etTitle.setText(assignment.getTitle());
        etDescription.setText(assignment.getDescription());
        tvDeadline.setText(assignment.getDeadline());

        if (assignment.getReminderTime() != null && !assignment.getReminderTime().isEmpty()) {
            tvReminder.setText(assignment.getReminderTime());
        }

        // Parse dates for calendars
        try {
            if (assignment.getDeadline() != null && !assignment.getDeadline().isEmpty()) {
                Date deadlineDate = dateFormat.parse(assignment.getDeadline());
                if (deadlineDate != null) {
                    deadlineCalendar.setTime(deadlineDate);
                }
            }

            if (assignment.getReminderTime() != null && !assignment.getReminderTime().isEmpty()) {
                Date reminderDate = dateFormat.parse(assignment.getReminderTime());
                if (reminderDate != null) {
                    reminderCalendar.setTime(reminderDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            if (subjects.get(i).getId() == assignment.getSubjectId()) {
                spinnerSubject.setSelection(i);
                break;
            }
        }
    }

    private void setupPrioritySpinner() {
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        // Set selected priority
        spinnerPriority.setSelection(assignment.getPriority());
    }

    private void setupStatusSpinner() {
        String[] statuses = {"Pending", "In Progress", "Completed"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Set selected status
        spinnerStatus.setSelection(assignment.getStatus());
    }

    private void showDateTimePicker(final boolean isDeadline) {
        final Calendar calendar = isDeadline ? deadlineCalendar : reminderCalendar;

        // Show date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // After date is set, show time picker
                        showTimePicker(isDeadline, calendar);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePicker(final boolean isDeadline, final Calendar calendar) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Update the TextView
                        String formattedDateTime = dateFormat.format(calendar.getTime());
                        if (isDeadline) {
                            tvDeadline.setText(formattedDateTime);
                        } else {
                            tvReminder.setText(formattedDateTime);
                        }
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    private void saveAssignment() {
        // Validate inputs
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tvDeadline.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected subject
        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();

        // Update assignment
        assignment.setTitle(title);
        assignment.setDescription(etDescription.getText().toString().trim());
        assignment.setSubjectId(selectedSubject.getId());
        assignment.setDeadline(tvDeadline.getText().toString());

        // Set reminder if selected
        if (!tvReminder.getText().toString().equals("Select reminder time") &&
                !tvReminder.getText().toString().isEmpty()) {
            assignment.setReminderTime(tvReminder.getText().toString());
        } else {
            assignment.setReminderTime(null);
        }

        // Set priority and status
        assignment.setPriority(spinnerPriority.getSelectedItemPosition());
        assignment.setStatus(spinnerStatus.getSelectedItemPosition());

        // Save to database
        assignmentDAO.open();
        int result = assignmentDAO.updateAssignment(assignment);
        assignmentDAO.close();

        if (result > 0) {
            Toast.makeText(this, "Assignment updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update assignment", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Assignment")
                .setMessage("Are you sure you want to delete this assignment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    assignmentDAO.open();
                    assignmentDAO.deleteAssignment(assignmentId);
                    assignmentDAO.close();
                    Toast.makeText(EditAssignmentActivity.this, "Assignment deleted", Toast.LENGTH_SHORT).show();
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

