package com.example.hoctap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hoctap.database.DatabaseHelper;
import com.example.hoctap.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public TodoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTodoItem(TodoItem todoItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, todoItem.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, todoItem.getDescription());
        values.put(DatabaseHelper.COLUMN_COMPLETED, todoItem.isCompleted() ? 1 : 0);

        return database.insert(DatabaseHelper.TABLE_TODO, null, values);
    }

    public int updateTodoItem(TodoItem todoItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, todoItem.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, todoItem.getDescription());
        values.put(DatabaseHelper.COLUMN_COMPLETED, todoItem.isCompleted() ? 1 : 0);

        return database.update(DatabaseHelper.TABLE_TODO, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(todoItem.getId())});
    }

    public void deleteTodoItem(long id) {
        database.delete(DatabaseHelper.TABLE_TODO,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public TodoItem getTodoItem(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_TODO,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            TodoItem todoItem = cursorToTodoItem(cursor);
            cursor.close();
            return todoItem;
        }
        return null;
    }

    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todoItems = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_TODO,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_COMPLETED + " ASC, " +
                        DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TodoItem todoItem = cursorToTodoItem(cursor);
                todoItems.add(todoItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return todoItems;
    }

    public List<TodoItem> getActiveTodoItems() {
        List<TodoItem> todoItems = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_TODO,
                null,
                DatabaseHelper.COLUMN_COMPLETED + " = ?",
                new String[]{"0"},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TodoItem todoItem = cursorToTodoItem(cursor);
                todoItems.add(todoItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return todoItems;
    }

    public List<TodoItem> getCompletedTodoItems() {
        List<TodoItem> todoItems = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_TODO,
                null,
                DatabaseHelper.COLUMN_COMPLETED + " = ?",
                new String[]{"1"},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TodoItem todoItem = cursorToTodoItem(cursor);
                todoItems.add(todoItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return todoItems;
    }

    private TodoItem cursorToTodoItem(Cursor cursor) {
        TodoItem todoItem = new TodoItem();
        todoItem.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        todoItem.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
        todoItem.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
        todoItem.setCompleted(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_COMPLETED)) == 1);
        todoItem.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return todoItem;
    }
}

