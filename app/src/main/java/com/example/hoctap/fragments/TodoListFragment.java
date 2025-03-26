package com.example.hoctap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.adapters.TodoAdapter;
import com.example.hoctap.dao.TodoDAO;
import com.example.hoctap.models.TodoItem;

import java.util.List;

public class TodoListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvNoTodos;
    private Button btnAll, btnActive, btnCompleted;
    private com.example.hoctap.adapters.TodoAdapter adapter;
    private TodoDAO todoDAO;
    private int currentFilter = 0; // 0: All, 1: Active, 2: Completed

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvNoTodos = view.findViewById(R.id.tvNoTodos);
        btnAll = view.findViewById(R.id.btnAll);
        btnActive = view.findViewById(R.id.btnActive);
        btnCompleted = view.findViewById(R.id.btnCompleted);

        todoDAO = new TodoDAO(getContext());

        setupRecyclerView();
        setupFilterButtons();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        btnAll.setOnClickListener(v -> {
            if (currentFilter != 0) {
                currentFilter = 0;
                updateButtonStyles();
                loadTodoItems();
            }
        });

        btnActive.setOnClickListener(v -> {
            if (currentFilter != 1) {
                currentFilter = 1;
                updateButtonStyles();
                loadTodoItems();
            }
        });

        btnCompleted.setOnClickListener(v -> {
            if (currentFilter != 2) {
                currentFilter = 2;
                updateButtonStyles();
                loadTodoItems();
            }
        });
    }

    private void updateButtonStyles() {
        btnAll.setBackgroundResource(currentFilter == 0 ? R.drawable.button_selected : R.drawable.button_normal);
        btnActive.setBackgroundResource(currentFilter == 1 ? R.drawable.button_selected : R.drawable.button_normal);
        btnCompleted.setBackgroundResource(currentFilter == 2 ? R.drawable.button_selected : R.drawable.button_normal);
    }

    private void loadTodoItems() {
        todoDAO.open();
        List<TodoItem> todoItems;

        switch (currentFilter) {
            case 1:
                todoItems = todoDAO.getActiveTodoItems();
                break;
            case 2:
                todoItems = todoDAO.getCompletedTodoItems();
                break;
            default:
                todoItems = todoDAO.getAllTodoItems();
                break;
        }

        todoDAO.close();

        if (todoItems.isEmpty()) {
            tvNoTodos.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoTodos.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setTodoItems(todoItems);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodoItems();
    }
}

