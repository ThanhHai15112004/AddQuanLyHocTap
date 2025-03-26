package com.example.hoctap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hoctap.R;
import com.example.hoctap.activities.AddTimetableEntryActivity;
import com.example.hoctap.adapters.TimetablePagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TimetableFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAdd;
    private Button btnListView, btnCalendarView;
    private boolean isListView = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        fabAdd = view.findViewById(R.id.fabAdd);
        btnListView = view.findViewById(R.id.btnListView);
        btnCalendarView = view.findViewById(R.id.btnCalendarView);

        setupViewPager();
        setupTabLayout();
        setupButtons();

        return view;
    }

    private void setupViewPager() {
        TimetablePagerAdapter adapter = new TimetablePagerAdapter(getChildFragmentManager(), getLifecycle(), isListView);
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Monday");
                    break;
                case 1:
                    tab.setText("Tuesday");
                    break;
                case 2:
                    tab.setText("Wednesday");
                    break;
                case 3:
                    tab.setText("Thursday");
                    break;
                case 4:
                    tab.setText("Friday");
                    break;
                case 5:
                    tab.setText("Saturday");
                    break;
                case 6:
                    tab.setText("Sunday");
                    break;
            }
        }).attach();
    }

    private void setupButtons() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTimetableEntryActivity.class);
            startActivity(intent);
        });

        btnListView.setOnClickListener(v -> {
            if (!isListView) {
                isListView = true;
                btnListView.setBackgroundResource(R.drawable.button_selected);
                btnCalendarView.setBackgroundResource(R.drawable.button_normal);
                refreshViewPager();
            }
        });

        btnCalendarView.setOnClickListener(v -> {
            if (isListView) {
                isListView = false;
                btnCalendarView.setBackgroundResource(R.drawable.button_selected);
                btnListView.setBackgroundResource(R.drawable.button_normal);
                refreshViewPager();
            }
        });
    }

    private void refreshViewPager() {
        TimetablePagerAdapter adapter = new TimetablePagerAdapter(getChildFragmentManager(), getLifecycle(), isListView);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}

