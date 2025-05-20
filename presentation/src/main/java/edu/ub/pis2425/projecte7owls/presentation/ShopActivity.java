package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ub.pis2425.projecte7owls.R;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        setupBottomNavigation();
        loadShoppingFragment();

    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_shop); // Marca "Shop" como seleccionado

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_contador) {
                Intent intent = new Intent(this, ContadorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_quiz) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_shop) {
                return true;
            } else if (id == R.id.nav_ruleta) {
                Intent intent = new Intent(this, RouletteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void loadShoppingFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, new ShoppingFragment()) // Aquí cargas tu ShoppingFragment
                .commit();
    }
}
