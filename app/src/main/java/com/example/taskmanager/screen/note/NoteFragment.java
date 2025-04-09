package com.example.taskmanager.screen.note;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.R;
import com.example.taskmanager.databinding.FragmentNoteBinding;
import com.example.taskmanager.model.Note;
import com.example.taskmanager.screen.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteFragment extends Fragment implements NoteAdapter.onLongClickItem {
    NoteAdapter adapter ;
    List<Note> allNote = new ArrayList<>();
    DatabaseHelper db;
    FragmentNoteBinding binding;

    public NoteFragment() {
    }

    public static NoteFragment newInstance(String param1, String param2) {
        return new NoteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());
        allNote = db.getNotesByUserId(PublicConstants.user.getId());
        adapter = new NoteAdapter(allNote,getContext());
        adapter.setInterfaces(this);
        binding.rvListNote.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListNote.setAdapter(adapter);

        binding.btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteDialog();
            }
        });
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm ghi chú");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        builder.setView(view);

        EditText edtNoteContent = view.findViewById(R.id.edtNoteContent);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String content = edtNoteContent.getText().toString().trim();
            long userId = PublicConstants.user.getId();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Nội dung không được để trống", Toast.LENGTH_LONG).show();
                return;
            }

            Note newNote = new Note(userId, "0", content, createdAt);
            db.addNote(newNote);
            Toast.makeText(getContext(), "Đã thêm ghi chú", Toast.LENGTH_SHORT).show();
            loadNotes();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onLongClick(Note note) {
        showEditOrDeleteNoteDialog(note);
    }

    private void showEditOrDeleteNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa ghi chú");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_note, null);
        builder.setView(view);

        EditText edtContent = view.findViewById(R.id.edtNoteContent);

        edtContent.setText(note.getContent());

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newContent = edtContent.getText().toString().trim();
            if (!newContent.isEmpty()) {
                note.setContent(newContent);
                note.setCreatedAt(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
                db.updateNote(note);
                loadNotes();
            } else {
                Toast.makeText(getContext(), "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Xoá", (dialog, which) -> {
            db.deleteNote(note.getId());
            loadNotes();
            Toast.makeText(getContext(), "Đã xoá ghi chú", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Huỷ", null);

        builder.show();
    }

    private void loadNotes() {
        allNote = db.getNotesByUserId(PublicConstants.user.getId());
        adapter = new NoteAdapter(allNote,getContext());
        adapter.setInterfaces(this);
        binding.rvListNote.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListNote.setAdapter(adapter);
    }

}