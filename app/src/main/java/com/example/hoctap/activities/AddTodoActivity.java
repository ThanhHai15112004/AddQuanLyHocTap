package com.example.hoctap.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.TodoDAO;
import com.example.hoctap.models.TodoItem;

public class AddTodoActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private Button btnSave;

    private TodoDAO todoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Initialize DAO
        todoDAO = new TodoDAO(this);

        // Setup save button
        btnSave.setOnClickListener(v -> saveTodo());
    }

    private void saveTodo() {
        // Validate inputs
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new todo item
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle(title);
        todoItem.setDescription(description);
        todoItem.setCompleted(false);

        // Save to database
        todoDAO.open();
        long result = todoDAO.insertTodoItem(todoItem);
        todoDAO.close();

        if (result > 0) {
            Toast.makeText(this, "To-do item added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add to-do item", Toast.LENGTH_SHORT).show();
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

