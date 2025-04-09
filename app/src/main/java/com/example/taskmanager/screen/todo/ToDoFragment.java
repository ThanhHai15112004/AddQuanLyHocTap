package com.example.taskmanager.screen.todo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.R;
import com.example.taskmanager.databinding.FragmentToDoBinding;
import com.example.taskmanager.model.Todo;
import com.example.taskmanager.screen.DatabaseHelper;
import java.util.Calendar;
import java.util.List;

public class ToDoFragment extends Fragment {
    List<Todo> listTodo;
    DatabaseHelper db;
    TodoAdapter todoAdapter;
    FragmentToDoBinding binding;
    public ToDoFragment() {
    }
    public static ToDoFragment newInstance(String param1, String param2) {
        return new ToDoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentToDoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());
        binding.btnAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTodoDialog();
            }
        });

        listTodo = db.getTodosByUserId(PublicConstants.user.getId());
        binding.rvListTodo.setLayoutManager(new LinearLayoutManager(getContext()));
        todoAdapter = new TodoAdapter(getContext(), listTodo, new TodoAdapter.OnTodoClickListener() {
            @Override
            public void onTodoClick(Todo todo) {
                showEditDeleteTodoDialog(todo);
            }

            @Override
            public void onStatusChanged(Todo todo, boolean isDone) {

            }
        });
        binding.rvListTodo.setAdapter(todoAdapter);
    }

    private void showAddTodoDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_todo, null);
        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDueDate = view.findViewById(R.id.etDueDate);

        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm To-Do")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String title = etTitle.getText().toString();
                    String dueDate = etDueDate.getText().toString();
                    if (!title.isEmpty() && !dueDate.isEmpty()) {
                        Todo newTodo = new Todo(PublicConstants.user.getId(), title, false, dueDate, 0);
                        db.addTodo(newTodo);
                        listTodo.add(newTodo);
                        todoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        etDueDate.setOnClickListener(v -> showDateTimePicker(etDueDate));
    }

    private void showDateTimePicker(final EditText etDueDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timeView, selectedHour, selectedMinute) -> {
                String dueDate = String.format("%02d/%02d/%04d %02d:%02d", selectedDay, selectedMonth + 1, selectedYear, selectedHour, selectedMinute);
                etDueDate.setText(dueDate);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showEditDeleteTodoDialog(Todo todo) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_todo, null);
        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDueDate = view.findViewById(R.id.etDueDate);

        etTitle.setText(todo.getTitle());
        etDueDate.setText(todo.getDueDate());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa To-Do")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String title = etTitle.getText().toString();
                    String dueDate = etDueDate.getText().toString();

                    if (!title.isEmpty() && !dueDate.isEmpty()) {
                        todo.setTitle(title);
                        todo.setDueDate(dueDate);
                        db.updateTodo(todo);
                        todoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Xóa", (dialog, which) -> {
                    db.deleteTodo(todo.getId());
                    listTodo.remove(todo);
                    todoAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

        etDueDate.setOnClickListener(v -> showDateTimePicker(etDueDate));
    }



}