package com.example.musicapp.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.musicapp.R;
import com.example.musicapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // בונוס/חובה: שימוש ב-ViewBinding למניעת findViewById וקריסות זיכרון
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. ניפוח המסך בעזרת ה-Binding והצגתו
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. מציאת מנהל הניווט (NavController) מתוך ה-FragmentContainerView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = binding.bottomNavigation;

            // 3. קסם המיפוי (Magic Mapping) - חיבור אוטומטי של התפריט התחתון למערכת הניווט
            NavigationUI.setupWithNavController(bottomNav, navController);

            // 4. ניהול ממשק גלובלי: הסתרת התפריט התחתון במסכי הזדהות (Login/Register)
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                    bottomNav.setVisibility(View.GONE); // מסתיר את התפריט
                } else {
                    bottomNav.setVisibility(View.VISIBLE); // מציג את התפריט בשאר המסכים
                }
            });
        }
    }
}