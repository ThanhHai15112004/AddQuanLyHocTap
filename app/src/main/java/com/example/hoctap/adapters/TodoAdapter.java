package com.example.hoctap.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.activities.EditTodoActivity; // Giả sử bạn có activity này để chỉnh sửa
import com.example.hoctap.dao.TodoDAO;
import com.example.hoctap.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private final Context context;
    private List<TodoItem> todoItems;
    private final TodoDAO todoDAO;

    public TodoAdapter(Context context) {
        this.context = context;
        this.todoItems = new ArrayList<>();
        this.todoDAO = new TodoDAO(context);
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems != null ? todoItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);

        // Bind data
        holder.tvTitle.setText(todoItem.getTitle() != null ? todoItem.getTitle() : "No Title");
        holder.checkBox.setChecked(todoItem.isCompleted());

        String description = todoItem.getDescription();
        if (description != null && !description.isEmpty()) {
            holder.tvDescription.setText(description);
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Checkbox listener
        holder.checkBox.setOnClickListener(v -> {
            todoItem.setCompleted(holder.checkBox.isChecked());
            try {
                todoDAO.open();
                todoDAO.updateTodoItem(todoItem);
            } catch (Exception e) {
                Toast.makeText(context, "Error updating todo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                todoDAO.close();
            }
            notifyItemChanged(position); // Refresh item UI
        });

        // Delete button listener
        holder.btnDelete.setOnClickListener(v -> {
            try {
                todoDAO.open();
                todoDAO.deleteTodoItem(todoItem.getId());
                todoItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, todoItems.size());
                Toast.makeText(context, "Todo item deleted", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Error deleting todo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                todoDAO.close();
            }
        });

        // Item click listener for editing
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTodoActivity.class);
            intent.putExtra("todo_id", todoItem.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvTitle, tvDescription;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}