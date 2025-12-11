package com.example.services_project.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import com.example.services_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private DashboardViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Liaison des vues
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        replaceFragment(new HomeFragment());

        // Gestion des clics du menu
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_services) {
                replaceFragment(new ServicesFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
                return true;
            } else if (id == R.id.nav_notifications) {
                replaceFragment(new NotificationsFragment());
                return true;
            } else if (id == R.id.nav_add) {
                replaceFragment(new AddFragment());
                return true;
            }
            return false;
        });

        fabAdd.setOnClickListener(v -> {
            replaceFragment(new AddFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dashboard_container, fragment)
                .commit();
    }
}
