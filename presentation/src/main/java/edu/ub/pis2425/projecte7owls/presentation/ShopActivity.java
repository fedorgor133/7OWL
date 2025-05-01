package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ub.pis2425.projecte7owls.R;

public class ShopActivity extends AppCompatActivity {

    private int cartTotal = 0;
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
                startActivity(new Intent(this, ContadorActivity.class));
                return true;
            } else if (id == R.id.nav_quiz) {
                startActivity(new Intent(this, QuizActivity.class));
                return true;
            } else if (id == R.id.nav_shop) {
                return true; // Ya estamos en Shop
            } else if (id == R.id.nav_ruleta) {
                startActivity(new Intent(this, RouletteActivity.class));
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
