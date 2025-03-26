package com.example.hoctap.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoctap.R;
import com.example.hoctap.dao.TodoDAO;
import com.example.hoctap.models.TodoItem;

public class EditTodoActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private CheckBox checkBoxCompleted;
    private Button btnSave, btnDelete;

    private TodoDAO todoDAO;
    private TodoItem todoItem;
    private long todoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get todo ID from intent
        todoId = getIntent().getLongExtra("todo_id", -1);
        if (todoId == -1) {
            Toast.makeText(this, "Error: Invalid to-do item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        checkBoxCompleted = findViewById(R.id.checkBoxCompleted);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Initialize DAO
        todoDAO = new TodoDAO(this);

        // Load todo data
        loadTodoItem();

        // Setup save button
        btnSave.setOnClickListener(v -> saveTodoItem());

        // Setup delete button
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadTodoItem() {
        todoDAO.open();
        todoItem = todoDAO.getTodoItem(todoId);
        todoDAO.close();

        if (todoItem == null) {
            Toast.makeText(this, "Error: To-do item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial values
        etTitle.setText(todoItem.getTitle());
        etDescription.setText(todoItem.getDescription());
        checkBoxCompleted.setChecked(todoItem.isCompleted());
    }

    private void saveTodoItem() {
        // Validate inputs
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update todo item
        todoItem.setTitle(title);
        todoItem.setDescription(description);
        todoItem.setCompleted(checkBoxCompleted.isChecked());

        // Save to database
        todoDAO.open();
        int result = todoDAO.updateTodoItem(todoItem);
        todoDAO.close();

        if (result > 0) {
            Toast.makeText(this, "To-do item updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update to-do item", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete To-Do Item")
                .setMessage("Are you sure you want to delete this to-do item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    todoDAO.open();
                    todoDAO.deleteTodoItem(todoId);
                    todoDAO.close();
                    Toast.makeText(EditTodoActivity.this, "To-do item deleted", Toast.LENGTH_SHORT).show();
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

