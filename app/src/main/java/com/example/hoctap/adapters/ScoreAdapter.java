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
import com.example.hoctap.activities.EditScoreActivity;
import com.example.hoctap.models.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private Context context;
    private List<Score> scores;

    public ScoreAdapter(Context context) {
        this.context = context;
        this.scores = new ArrayList<>();
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score score = scores.get(position);

        holder.tvScoreType.setText(score.getScoreType());
        holder.tvSubject.setText(score.getSubjectName());
        holder.tvScoreValue.setText(String.format(Locale.getDefault(), "%.1f", score.getScoreValue()));
        holder.tvScoreWeight.setText(String.format(Locale.getDefault(), "Weight: %.1f", score.getScoreWeight()));

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditScoreActivity.class);
            intent.putExtra("score_id", score.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvScoreType, tvSubject, tvScoreValue, tvScoreWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScoreType = itemView.findViewById(R.id.tvScoreType);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvScoreValue = itemView.findViewById(R.id.tvScoreValue);
            tvScoreWeight = itemView.findViewById(R.id.tvScoreWeight);
        }
    }
}

