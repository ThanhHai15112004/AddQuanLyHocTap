package com.example.hoctap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap.R;
import com.example.hoctap.adapters.TimetableAdapter;
import com.example.hoctap.dao.TimetableDAO;
import com.example.hoctap.models.TimetableEntry;

import java.util.List;

public class DayTimetableFragment extends Fragment {
    private static final String ARG_DAY = "day";
    private static final String ARG_IS_LIST_VIEW = "is_list_view";

    private int dayOfWeek;
    private boolean isListView;
    private RecyclerView recyclerView;
    private TextView tvNoClasses;
    private TimetableAdapter adapter;
    private TimetableDAO timetableDAO;

    public static DayTimetableFragment newInstance(int day, boolean isListView) {
        DayTimetableFragment fragment = new DayTimetableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, day);
        args.putBoolean(ARG_IS_LIST_VIEW, isListView);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayOfWeek = getArguments().getInt(ARG_DAY);
            isListView = getArguments().getBoolean(ARG_IS_LIST_VIEW);
        }
        timetableDAO = new TimetableDAO(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (isListView) {
            view = inflater.inflate(R.layout.fragment_day_timetable_list, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_day_timetable_calendar, container, false);
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        tvNoClasses = view.findViewById(R.id.tvNoClasses);

        setupRecyclerView();
        loadTimetableEntries();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TimetableAdapter(getContext(), isListView);
        recyclerView.setAdapter(adapter);
    }

    private void loadTimetableEntries() {
        timetableDAO.open();
        List<TimetableEntry> entries = timetableDAO.getTimetableEntriesByDay(dayOfWeek);
        timetableDAO.close();

        if (entries.isEmpty()) {
            tvNoClasses.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoClasses.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setTimetableEntries(entries);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTimetableEntries();
    }
}

