package com.example.hoctap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hoctap.database.DatabaseHelper;
import com.example.hoctap.models.Assignment;

import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private SubjectDAO subjectDAO;

    public AssignmentDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        subjectDAO = new SubjectDAO(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertAssignment(Assignment assignment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_ID, assignment.getSubjectId());
        values.put(DatabaseHelper.COLUMN_TITLE, assignment.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(DatabaseHelper.COLUMN_DEADLINE, assignment.getDeadline());
        values.put(DatabaseHelper.COLUMN_REMINDER_TIME, assignment.getReminderTime());
        values.put(DatabaseHelper.COLUMN_PRIORITY, assignment.getPriority());
        values.put(DatabaseHelper.COLUMN_STATUS, assignment.getStatus());

        return database.insert(DatabaseHelper.TABLE_ASSIGNMENTS, null, values);
    }

    public int updateAssignment(Assignment assignment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_ID, assignment.getSubjectId());
        values.put(DatabaseHelper.COLUMN_TITLE, assignment.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, assignment.getDescription());
        values.put(DatabaseHelper.COLUMN_DEADLINE, assignment.getDeadline());
        values.put(DatabaseHelper.COLUMN_REMINDER_TIME, assignment.getReminderTime());
        values.put(DatabaseHelper.COLUMN_PRIORITY, assignment.getPriority());
        values.put(DatabaseHelper.COLUMN_STATUS, assignment.getStatus());

        return database.update(DatabaseHelper.TABLE_ASSIGNMENTS, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(assignment.getId())});
    }

    public void deleteAssignment(long id) {
        database.delete(DatabaseHelper.TABLE_ASSIGNMENTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Assignment getAssignment(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_ASSIGNMENTS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Assignment assignment = cursorToAssignment(cursor);
            cursor.close();
            return assignment;
        }
        return null;
    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_ASSIGNMENTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_DEADLINE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Assignment assignment = cursorToAssignment(cursor);
                assignments.add(assignment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return assignments;
    }

    public List<Assignment> getFilteredAssignments(Long subjectId, Integer priority, Integer status) {
        List<Assignment> assignments = new ArrayList<>();

        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (subjectId != null) {
            selection += DatabaseHelper.COLUMN_SUBJECT_ID + " = ?";
            selectionArgs.add(String.valueOf(subjectId));
        }

        if (priority != null) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += DatabaseHelper.COLUMN_PRIORITY + " = ?";
            selectionArgs.add(String.valueOf(priority));
        }

        if (status != null) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += DatabaseHelper.COLUMN_STATUS + " = ?";
            selectionArgs.add(String.valueOf(status));
        }

        Cursor cursor;
        if (selection.isEmpty()) {
            cursor = database.query(DatabaseHelper.TABLE_ASSIGNMENTS,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_DEADLINE + " ASC");
        } else {
            cursor = database.query(DatabaseHelper.TABLE_ASSIGNMENTS,
                    null, selection, selectionArgs.toArray(new String[0]),
                    null, null, DatabaseHelper.COLUMN_DEADLINE + " ASC");
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Assignment assignment = cursorToAssignment(cursor);
                assignments.add(assignment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return assignments;
    }

    private Assignment cursorToAssignment(Cursor cursor) {
        Assignment assignment = new Assignment();
        assignment.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        assignment.setSubjectId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBJECT_ID)));
        assignment.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
        assignment.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
        assignment.setDeadline(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DEADLINE)));
        assignment.setReminderTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REMINDER_TIME)));
        assignment.setPriority(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRIORITY)));
        assignment.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)));
        assignment.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));

        // Get subject name for display
        subjectDAO.open();
        String subjectName = subjectDAO.getSubject(assignment.getSubjectId()).getSubjectName();
        subjectDAO.close();

        assignment.setSubjectName(subjectName);

        return assignment;
    }
}
