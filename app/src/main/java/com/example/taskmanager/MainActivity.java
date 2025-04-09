package com.example.taskmanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanager.databinding.ActivityMainBinding;
import com.example.taskmanager.screen.assignment.AssignmentFragment;
import com.example.taskmanager.screen.home.HomeFragment;
import com.example.taskmanager.screen.notifications.NotificationsFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static MainActivity instance;

    public static MainActivity getInstants() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load fragment mặc định
        showFragment(new HomeFragment());

        binding.navView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                showFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                showFragment(new AssignmentFragment());
            } else if (item.getItemId() == R.id.navigation_notifications) {
                showFragment(new NotificationsFragment());
            }
            return true;
        });
    }

    public void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit(); // Không addToBackStack nếu không cần quay lại
    }
}
