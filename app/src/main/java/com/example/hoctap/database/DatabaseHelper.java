package com.example.hoctap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hoctap.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_SUBJECTS = "subjects";
    public static final String TABLE_TIMETABLE = "timetable";
    public static final String TABLE_ASSIGNMENTS = "assignments";
    public static final String TABLE_SCORES = "scores";
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_TODO = "todo";

    // Common Column Names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUBJECT_ID = "subject_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Subjects Table Columns
    public static final String COLUMN_SUBJECT_CODE = "subject_code";
    public static final String COLUMN_SUBJECT_NAME = "subject_name";
    public static final String COLUMN_SEMESTER = "semester";

    // Timetable Table Columns
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_LOCATION = "location";

    // Assignments Table Columns
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_REMINDER_TIME = "reminder_time";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_STATUS = "status";

    // Scores Table Columns
    public static final String COLUMN_SCORE_TYPE = "score_type";
    public static final String COLUMN_SCORE_VALUE = "score_value";
    public static final String COLUMN_SCORE_WEIGHT = "score_weight";

    // Notes Table - uses common columns

    // Todo Table Columns
    public static final String COLUMN_COMPLETED = "completed";

    // Create Table Statements
    private static final String CREATE_SUBJECTS_TABLE = "CREATE TABLE " + TABLE_SUBJECTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SUBJECT_CODE + " TEXT NOT NULL, "
            + COLUMN_SUBJECT_NAME + " TEXT NOT NULL, "
            + COLUMN_SEMESTER + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TIMETABLE_TABLE = "CREATE TABLE " + TABLE_TIMETABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SUBJECT_ID + " INTEGER, "
            + COLUMN_DAY_OF_WEEK + " INTEGER NOT NULL, "
            + COLUMN_START_TIME + " TEXT NOT NULL, "
            + COLUMN_END_TIME + " TEXT NOT NULL, "
            + COLUMN_LOCATION + " TEXT, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COLUMN_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE " + TABLE_ASSIGNMENTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SUBJECT_ID + " INTEGER, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_DEADLINE + " DATETIME NOT NULL, "
            + COLUMN_REMINDER_TIME + " DATETIME, "
            + COLUMN_PRIORITY + " INTEGER DEFAULT 0, "
            + COLUMN_STATUS + " INTEGER DEFAULT 0, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COLUMN_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SUBJECT_ID + " INTEGER, "
            + COLUMN_SCORE_TYPE + " TEXT NOT NULL, "
            + COLUMN_SCORE_VALUE + " REAL NOT NULL, "
            + COLUMN_SCORE_WEIGHT + " REAL DEFAULT 1.0, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COLUMN_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SUBJECT_ID + " INTEGER, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COLUMN_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_COMPLETED + " INTEGER DEFAULT 0, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SUBJECTS_TABLE);
        db.execSQL(CREATE_TIMETABLE_TABLE);
        db.execSQL(CREATE_ASSIGNMENTS_TABLE);
        db.execSQL(CREATE_SCORES_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMETABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);

        // Create tables again
        onCreate(db);
    }
}

