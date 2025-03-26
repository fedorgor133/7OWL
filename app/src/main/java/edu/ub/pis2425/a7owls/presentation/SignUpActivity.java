package edu.ub.pis2425.a7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.ub.pis2425.a7owls.databinding.ActivitySignupBinding;
import edu.ub.pis2425.a7owls.data.service.AuthService;

public class SignUpActivity extends AppCompatActivity {
    private AuthService authService;
    private ActivitySignupBinding binding;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Initializations */
        authService = new AuthService();
        initWidgetListeners();
    }

    /**
     * Inicialitza listeners dels widgets
     */
    private void initWidgetListeners() {
        binding.btnSignUp.setOnClickListener(ignoredView -> {
            /* Obté valors dels widgets */
            String username = binding.usernameSignUp.getText().toString();
            String password = binding.passwordSignUp.getText().toString();
            String passwordConfirmation = binding.passwordSignUp2.getText().toString();

            /* Defineix listener per l'operacio de sign up */
            AuthService.OnSignUpListener onSignUpListener = new AuthService.OnSignUpListener() {
                @Override
                public void onSignUpSuccess() {
                    finish();
                }
                @Override
                public void onSignUpError(Throwable throwable) {
                    String errorMessage = throwable.getMessage();
                    Toast.makeText(
                            SignUpActivity.this,
                            errorMessage,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            };


            authService.signUp(
                    username,
                    password,
                    passwordConfirmation,
                    onSignUpListener
            );
        });

        binding.logInTxt.setOnClickListener(ignoredView -> {
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}