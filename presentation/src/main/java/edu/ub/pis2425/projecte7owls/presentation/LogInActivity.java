package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.ub.pis2425.projecte7owls.data.service.AuthService;
import edu.ub.pis2425.projecte7owls.databinding.ActivityLoginBinding;
import edu.ub.pis2425.projecte7owls.domain.entities.User;

public class LogInActivity extends AppCompatActivity {
    /* Attributes */
    private AuthService authService;
    /* View binding */
    private ActivityLoginBinding binding;

    /**
     * Trucat quan l'activity es creada
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Set view binding */
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Inicialitzacions */
        authService = new AuthService();
        initWidgetListeners();
    }

    /**
     * Inicialitza els listeners dels widgets
     */
    private void initWidgetListeners() {
        binding.btnLogIn.setOnClickListener(ignoredView -> {
            /* Obté el valor del widgets */
            String username = binding.logInUsername.getText().toString();
            String password = binding.logInPassword.getText().toString();

            /* Define the listener for the log-in operation */
            AuthService.OnLogInListener listener;
            listener = new AuthService.OnLogInListener() {
                @Override
                public void onLogInSuccess(User user) {
                    Intent intent = new Intent(
                            LogInActivity.this,
                            LoggedInActivity.class
                    );
                    intent.putExtra("USER_ID", user.getId());
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onLogInError(Throwable throwable) {
                    String errorMessage = throwable.getMessage();
                    Toast.makeText(
                            LogInActivity.this,
                            errorMessage,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            };
            authService.logIn(username, password, listener);
        });

        binding.btnSignUp.setOnClickListener(ignoredView -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}