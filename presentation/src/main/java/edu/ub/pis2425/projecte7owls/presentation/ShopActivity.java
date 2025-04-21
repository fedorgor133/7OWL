package edu.ub.pis2425.projecte7owls.presentation;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.databinding.ActivityLoginBinding;
import edu.ub.pis2425.projecte7owls.databinding.ActivityQuizBinding;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_shop);
        super.onCreate(savedInstanceState);
        setupBottomNavigation();
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
                return true;
            } else if (id == R.id.nav_ruleta) {
                startActivity(new Intent(this, RouletteActivity.class));
                return true;
            }
            return false;
        });
    }
}