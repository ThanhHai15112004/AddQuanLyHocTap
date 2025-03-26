package com.example.hoctap.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hoctap.fragments.NotesListFragment;
import com.example.hoctap.fragments.TodoListFragment;

public class NotesPagerAdapter extends FragmentStateAdapter {

    public NotesPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NotesListFragment();
            case 1:
                return new TodoListFragment();
            default:
                return new NotesListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Notes and Todo tabs
    }
}

