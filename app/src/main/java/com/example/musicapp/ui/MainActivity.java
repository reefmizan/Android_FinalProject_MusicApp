package com.example.musicapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.musicapp.R;
import com.example.musicapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = binding.bottomNavigation;
            ImageButton btnLogout = binding.btnLogout;

            NavigationUI.setupWithNavController(bottomNav, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                    bottomNav.setVisibility(View.GONE);
                    btnLogout.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                    btnLogout.setVisibility(View.VISIBLE);

                    btnLogout.setOnClickListener(v -> {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                        navController.navigate(R.id.loginFragment);
                    });
                }
            });
        }
    }
}