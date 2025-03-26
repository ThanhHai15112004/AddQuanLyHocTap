package com.example.hoctap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.hoctap.fragments.AssignmentsFragment;
import com.example.hoctap.fragments.NotesFragment;
import com.example.hoctap.fragments.ScoresFragment;
import com.example.hoctap.fragments.TimetableFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment timetableFragment;
    private Fragment assignmentsFragment;
    private Fragment scoresFragment;
    private Fragment notesFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Initialize fragments
        timetableFragment = new TimetableFragment();
        assignmentsFragment = new AssignmentsFragment();
        scoresFragment = new ScoresFragment();
        notesFragment = new NotesFragment();

        // Set default fragment
        if (savedInstanceState == null) {
            activeFragment = timetableFragment;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, timetableFragment, "timetable")
                    .add(R.id.fragment_container, assignmentsFragment, "assignments")
                    .hide(assignmentsFragment)
                    .add(R.id.fragment_container, scoresFragment, "scores")
                    .hide(scoresFragment)
                    .add(R.id.fragment_container, notesFragment, "notes")
                    .hide(notesFragment)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_timetable);
        } else {
            // Restore fragments from saved state
            timetableFragment = getSupportFragmentManager().findFragmentByTag("timetable");
            assignmentsFragment = getSupportFragmentManager().findFragmentByTag("assignments");
            scoresFragment = getSupportFragmentManager().findFragmentByTag("scores");
            notesFragment = getSupportFragmentManager().findFragmentByTag("notes");
            activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    // Thay switch-case bằng if-else
                    if (itemId == R.id.nav_timetable) {
                        selectedFragment = timetableFragment;
                    } else if (itemId == R.id.nav_assignments) {
                        selectedFragment = assignmentsFragment;
                    } else if (itemId == R.id.nav_scores) {
                        selectedFragment = scoresFragment;
                    } else if (itemId == R.id.nav_notes) {
                        selectedFragment = notesFragment;
                    } else {
                        return false;
                    }

                    if (selectedFragment != null && selectedFragment != activeFragment) {
                        switchFragment(selectedFragment);
                        return true;
                    }
                    return false;
                }
            };

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Hide current active fragment
        if (activeFragment != null) {
            transaction.hide(activeFragment);
        }

        // Show selected fragment
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        }

        // Add animation for smoother transition
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        // Commit transaction
        transaction.commit();
        activeFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        // If not on default fragment, go back to TimetableFragment
        if (activeFragment != timetableFragment) {
            switchFragment(timetableFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_timetable);
        } else {
            super.onBackPressed(); // Exit app if on default fragment
        }
    }
}