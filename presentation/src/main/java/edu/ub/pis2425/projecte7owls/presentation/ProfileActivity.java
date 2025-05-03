package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_contador);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_contador) {
                startActivity(new Intent(this, ContadorActivity.class));
                return true;
            } else if (id == R.id.nav_quiz) {
                startActivity(new Intent(this, QuizActivity.class));
                return true;
            } else if (id == R.id.nav_shop) {
                startActivity(new Intent(this, ShopActivity.class));
                return true;
            } else if (id == R.id.nav_ruleta) {
                startActivity(new Intent(this, RouletteActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}
