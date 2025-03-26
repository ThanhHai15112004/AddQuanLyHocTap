package com.example.hoctap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.hoctap.database.DatabaseHelper;
import com.example.hoctap.models.Score;
import com.example.hoctap.models.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreDAO {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final SubjectDAO subjectDAO;

    public ScoreDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        subjectDAO = new SubjectDAO(context);
    }

    public void open() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (database != null && database.isOpen()) {
            dbHelper.close();
        }
    }

    public long insertScore(Score score) {
        if (score == null || score.getScoreValue() < 0 || score.getScoreWeight() < 0) {
            return -1; // Invalid input
        }
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_SUBJECT_ID, score.getSubjectId());
            values.put(DatabaseHelper.COLUMN_SCORE_TYPE, score.getScoreType());
            values.put(DatabaseHelper.COLUMN_SCORE_VALUE, score.getScoreValue());
            values.put(DatabaseHelper.COLUMN_SCORE_WEIGHT, score.getScoreWeight());
            return database.insert(DatabaseHelper.TABLE_SCORES, null, values);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }
    }

    public int updateScore(Score score) {
        if (score == null || score.getScoreValue() < 0 || score.getScoreWeight() < 0) {
            return -1; // Invalid input
        }
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_SUBJECT_ID, score.getSubjectId());
            values.put(DatabaseHelper.COLUMN_SCORE_TYPE, score.getScoreType());
            values.put(DatabaseHelper.COLUMN_SCORE_VALUE, score.getScoreValue());
            values.put(DatabaseHelper.COLUMN_SCORE_WEIGHT, score.getScoreWeight());
            return database.update(DatabaseHelper.TABLE_SCORES, values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(score.getId())});
        } catch (SQLiteException e) {
            e.printStackTrace();
            return -1;
        } finally {
            close();
        }
    }

    public void deleteScore(long id) {
        open();
        try {
            database.delete(DatabaseHelper.TABLE_SCORES,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public Score getScore(long id) {
        open();
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.TABLE_SCORES,
                    null,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToScore(cursor);
            }
            return null;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            close();
        }
    }

    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();
        open();
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.TABLE_SCORES,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_CREATED_AT + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    scores.add(cursorToScore(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            close();
        }
        return scores;
    }

    public List<Score> getScoresBySubject(long subjectId) {
        List<Score> scores = new ArrayList<>();
        open();
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.TABLE_SCORES,
                    null,
                    DatabaseHelper.COLUMN_SUBJECT_ID + " = ?",
                    new String[]{String.valueOf(subjectId)},
                    null, null,
                    DatabaseHelper.COLUMN_CREATED_AT + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    scores.add(cursorToScore(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            close();
        }
        return scores;
    }

    public List<Score> getScoresBySemester(String semester) {
        List<Score> scores = new ArrayList<>();
        open();
        try {
            subjectDAO.open();
            List<Subject> subjects = subjectDAO.getSubjectsBySemester(semester);
            subjectDAO.close();

            if (subjects == null || subjects.isEmpty()) {
                return scores; // Return empty list if no subjects
            }

            // Use a single query with IN clause for better performance
            String subjectIds = subjects.stream()
                    .map(subject -> String.valueOf(subject.getId()))
                    .collect(Collectors.joining(","));
            Cursor cursor = database.query(DatabaseHelper.TABLE_SCORES,
                    null,
                    DatabaseHelper.COLUMN_SUBJECT_ID + " IN (" + subjectIds + ")",
                    null, null, null,
                    DatabaseHelper.COLUMN_CREATED_AT + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    scores.add(cursorToScore(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return scores;
    }

    public double getAverageScoreBySubject(long subjectId) {
        List<Score> scores = getScoresBySubject(subjectId);
        return calculateAverageScore(scores);
    }

    public double getAverageScoreBySemester(String semester) {
        List<Score> scores = getScoresBySemester(semester);
        return calculateAverageScore(scores);
    }

    public double getOverallAverageScore() {
        List<Score> scores = getAllScores();
        return calculateAverageScore(scores);
    }

    private double calculateAverageScore(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0;
        }
        double totalWeightedScore = 0;
        double totalWeight = 0;
        for (Score score : scores) {
            totalWeightedScore += score.getScoreValue() * score.getScoreWeight();
            totalWeight += score.getScoreWeight();
        }
        return totalWeight == 0 ? 0 : totalWeightedScore / totalWeight;
    }

    private Score cursorToScore(Cursor cursor) {
        Score score = new Score();
        score.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        score.setSubjectId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBJECT_ID)));
        score.setScoreType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE_TYPE)));
        score.setScoreValue(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE_VALUE)));
        score.setScoreWeight(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE_WEIGHT)));
        score.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)));

        try {
            subjectDAO.open();
            Subject subject = subjectDAO.getSubject(score.getSubjectId());
            score.setSubjectName(subject != null ? subject.getSubjectName() : "Unknown");
        } catch (Exception e) {
            score.setSubjectName("Error");
            e.printStackTrace();
        } finally {
            subjectDAO.close();
        }

        return score;
    }
}