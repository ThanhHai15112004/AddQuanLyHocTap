package com.example.hoctap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hoctap.database.DatabaseHelper;
import com.example.hoctap.models.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private SubjectDAO subjectDAO;

    public NoteDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        subjectDAO = new SubjectDAO(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_ID, note.getSubjectId());
        values.put(DatabaseHelper.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, note.getDescription());

        return database.insert(DatabaseHelper.TABLE_NOTES, null, values);
    }

    public int updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_ID, note.getSubjectId());
        values.put(DatabaseHelper.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, note.getDescription());

        return database.update(DatabaseHelper.TABLE_NOTES, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(long id) {
        database.delete(DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Note getNote(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Note note = cursorToNote(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Note note = cursorToNote(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return notes;
    }

    public List<Note> getNotesBySubject(long subjectId) {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                null,
                DatabaseHelper.COLUMN_SUBJECT_ID + " = ?",
                new String[]{String.valueOf(subjectId)},
                null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Note note = cursorToNote(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        note.setSubjectId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBJECT_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
        note.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
        note.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));

        // Get subject name for display
        subjectDAO.open();
        String subjectName = subjectDAO.getSubject(note.getSubjectId()).getSubjectName();
        subjectDAO.close();

        note.setSubjectName(subjectName);

        return note;
    }
}

