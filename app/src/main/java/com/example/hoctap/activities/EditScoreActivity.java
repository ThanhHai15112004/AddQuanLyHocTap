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
import com.example.hoctap.dao.ScoreDAO;
import com.example.hoctap.dao.SubjectDAO;
import com.example.hoctap.models.Score;
import com.example.hoctap.models.Subject;

import java.util.List;
import java.util.Locale;

public class EditScoreActivity extends AppCompatActivity {
    private EditText etScoreType, etScoreValue, etScoreWeight;
    private Spinner spinnerSubject;
    private Button btnSave, btnDelete;

    private SubjectDAO subjectDAO;
    private ScoreDAO scoreDAO;
    private List<Subject> subjects;
    private Score score;
    private long scoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_score);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get score ID from intent
        scoreId = getIntent().getLongExtra("score_id", -1);
        if (scoreId == -1) {
            Toast.makeText(this, "Error: Invalid score", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        etScoreType = findViewById(R.id.etScoreType);
        etScoreValue = findViewById(R.id.etScoreValue);
        etScoreWeight = findViewById(R.id.etScoreWeight);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Initialize DAOs
        subjectDAO = new SubjectDAO(this);
        scoreDAO = new ScoreDAO(this);

        // Load score data
        loadScore();

        // Setup subject spinner
        setupSubjectSpinner();

        // Setup save button
        btnSave.setOnClickListener(v -> saveScore());

        // Setup delete button
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadScore() {
        scoreDAO.open();
        score = scoreDAO.getScore(scoreId);
        scoreDAO.close();

        if (score == null) {
            Toast.makeText(this, "Error: Score not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial values
        etScoreType.setText(score.getScoreType());
        etScoreValue.setText(String.format(Locale.getDefault(), "%.1f", score.getScoreValue()));
        etScoreWeight.setText(String.format(Locale.getDefault(), "%.1f", score.getScoreWeight()));
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
            if (subjects.get(i).getId() == score.getSubjectId()) {
                spinnerSubject.setSelection(i);
                break;
            }
        }
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

        // Update score
        score.setScoreType(scoreType);
        score.setScoreValue(scoreValue);
        score.setScoreWeight(scoreWeight);
        score.setSubjectId(selectedSubject.getId());

        // Save to database
        scoreDAO.open();
        int result = scoreDAO.updateScore(score);
        scoreDAO.close();

        if (result > 0) {
            Toast.makeText(this, "Score updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update score", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Score")
                .setMessage("Are you sure you want to delete this score?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    scoreDAO.open();
                    scoreDAO.deleteScore(scoreId);
                    scoreDAO.close();
                    Toast.makeText(EditScoreActivity.this, "Score deleted", Toast.LENGTH_SHORT).show();
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

