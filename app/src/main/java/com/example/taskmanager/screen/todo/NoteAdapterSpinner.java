package com.example.taskmanager.screen.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.model.Note;
import com.example.taskmanager.screen.DatabaseHelper;

import java.util.List;

public class NoteAdapterSpinner extends ArrayAdapter<Note> {

    private Context context;
    private List<Note> notes;

    public NoteAdapterSpinner(@NonNull Context context, @NonNull List<Note> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        this.context = context;
        this.notes = objects;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        Note note = notes.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);

        // Hiển thị subject và content của Note
        String nameSubject = new DatabaseHelper(context).getSubjectNameByCode(String.valueOf(note.getSubjectId()), PublicConstants.user.getId());
        String displayText = "Subject: " + nameSubject + "---" + "Nội Dung: " + note.getContent();
        textView.setText(displayText);

        return convertView;
    }
}

