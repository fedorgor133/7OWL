package edu.ub.pis2425.a7owls.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.ub.pis2425.a7owls.databinding.ActivityLoggedInBinding;

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