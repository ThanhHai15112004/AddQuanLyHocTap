package com.example.hoctap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hoctap.R;
import com.example.hoctap.activities.AddNoteActivity;
import com.example.hoctap.activities.AddTodoActivity;
import com.example.hoctap.adapters.NotesPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NotesFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        fabAdd = view.findViewById(R.id.fabAdd);

        setupViewPager();
        setupTabLayout();
        setupFab();

        return view;
    }

    private void setupViewPager() {
        NotesPagerAdapter adapter = new NotesPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Notes");
                    break;
                case 1:
                    tab.setText("To-Do");
                    break;
            }
        }).attach();
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            int currentTab = viewPager.getCurrentItem();
            Intent intent;
            if (currentTab == 0) {
                // Notes tab
                intent = new Intent(getActivity(), AddNoteActivity.class);
            } else {
                // To-Do tab
                intent = new Intent(getActivity(), AddTodoActivity.class);
            }
            startActivity(intent);
        });
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

