package com.example.taskmanager.screen.note;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.R;
import com.example.taskmanager.model.Note;
import com.example.taskmanager.screen.DatabaseHelper;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    Context context;

    private onLongClickItem interfaces;

    private final List<Note> notes;

    public NoteAdapter(List<Note> notes, Context context) {
        this.context = context;
        this.notes = notes;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteContent, tvNoteCreatedAt,tvTitle;
        LinearLayout Itemview;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            tvNoteCreatedAt = itemView.findViewById(R.id.tvNoteCreatedAt);
            Itemview = itemView.findViewById(R.id.itemView);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        String title = new DatabaseHelper(context).getSubjectNameByCode(String.valueOf(note.getSubjectId()), PublicConstants.user.getId());
        if (title!= null) {
            holder.tvTitle.setText(title);
        } else {
            holder.tvTitle.setVisibility(GONE);
        }
        holder.tvNoteContent.setText(note.getContent());
        holder.tvNoteCreatedAt.setText(note.getCreatedAt());
        holder.Itemview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interfaces.onLongClick(note);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    interface onLongClickItem {
        void onLongClick(Note note);
    }

    public void setInterfaces(onLongClickItem interfaces) {
        this.interfaces = interfaces;
    }
}