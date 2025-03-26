package com.example.hoctap.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.activities.EditNoteActivity;
import com.example.hoctap.models.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private final Context context;
    private List<Note> notes;
    private final SimpleDateFormat inputFormat;
    private final SimpleDateFormat outputFormat;

    public NoteAdapter(Context context) {
        this.context = context;
        this.notes = new ArrayList<>();
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvSubject.setText(note.getSubjectName());

        String description = note.getDescription();
        if (description != null && !description.isEmpty()) {
            holder.tvPreview.setText(description);
            holder.tvPreview.setVisibility(View.VISIBLE);
        } else {
            holder.tvPreview.setVisibility(View.GONE);
        }

        String createdAt = note.getCreatedAt();
        if (createdAt != null) {
            try {
                Date createdDate = inputFormat.parse(createdAt);
                holder.tvDate.setText(outputFormat.format(createdDate));
            } catch (ParseException e) {
                holder.tvDate.setText(createdAt); // Fallback to raw string
                e.printStackTrace(); // Log error for debugging
            }
        } else {
            holder.tvDate.setText("N/A");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditNoteActivity.class);
            intent.putExtra("note_id", note.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubject, tvPreview, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvPreview = itemView.findViewById(R.id.tvPreview);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}