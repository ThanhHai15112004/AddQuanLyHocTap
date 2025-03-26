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
import com.example.hoctap.dao.ScoreDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Score;
import com.example.hoctap.models.Subject;

import java.util.List;

public class AddScoreActivity extends AppCompatActivity {
    private EditText etScoreType, etScoreValue, etScoreWeight;
    private Spinner spinnerSubject;
    private Button btnSave;

    private SubjectDAO subjectDAO;
    private ScoreDAO scoreDAO;
    private List<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        etScoreType = findViewById(R.id.etScoreType);
        etScoreValue = findViewById(R.id.etScoreValue);
        etScoreWeight = findViewById(R.id.etScoreWeight);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnSave = findViewById(R.id.btnSave);

        // Initialize DAOs
        subjectDAO = new SubjectDAO(this);
        scoreDAO = new ScoreDAO(this);

        // Setup subject spinner
        setupSubjectSpinner();

        // Setup save button
        btnSave.setOnClickListener(v -> saveScore());
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

    private void saveScore() {
        // Validate inputs
        String scoreType = etScoreType.getText().toString().trim();
        String scoreValueStr = etScoreValue.getText().toString().trim();
        String scoreWeightStr = etScoreWeight.getText().toString().trim();

        if (scoreType.isEmpty()) {
            Toast.makeText(this, "Please enter a score type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (scoreValueStr.isEmpty()) {
            Toast.makeText(this, "Please enter a score value", Toast.LENGTH_SHORT).show();
            return;
        }

        double scoreValue;
        try {
            scoreValue = Double.parseDouble(scoreValueStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid score value", Toast.LENGTH_SHORT).show();
            return;
        }

        double scoreWeight = 1.0; // Default weight
        if (!scoreWeightStr.isEmpty()) {
            try {
                scoreWeight = Double.parseDouble(scoreWeightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid weight value", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Get selected subject
        Subject selectedSubject = (Subject) spinnerSubject.getSelectedItem();

        // Create new score
        Score score = new Score();
        score.setScoreType(scoreType);
        score.setScoreValue(scoreValue);
        score.setScoreWeight(scoreWeight);
        score.setSubjectId(selectedSubject.getId());

        // Save to database
        scoreDAO.open();
        long result = scoreDAO.insertScore(score);
        scoreDAO.close();

        if (result > 0) {
            Toast.makeText(this, "Score added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add score", Toast.LENGTH_SHORT).show();
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

