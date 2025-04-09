package com.example.taskmanager.screen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskmanager.model.Assignment;
import com.example.taskmanager.model.Grade;
import com.example.taskmanager.model.Note;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.model.Schedule;
import com.example.taskmanager.model.Subject;
import com.example.taskmanager.model.Todo;
import com.example.taskmanager.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "school_management.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_USERS_TABLE = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT UNIQUE NOT NULL," +
            "password_hash TEXT NOT NULL," +
            "full_name TEXT" +
            ");";

    private static final String CREATE_SUBJECTS_TABLE = "CREATE TABLE subjects (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "name TEXT NOT NULL," +
            "code TEXT," +
            "color_label TEXT," +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ");";

    private static final String CREATE_SCHEDULES_TABLE = "CREATE TABLE schedules (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "subject_id TEXT," +
            "title TEXT," +
            "description TEXT," +
            "day_of_week INTEGER CHECK (day_of_week BETWEEN 0 AND 6)," +
            "start_time TEXT," +
            "end_time TEXT," +
            "location TEXT," +
            "repeat_type TEXT," +
            "FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE" +
            ");";

    private static final String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE assignments (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "subject_id TEXT," +
            "title TEXT NOT NULL," +
            "description TEXT," +
            "due_date TEXT," +
            "priority_level TEXT CHECK (priority_level IN ('low', 'medium', 'high'))," +
            "status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'completed'))," +
            "FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE" +
            ");";

    private static final String CREATE_GRADES_TABLE = "CREATE TABLE grades (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "subject_id TEXT," +
            "title TEXT," +
            "score REAL," +
            "max_score REAL," +
            "grade_type TEXT," +
            "date TEXT," +
            "FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE" +
            ");";

    private static final String CREATE_NOTES_TABLE = "CREATE TABLE notes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "subject_id TEXT," +
            "content TEXT," +
            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
            "FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE SET NULL" +
            ");";

    private static final String CREATE_TODOS_TABLE = "CREATE TABLE todos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "title TEXT," +
            "is_done BOOLEAN DEFAULT FALSE," +
            "due_date TEXT," +
            "note_id INTEGER," +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
            "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE SET NULL" +
            ");";

    private static final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE notifications (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "assignment_id INTEGER," +
            "schedule_id INTEGER," +
            "remind_at TEXT," +
            "message TEXT," +
            "is_sent BOOLEAN DEFAULT FALSE," +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
            "FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE SET NULL," +
            "FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE SET NULL" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_SUBJECTS_TABLE);
        db.execSQL(CREATE_SCHEDULES_TABLE);
        db.execSQL(CREATE_ASSIGNMENTS_TABLE);
        db.execSQL(CREATE_GRADES_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_TODOS_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notifications");
        db.execSQL("DROP TABLE IF EXISTS todos");
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS grades");
        db.execSQL("DROP TABLE IF EXISTS assignments");
        db.execSQL("DROP TABLE IF EXISTS schedules");
        db.execSQL("DROP TABLE IF EXISTS subjects");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put("email", user.getEmail());
        values.put("password_hash", user.getPasswordHash());
        values.put("full_name", user.getFullName());

        return db.insert("users", null, values);
    }

    public User getUserByEmail(String email) {
        if (db == null || !db.isOpen()) {
            db = this.getReadableDatabase();
        }

        String query = "SELECT * FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String emailValue = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));

            user = new User(id, emailValue, passwordHash, fullName);
            cursor.close();
        }

        return user;
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("email", user.getEmail());
        values.put("full_name", user.getFullName());

        return db.update("users", values, "id = ?", new String[]{String.valueOf(user.getId())});
    }

    public int deleteUser(long userId) {
        return db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
    }

    public long addSubject(Subject subject) {
        ContentValues values = new ContentValues();
        values.put("user_id", subject.getUserId());
        values.put("name", subject.getName());
        values.put("code", subject.getCode());
        values.put("color_label", subject.getColorLabel());

        return db.insert("subjects", null, values);
    }
    public List<Subject> getSubjectsByUserId(long userId) {
        List<Subject> subjectList = new ArrayList<>();
        String query = "SELECT * FROM subjects WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
                String colorLabel = cursor.getString(cursor.getColumnIndexOrThrow("color_label"));

                Subject subject = new Subject(id, userId, name, code, colorLabel);
                subjectList.add(subject);
            }
            cursor.close();
        }

        return subjectList;
    }

    public String getSubjectNameByCode(String code, long userId) {
        String query = "SELECT name FROM subjects WHERE code = ? AND user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{code, String.valueOf(userId)});

        String name = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
            cursor.close();
        }
        return name;
    }



    public int updateSubject(Subject subject) {
        ContentValues values = new ContentValues();
        values.put("name", subject.getName());
        values.put("code", subject.getCode());
        values.put("color_label", subject.getColorLabel());

        return db.update("subjects", values, "id = ?", new String[]{String.valueOf(subject.getId())});
    }

    public int deleteSubject(long subjectId) {
        return db.delete("subjects", "id = ?", new String[]{String.valueOf(subjectId)});
    }

    public long addSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put("subject_id", schedule.getSubjectId());
        values.put("title", schedule.getTitle());
        values.put("description", schedule.getDescription());
        values.put("day_of_week", schedule.getDayOfWeek());
        values.put("start_time", schedule.getStartTime());
        values.put("end_time", schedule.getEndTime());
        values.put("location", schedule.getLocation());
        values.put("repeat_type", schedule.getRepeatType());

        return db.insert("schedules", null, values);
    }

    public List<Schedule> getSchedulesByUserId(long userId) {
        List<Schedule> res = new ArrayList<>();
        List<Subject> subjects = getSubjectsByUserId(userId);

        if (subjects.isEmpty()) {
            return res;
        }

        for (Subject subject : subjects) {
            String query = "SELECT * FROM schedules WHERE subject_id = ?";
            Cursor cursor = db.rawQuery(query, new String[]{subject.getCode()});

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String subjectId = cursor.getString(cursor.getColumnIndexOrThrow("subject_id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    int dayOfWeek = cursor.getInt(cursor.getColumnIndexOrThrow("day_of_week"));
                    String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                    String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                    String repeatType = cursor.getString(cursor.getColumnIndexOrThrow("repeat_type"));

                    Schedule schedule = new Schedule(id, subjectId, title, description, dayOfWeek, startTime, endTime, location, repeatType);
                    res.add(schedule);
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return res;
    }


    public int updateSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put("subject_id", schedule.getSubjectId());
        values.put("title", schedule.getTitle());
        values.put("description", schedule.getDescription());
        values.put("day_of_week", schedule.getDayOfWeek());
        values.put("start_time", schedule.getStartTime());
        values.put("end_time", schedule.getEndTime());
        values.put("location", schedule.getLocation());
        values.put("repeat_type", schedule.getRepeatType());

        return db.update("schedules", values, "id = ?", new String[]{String.valueOf(schedule.getId())});
    }

    public int deleteSchedule(long scheduleId) {
        return db.delete("schedules", "id = ?", new String[]{String.valueOf(scheduleId)});
    }

    public long addAssignment(Assignment assignment) {
        ContentValues values = new ContentValues();
        values.put("subject_id", assignment.getSubjectId());
        values.put("title", assignment.getTitle());
        values.put("description", assignment.getDescription());
        values.put("due_date", assignment.getDueDate());
        values.put("priority_level", assignment.getPriorityLevel());

        return db.insert("assignments", null, values);
    }

    public List<Assignment> getAssignmentsBySubjectId(String subjectId) {
        List<Assignment> assignments = new ArrayList<>();

        String query = "SELECT * FROM assignments WHERE subject_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(subjectId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String subjectIdFromDb = cursor.getString(cursor.getColumnIndexOrThrow("subject_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
                String priorityLevel = cursor.getString(cursor.getColumnIndexOrThrow("priority_level"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                Assignment assignment = new Assignment(id, subjectIdFromDb, title, description, dueDate, priorityLevel, status);
                assignments.add(assignment);
            }
            cursor.close();
        }

        return assignments;
    }

    public Assignment getAssignmentById(long id) {
        Assignment assignment = null;

        // Query to fetch a specific assignment by its ID
        String query = "SELECT * FROM assignments WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Extract data from the cursor
                String subjectIdFromDb = cursor.getString(cursor.getColumnIndexOrThrow("subject_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
                String priorityLevel = cursor.getString(cursor.getColumnIndexOrThrow("priority_level"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                // Create the Assignment object from the cursor data
                assignment = new Assignment(id, subjectIdFromDb, title, description, dueDate, priorityLevel, status);
            }
            cursor.close();
        }

        return assignment; // Return the Assignment or null if not found
    }


    public int updateAssignment(long assignmentId, String newTitle, String newDescription, String newDueDate, String newPriorityLevel) {
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("description", newDescription);
        values.put("due_date", newDueDate);
        values.put("priority_level", newPriorityLevel);

        return db.update("assignments", values, "id = ?", new String[]{String.valueOf(assignmentId)});
    }

    public int deleteAssignment(long assignmentId) {
        return db.delete("assignments", "id = ?", new String[]{String.valueOf(assignmentId)});
    }

    public long addGrade(Grade grade) {
        ContentValues values = new ContentValues();
        values.put("subject_id", grade.getSubjectId());
        values.put("title", grade.getTitle());
        values.put("score", grade.getScore());
        values.put("max_score", grade.getMaxScore());
        values.put("grade_type", grade.getGradeType());
        values.put("date", grade.getDate());

        return db.insert("grades", null, values);
    }

    public List<Grade> getGradeListBySubjectId(String subjectId) {
        List<Grade> grades = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM grades WHERE subject_id = ? ORDER BY date ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(subjectId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String subId = cursor.getString(cursor.getColumnIndexOrThrow("subject_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                double score = cursor.getDouble(cursor.getColumnIndexOrThrow("score"));
                double maxScore = cursor.getDouble(cursor.getColumnIndexOrThrow("max_score"));
                String gradeType = cursor.getString(cursor.getColumnIndexOrThrow("grade_type"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                Grade grade = new Grade(id, subId, title, score, maxScore, gradeType, date);
                grades.add(grade);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return grades;
    }



    public int updateGrade(long gradeId, float newScore, String newGradeType) {
        ContentValues values = new ContentValues();
        values.put("score", newScore);
        values.put("grade_type", newGradeType);

        return db.update("grades", values, "id = ?", new String[]{String.valueOf(gradeId)});
    }

    public int deleteGrade(long gradeId) {
        return db.delete("grades", "id = ?", new String[]{String.valueOf(gradeId)});
    }

    public long addNote(Note newNote) {
        ContentValues values = new ContentValues();
        values.put("user_id", newNote.getUserId());
        values.put("subject_id", newNote.getSubjectId());
        values.put("content", newNote.getContent());

        return db.insert("notes", null, values);
    }


    public List<Note> getNotesByUserId(long userId) {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM notes WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String subjectId = cursor.getString(cursor.getColumnIndexOrThrow("subject_id"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                Note note = new Note(id, userId, subjectId, content, createdAt);
                notes.add(note);
            }
            cursor.close();
        }
        return notes;
    }

    public Note getNoteByNoteId(long noteId) {
        Note note = null;
        String query = "SELECT * FROM notes WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(noteId)});

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
            String subjectId = cursor.getString(cursor.getColumnIndexOrThrow("subject_id"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

            note = new Note(id, userId, subjectId, content, createdAt);
            cursor.close();
        }
        return note;
    }



    public int updateNote(Note newNote) {
        ContentValues values = new ContentValues();
        values.put("content", newNote.getContent());
        values.put("subject_id", newNote.getSubjectId());

        return db.update("notes", values, "id = ?", new String[]{String.valueOf(newNote.getId())});
    }

    public int deleteNote(long noteId) {
        return db.delete("notes", "id = ?", new String[]{String.valueOf(noteId)});
    }

    public long addTodo(Todo newTodo) {
        ContentValues values = new ContentValues();
        values.put("user_id", newTodo.getUserId());
        values.put("title", newTodo.getTitle());
        values.put("is_done", newTodo.isDone() ? 1 : 0);
        values.put("due_date", newTodo.getDueDate());
        values.put("note_id", newTodo.getNoteId());

        return db.insert("todos", null, values);
    }

    public List<Todo> getTodosByUserId(long userId) {
        List<Todo> todos = new ArrayList<>();
        String query = "SELECT * FROM todos WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                long noteId = cursor.getLong(cursor.getColumnIndexOrThrow("note_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done")) == 1;
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
                long userIdFromDb = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));

                Todo todo = new Todo(id, userIdFromDb, title, isDone, dueDate, noteId);
                todos.add(todo);
            }
            cursor.close();
        }
        return todos;
    }


    public int updateTodo(Todo todo) {
        ContentValues values = new ContentValues();
        values.put("title", todo.getTitle());
        values.put("is_done", todo.isDone() ? 1 : 0);  // Boolean to int (1 for true, 0 for false)
        values.put("due_date", todo.getDueDate());
        values.put("note_id", todo.getNoteId());

        return db.update("todos", values, "id = ?", new String[]{String.valueOf(todo.getId())});
    }


    public int deleteTodo(long todoId) {
        return db.delete("todos", "id = ?", new String[]{String.valueOf(todoId)});
    }

    public long addNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", notification.getUserId());
        values.put("assignment_id", notification.getAssignmentId());
        values.put("schedule_id", notification.getScheduleId());
        values.put("remind_at", notification.getRemindAt());
        values.put("message", notification.getMessage());
        values.put("is_sent", notification.isSent() ? 1 : 0);

        return db.insert("notifications", null, values);
    }

    public Cursor getNotificationsByUserId(long userId) {
        String query = "SELECT * FROM notifications WHERE user_id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public int updateNotification(long notificationId, String newMessage) {
        ContentValues values = new ContentValues();
        values.put("message", newMessage);

        return db.update("notifications", values, "id = ?", new String[]{String.valueOf(notificationId)});
    }

    public int updateNotificationStatussent(long notificationId) {
        ContentValues values = new ContentValues();
        values.put("is_sent ", 1);

        return db.update("notifications", values, "assignment_id = ?", new String[]{String.valueOf(notificationId)});
    }

    public int deleteNotification(long notificationId) {
        return db.delete("notifications", "id = ?", new String[]{String.valueOf(notificationId)});
    }

}
