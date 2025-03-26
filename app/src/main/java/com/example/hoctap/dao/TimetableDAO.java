package com.example.hoctap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hoctap.database.DatabaseHelper;
import com.example.hoctap.models.TimetableEntry;

import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private SubjectDAO subjectDAO;

    public TimetableDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        subjectDAO = new SubjectDAO(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTimetableEntry(TimetableEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_ID, entry.getSubjectId());
        values.put(DatabaseHelper.COLUMN_DAY_OF_WEEK, entry.getDayOfWeek());
        values.put(DatabaseHelper.COLUMN_START_TIME, entry.getStartTime());
        values.put(DatabaseHelper.COLUMN_END_TIME, entry.getEndTime());
        values.put(DatabaseHelper.COLUMN_LOCATION, entry.getLocation());

        return database.insert(DatabaseHelper.TABLE_TIMETABLE, null, values);
    }

    public int updateTimetableEntry(TimetableEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SUBJECT_ID, entry.getSubjectId());
        values.put(DatabaseHelper.COLUMN_DAY_OF_WEEK, entry.getDayOfWeek());
        values.put(DatabaseHelper.COLUMN_START_TIME, entry.getStartTime());
        values.put(DatabaseHelper.COLUMN_END_TIME, entry.getEndTime());
        values.put(DatabaseHelper.COLUMN_LOCATION, entry.getLocation());

        return database.update(DatabaseHelper.TABLE_TIMETABLE, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
    }

    public void deleteTimetableEntry(long id) {
        database.delete(DatabaseHelper.TABLE_TIMETABLE,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public TimetableEntry getTimetableEntry(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_TIMETABLE,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            TimetableEntry entry = cursorToTimetableEntry(cursor);
            cursor.close();
            return entry;
        }
        return null;
    }

    public List<TimetableEntry> getAllTimetableEntries() {
        List<TimetableEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_TIMETABLE,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_DAY_OF_WEEK + " ASC, " +
                        DatabaseHelper.COLUMN_START_TIME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TimetableEntry entry = cursorToTimetableEntry(cursor);
                entries.add(entry);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return entries;
    }

    public List<TimetableEntry> getTimetableEntriesByDay(int dayOfWeek) {
        List<TimetableEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_TIMETABLE,
                null,
                DatabaseHelper.COLUMN_DAY_OF_WEEK + " = ?",
                new String[]{String.valueOf(dayOfWeek)},
                null, null,
                DatabaseHelper.COLUMN_START_TIME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TimetableEntry entry = cursorToTimetableEntry(cursor);

                // Get subject name for display
                subjectDAO.open();
                String subjectName = subjectDAO.getSubject(entry.getSubjectId()).getSubjectName();
                subjectDAO.close();

                entry.setSubjectName(subjectName);
                entries.add(entry);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return entries;
    }

    private TimetableEntry cursorToTimetableEntry(Cursor cursor) {
        TimetableEntry entry = new TimetableEntry();
        entry.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        entry.setSubjectId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBJECT_ID)));
        entry.setDayOfWeek(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY_OF_WEEK)));
        entry.setStartTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME)));
        entry.setEndTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME)));
        entry.setLocation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION)));
        entry.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return entry;
    }
}
