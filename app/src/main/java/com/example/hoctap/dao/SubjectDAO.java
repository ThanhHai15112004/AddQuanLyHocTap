package com.example.hoctap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hoctap.database.DatabaseHelper;
import com.example.hoctap.models.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public SubjectDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertSubject(Subject subject) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_CODE, subject.getSubjectCode());
        values.put(DatabaseHelper.COLUMN_SUBJECT_NAME, subject.getSubjectName());
        values.put(DatabaseHelper.COLUMN_SEMESTER, subject.getSemester());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, subject.getDescription());

        return database.insert(DatabaseHelper.TABLE_SUBJECTS, null, values);
    }

    public int updateSubject(Subject subject) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_CODE, subject.getSubjectCode());
        values.put(DatabaseHelper.COLUMN_SUBJECT_NAME, subject.getSubjectName());
        values.put(DatabaseHelper.COLUMN_SEMESTER, subject.getSemester());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, subject.getDescription());

        return database.update(DatabaseHelper.TABLE_SUBJECTS, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(subject.getId())});
    }

    public void deleteSubject(long id) {
        database.delete(DatabaseHelper.TABLE_SUBJECTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Subject getSubject(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_SUBJECTS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Subject subject = cursorToSubject(cursor);
            cursor.close();
            return subject;
        }
        return null;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_SUBJECTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_SUBJECT_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Subject subject = cursorToSubject(cursor);
                subjects.add(subject);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return subjects;
    }

    public List<Subject> getSubjectsBySemester(String semester) {
        List<Subject> subjects = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_SUBJECTS,
                null,
                DatabaseHelper.COLUMN_SEMESTER + " = ?",
                new String[]{semester},
                null, null,
                DatabaseHelper.COLUMN_SUBJECT_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Subject subject = cursorToSubject(cursor);
                subjects.add(subject);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return subjects;
    }

    private Subject cursorToSubject(Cursor cursor) {
        Subject subject = new Subject();
        subject.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        subject.setSubjectCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBJECT_CODE)));
        subject.setSubjectName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBJECT_NAME)));
        subject.setSemester(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SEMESTER)));
        subject.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
        subject.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return subject;
    }
}

