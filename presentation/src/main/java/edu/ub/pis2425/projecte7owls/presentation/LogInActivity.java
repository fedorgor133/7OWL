package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.ub.pis2425.projecte7owls.databinding.ActivityLoginBinding;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        initWidgetListeners();
    }

    private void initWidgetListeners() {
        binding.btnLogIn.setOnClickListener(ignoredView -> {
            String email = binding.logInUsername.getText().toString().trim();
            String password = binding.logInPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            /*
            Usamos la funcion de sign in de FirebaseAuth para loguearnos. Firebase
            comprueba que el usuario ya exista y que la contraseña sea correcta.
            Si la tasca es exitosa nos lleva a la pantalla principal.
             */
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Intent intent = new Intent(LogInActivity.this, ContadorActivity.class);
                                intent.putExtra("USER_ID", user.getUid()); // Pasamos el UID de Firebase
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.btnSignUp.setOnClickListener(ignoredView -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}