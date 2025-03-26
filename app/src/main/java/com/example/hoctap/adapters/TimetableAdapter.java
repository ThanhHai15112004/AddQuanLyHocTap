package com.example.hoctap.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.activities.EditTimetableEntryActivity;
import com.example.hoctap.models.TimetableEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private final Context context;
    private List<TimetableEntry> timetableEntries;
    private final boolean isListView;
    private OnItemClickListener listener;
    private final SimpleDateFormat timeFormat;
    private final int[] subjectColors;

    public interface OnItemClickListener {
        void onItemClick(TimetableEntry entry);
        void onLongClick(TimetableEntry entry, int position);
    }

    public TimetableAdapter(Context context, boolean isListView) {
        this.context = context;
        this.isListView = isListView;
        this.timetableEntries = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.subjectColors = new int[]{
                context.getResources().getColor(R.color.subject_1),
                context.getResources().getColor(R.color.subject_2),
                context.getResources().getColor(R.color.subject_3),
                context.getResources().getColor(R.color.subject_4),
                context.getResources().getColor(R.color.subject_5),
                context.getResources().getColor(R.color.subject_6),
                context.getResources().getColor(R.color.subject_7)
        };
    }

    public void setTimetableEntries(List<TimetableEntry> timetableEntries) {
        this.timetableEntries = timetableEntries != null ? timetableEntries : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addTimetableEntry(TimetableEntry entry) {
        if (entry != null) {
            timetableEntries.add(entry);
            sortEntriesByTime();
            notifyItemInserted(timetableEntries.size() - 1);
        }
    }

    public void updateTimetableEntry(TimetableEntry entry, int position) {
        if (entry != null && position >= 0 && position < timetableEntries.size()) {
            timetableEntries.set(position, entry);
            sortEntriesByTime();
            notifyItemChanged(position);
        }
    }

    public void removeTimetableEntry(int position) {
        if (position >= 0 && position < timetableEntries.size()) {
            timetableEntries.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, timetableEntries.size());
        }
    }

    public TimetableEntry getItem(int position) {
        if (position >= 0 && position < timetableEntries.size()) {
            return timetableEntries.get(position);
        }
        return null;
    }

    private void sortEntriesByTime() {
        timetableEntries.sort((entry1, entry2) -> {
            try {
                Date time1 = timeFormat.parse(entry1.getStartTime() != null ? entry1.getStartTime() : "00:00");
                Date time2 = timeFormat.parse(entry2.getStartTime() != null ? entry2.getStartTime() : "00:00");
                return time1.compareTo(time2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                isListView ? R.layout.item_timetable_list : R.layout.item_timetable_calendar,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimetableEntry entry = timetableEntries.get(position);

        if (isListView) {
            holder.tvSubjectName.setText(entry.getSubjectName() != null ? entry.getSubjectName() : "Unknown Subject");
            holder.tvTime.setText(String.format("%s - %s",
                    entry.getStartTime() != null ? entry.getStartTime() : "N/A",
                    entry.getEndTime() != null ? entry.getEndTime() : "N/A"));

            String location = entry.getLocation();
            if (location != null && !location.isEmpty()) {
                holder.tvLocation.setText(location);
                holder.tvLocation.setVisibility(View.VISIBLE);
            } else {
                holder.tvLocation.setVisibility(View.GONE);
            }

            int colorIndex = (int) (entry.getSubjectId() % subjectColors.length);
            holder.colorIndicator.setBackgroundColor(subjectColors[colorIndex]);

            String duration = calculateDuration(entry.getStartTime(), entry.getEndTime());
            holder.itemView.setContentDescription(
                    String.format("%s class from %s to %s, duration %s, in %s",
                            entry.getSubjectName() != null ? entry.getSubjectName() : "Unknown",
                            entry.getStartTime() != null ? entry.getStartTime() : "N/A",
                            entry.getEndTime() != null ? entry.getEndTime() : "N/A",
                            duration,
                            location != null && !location.isEmpty() ? location : "unknown location"));
        } else {
            holder.tvTimeSlot.setText(entry.getStartTime() != null ? entry.getStartTime() : "N/A");
            holder.tvSubjectName.setText(entry.getSubjectName() != null ? entry.getSubjectName() : "Unknown Subject");

            String location = entry.getLocation();
            if (location != null && !location.isEmpty()) {
                holder.tvLocation.setText(location);
                holder.tvLocation.setVisibility(View.VISIBLE);
            } else {
                holder.tvLocation.setVisibility(View.GONE);
            }

            int colorIndex = (int) (entry.getSubjectId() % subjectColors.length);
            int color = subjectColors[colorIndex];
            int lighterColor = lightenColor(color, 0.8f);
            holder.cardClass.setCardBackgroundColor(lighterColor);

            boolean isDarkColor = isDarkColor(lighterColor);
            int textColor = isDarkColor ? Color.WHITE : Color.BLACK;
            holder.tvSubjectName.setTextColor(textColor);
            holder.tvLocation.setTextColor(isDarkColor ? Color.LTGRAY : Color.DKGRAY);

            String duration = calculateDuration(entry.getStartTime(), entry.getEndTime());
            holder.itemView.setContentDescription(
                    String.format("%s class at %s, duration %s, in %s",
                            entry.getSubjectName() != null ? entry.getSubjectName() : "Unknown",
                            entry.getStartTime() != null ? entry.getStartTime() : "N/A",
                            duration,
                            location != null && !location.isEmpty() ? location : "unknown location"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(entry);
            } else {
                openEditActivity(entry);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(entry, holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    private void openEditActivity(TimetableEntry entry) {
        Intent intent = new Intent(context, EditTimetableEntryActivity.class);
        intent.putExtra("entry_id", entry.getId());
        context.startActivity(intent);
    }

    private String calculateDuration(String startTime, String endTime) {
        try {
            Date start = timeFormat.parse(startTime != null ? startTime : "00:00");
            Date end = timeFormat.parse(endTime != null ? endTime : "00:00");
            if (start != null && end != null) {
                long durationMillis = end.getTime() - start.getTime();
                long minutes = durationMillis / (60 * 1000);
                if (minutes < 60) {
                    return minutes + " minutes";
                } else {
                    long hours = minutes / 60;
                    long remainingMinutes = minutes % 60;
                    return hours + " hour" + (hours > 1 ? "s" : "") +
                            (remainingMinutes > 0 ? " " + remainingMinutes + " minute" + (remainingMinutes > 1 ? "s" : "") : "");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "unknown duration";
    }

    private int lightenColor(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) + 255 * factor));
        int green = (int) ((Color.green(color) * (1 - factor) + 255 * factor));
        int blue = (int) ((Color.blue(color) * (1 - factor) + 255 * factor));
        return Color.rgb(red, green, blue);
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    @Override
    public int getItemCount() {
        return timetableEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvTime, tvLocation, tvTimeSlot;
        View colorIndicator;
        CardView cardClass;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.tvTime) != null) { // List view
                tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                colorIndicator = itemView.findViewById(R.id.colorIndicator);
            } else { // Calendar view
                tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
                tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                cardClass = itemView.findViewById(R.id.cardClass);
            }
        }
    }

    public static int getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        return day < 0 ? 6 : day;
    }

    public static String getCurrentTimeString() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    public static boolean isClassInProgress(TimetableEntry entry) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date currentTime = timeFormat.parse(getCurrentTimeString());
            Date startTime = timeFormat.parse(entry.getStartTime() != null ? entry.getStartTime() : "00:00");
            Date endTime = timeFormat.parse(entry.getEndTime() != null ? entry.getEndTime() : "00:00");
            return currentTime != null && startTime != null && endTime != null &&
                    currentTime.after(startTime) && currentTime.before(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isClassUpcoming(TimetableEntry entry) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date currentTime = timeFormat.parse(getCurrentTimeString());
            Date startTime = timeFormat.parse(entry.getStartTime() != null ? entry.getStartTime() : "00:00");
            return currentTime != null && startTime != null &&
                    currentTime.before(startTime) && entry.getDayOfWeek() == getCurrentDayOfWeek();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void filterByDay(int day) {
        List<TimetableEntry> filteredList = new ArrayList<>();
        for (TimetableEntry entry : timetableEntries) {
            if (entry.getDayOfWeek() == day) {
                filteredList.add(entry);
            }
        }
        timetableEntries = filteredList;
        sortEntriesByTime();
        notifyDataSetChanged();
    }

    public void filterBySubject(long subjectId) {
        List<TimetableEntry> filteredList = new ArrayList<>();
        for (TimetableEntry entry : timetableEntries) {
            if (entry.getSubjectId() == subjectId) {
                filteredList.add(entry);
            }
        }
        timetableEntries = filteredList;
        sortEntriesByTime();
        notifyDataSetChanged();
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        for (TimetableEntry entry : timetableEntries) {
            String location = entry.getLocation();
            if (location != null && !location.isEmpty() && !locations.contains(location)) {
                locations.add(location);
            }
        }
        return locations;
    }

    public String calculateTotalHoursPerWeek() {
        double totalMinutes = 0;
        for (TimetableEntry entry : timetableEntries) {
            try {
                Date startTime = timeFormat.parse(entry.getStartTime() != null ? entry.getStartTime() : "00:00");
                Date endTime = timeFormat.parse(entry.getEndTime() != null ? entry.getEndTime() : "00:00");
                if (startTime != null && endTime != null) {
                    totalMinutes += (endTime.getTime() - startTime.getTime()) / (60 * 1000);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        double hours = totalMinutes / 60;
        return String.format(Locale.getDefault(), "%.1f hours", hours);
    }
}