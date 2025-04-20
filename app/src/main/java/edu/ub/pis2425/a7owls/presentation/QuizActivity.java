package edu.ub.pis2425.a7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.a7owls.R;
import edu.ub.pis2425.a7owls.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<DocumentSnapshot> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final int totalQuestions = 10;
    private Handler inactivityHandler;
    private Runnable inactivityRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inactivityHandler = new Handler();
        inactivityRunnable = this::endQuizDueToInactivity;

        setupBottomNavigation();
        loadQuestions();
        setupOptionListeners();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_quiz);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_contador) {
                startActivity(new Intent(this, ContadorActivity.class));
                return true;
            } else if (id == R.id.nav_quiz) {
                return true;
            } else if (id == R.id.nav_shop) {
                startActivity(new Intent(this, ShopActivity.class));
                return true;
            } else if (id == R.id.nav_ruleta) {
                startActivity(new Intent(this, RuletaActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadQuestions() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("preguntas_usuari")
                .whereEqualTo("userId", uid)
                .whereEqualTo("contestadaCorrecta", false)
                .limit(totalQuestions)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    questions = querySnapshot.getDocuments();
                    if (questions.isEmpty()) {
                        Toast.makeText(this, "No questions available. Come back later!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        showQuestion();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void showQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showFinalScore();
            return;
        }

        resetInactivityTimer();

        DocumentSnapshot qDoc = questions.get(currentQuestionIndex);
        String pregunta = qDoc.getString("pregunta");
        String correcta = qDoc.getString("respuestaCorrecta");
        List<String> opciones = new ArrayList<>();
        opciones.add(qDoc.getString("respuesta1"));
        opciones.add(qDoc.getString("respuesta2"));
        opciones.add(qDoc.getString("respuesta3"));
        opciones.add(correcta);
        Collections.shuffle(opciones);

        binding.txtPregunta.setText(pregunta);
        binding.radioGroup.clearCheck();
        binding.radio1.setText(opciones.get(0));
        binding.radio2.setText(opciones.get(1));
        binding.radio3.setText(opciones.get(2));
        binding.radio4.setText(opciones.get(3));
    }

    private void setupOptionListeners() {
        binding.btnSubmit.setOnClickListener(v -> {
            int selectedId = binding.radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            resetInactivityTimer();

            RadioButton selected = findViewById(selectedId);
            String selectedAnswer = selected.getText().toString();
            DocumentSnapshot currentDoc = questions.get(currentQuestionIndex);
            String correctAnswer = currentDoc.getString("respuestaCorrecta");

            boolean isCorrect = selectedAnswer.equals(correctAnswer);
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("contestadaCorrecta", isCorrect);

            if (isCorrect) {
                score += 10;
                Toast.makeText(this, "+10 points! Correct", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show();
            }

            db.collection("preguntas")
                    .document(currentDoc.getId())
                    .update(updateData);

            currentQuestionIndex++;
            showQuestion();
        });
    }

    private void showFinalScore() {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> result = new HashMap<>();
        result.put("userId", uid);
        result.put("score", score);
        result.put("timestamp", new java.util.Date());

        db.collection("results")
                .add(result)
                .addOnSuccessListener(doc ->
                        Toast.makeText(this, "Quiz finished! Score: " + score + " points", Toast.LENGTH_LONG).show()
                );

        finish();
    }

    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable, 60000); // 60s
    }

    private void endQuizDueToInactivity() {
        Toast.makeText(this, "No activity detected. Quiz ended.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        super.onDestroy();
    }
}
