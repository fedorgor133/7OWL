package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.ub.pis2425.projecte7owls.databinding.ActivityLoggedInBinding;

public class LoggedInActivity extends AppCompatActivity {

    private ActivityLoggedInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoggedInBinding
                .inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initWidgetListeners();
    }

    private void initWidgetListeners() {
        binding.btnLogOut.setOnClickListener(ignoredView -> {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}