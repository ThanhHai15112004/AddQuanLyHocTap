package com.example.hoctap.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hoctap.fragments.DayTimetableFragment;

public class TimetablePagerAdapter extends FragmentStateAdapter {
    private boolean isListView;

    public TimetablePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean isListView) {
        super(fragmentManager, lifecycle);
        this.isListView = isListView;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Days are 0-indexed (0 = Monday, 6 = Sunday)
        return DayTimetableFragment.newInstance(position, isListView);
    }

    @Override
    public int getItemCount() {
        return 7; // 7 days of the week
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < 7;
    }
}

