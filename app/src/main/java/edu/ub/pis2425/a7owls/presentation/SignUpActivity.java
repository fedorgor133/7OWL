package edu.ub.pis2425.a7owls.presentation;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.ub.pis2425.a7owls.databinding.ActivitySignupBinding;
import edu.ub.pis2425.a7owls.data.service.AuthService;

public class SignUpActivity extends AppCompatActivity {
    /* Attributes */
    private AuthService authService;
    /* View binding */
    private ActivitySignupBinding binding;

    /**
     * Called when the activity is being created.
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
     * Initialize the listeners of the widgets.
     */
    private void initWidgetListeners() {
        binding.btnSignUp.setOnClickListener(ignoredView -> {
            /* Get the values of the widgets */
            String username = binding.usernameSignUp.getText().toString();
            String password = binding.passwordSignUp.getText().toString();
            String passwordConfirmation = binding.passwordSignUp2.getText().toString();

            /* Define the listener for the sign up operation */
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

            /* Invoke the sign up operation */
            authService.signUp(
                    username,
                    password,
                    passwordConfirmation,
                    onSignUpListener
            );
        });
    }
}