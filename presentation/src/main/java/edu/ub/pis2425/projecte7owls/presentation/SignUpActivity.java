package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.ub.pis2425.projecte7owls.databinding.ActivitySignupBinding;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        initWidgetListeners();
    }

    private void copiarPreguntasBaseParaUsuario(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*
        Leemos las preguntas de Firestore y las copiamos en una nueva colección
        personalizada para el usuario a partir de collection
        Con get cogemos cada documento de la colección
        Y las añadimos a preguntas_usuari con el id del usuario y boolean
        de que aún no han sido contestadas
         */
        db.collection("preguntas")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        // Crear una copia de la pregunta con el UID y el campo contestadaCorrecta
                        Map<String, Object> pregunta = new HashMap<>(doc.getData());
                        pregunta.put("userId", uid);  // Asigna el UID del usuario
                        pregunta.put("contestadaCorrecta", false);  // Inicializa como no respondida

                        // Guardar la pregunta en la colección "preguntas_usuari"
                        db.collection("preguntas_usuari").add(pregunta);
                    }
                    /*
                    Con esto comprobamos que han sido copiadas con éxito
                    Lo borraremos luego porque esto es para asegurar que se copien
                    de cara a nosotros, los desarrolladores
                     */
                    Toast.makeText(this, "Preguntas copiadas con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void initWidgetListeners() {
        binding.btnSignUp.setOnClickListener(ignoredView -> {
            String email = binding.usernameSignUp.getText().toString().trim();
            String password = binding.passwordSignUp.getText().toString();
            String confirmPassword = binding.passwordSignUp2.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            /*
            Esta función es la que crea el usuario en Firebase y
            comprueba que se ha creado correctamente y no existe un usuario
            con el mismo email
             */
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            /*
                            Truquem a una funció per copiar les preguntes base
                            des de Firestore amb el seu UID personalitzat.
                             */
                            copiarPreguntasBaseParaUsuario(uid);

                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.logInTxt.setOnClickListener(ignoredView -> {
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}