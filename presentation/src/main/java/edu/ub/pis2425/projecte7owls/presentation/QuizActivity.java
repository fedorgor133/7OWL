package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.databinding.ActivityQuizBinding;
import edu.ub.pis2425.projecte7owls.presentation.QuizDialogFragment;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.QuizViewModel;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.UserViewModel;

public class QuizActivity extends AppCompatActivity implements QuizDialogFragment.QuizActivityCallback {

    private ActivityQuizBinding binding;
    private FirebaseAuth mAuth;
    private QuizViewModel quizViewModel;
    private UserViewModel userViewModel;
    private List<DocumentSnapshot> questions = new ArrayList<>();
    private TextView pointsTextViewQuiz;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int numQuiz=0;
    private final int totalQuestions = 10;
    private Handler inactivityHandler;
    private Runnable inactivityRunnable;
    private String uid;
    private int currentPoints;
    private boolean quizRestarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        pointsTextViewQuiz = findViewById(R.id.pointsTextViewQuiz);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        inactivityHandler = new Handler();
        inactivityRunnable = this::endQuizDueToInactivity;


        setupBottomNavigation();
        setupOptionListeners();
        observeUserScore();
        observeQuizData();

        restoreState();
        quizViewModel.loadQuestions(uid, totalQuestions);
        quizViewModel.loadUserPoints(uid);
    }

    private void observeQuizData() {
        quizViewModel.getQuestions().observe(this, loadedQuestions -> {
            questions = loadedQuestions;

            if ((questions == null || questions.size() < totalQuestions) && !quizRestarted) {
                quizRestarted = true;
                //quizViewModel.resetUserQuestions(uid);
                new Handler().postDelayed(() -> {
                    resetQuizState();
                    quizViewModel.loadQuestions(uid, totalQuestions);
                    Toast.makeText(this, "Reiniciando preguntas...", Toast.LENGTH_SHORT).show();
                }, 1000);
                return;
            }

            showQuestion();
        });

        quizViewModel.getUserPoints().observe(this, points -> {
            currentPoints = points;
            pointsTextViewQuiz.setText("Points: " + points);
        });

        quizViewModel.getErrors().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );

        quizViewModel.getNumQuiz(uid).observe(this, num -> {
            numQuiz = num;
            comprovarNumQuiz();
        });
    }
    private void comprovarNumQuiz(){
        if (numQuiz >= 3) {
            Toast.makeText(this, "You have already done 3 quizzes today", Toast.LENGTH_LONG).show();
            finish();
        }
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
            if (isCorrect) {
                score += 10;
                userViewModel.updateUserScore(uid, currentPoints + score);
                userViewModel.addScoreHistory(uid, 10, "Quiz");
                Toast.makeText(this, "+10 owls! Correct", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show();
            }

            quizViewModel.updateQuestionResult(currentDoc.getId(), isCorrect);
            currentQuestionIndex++;
            showQuestion();
        });
    }

    private void showFinalScore() {
        quizViewModel.saveQuizResult(uid, score);
        clearSavedState();
        quizViewModel.increaseNumQuiz(uid,numQuiz);
        numQuiz++;
        QuizDialogFragment.newInstance(score)
                .show(getSupportFragmentManager(), "score_dialog");
    }

    @Override
    public void onRetryQuiz() {
        //quizViewModel.resetUserQuestions(uid);
        resetQuizState();
        quizViewModel.loadQuestions(uid, totalQuestions);
    }

    private void observeUserScore() {
        userViewModel.observeUserScore(uid).observe(this, score ->
                pointsTextViewQuiz.setText("Points: " + score + " owls")
        );
    }

    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable, 60000);
    }

    private void endQuizDueToInactivity() {
        quizViewModel.increaseNumQuiz(uid,numQuiz);
        Toast.makeText(this, "No activity detected. Quiz ended.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void restoreState() {
        SharedPreferences prefs = getSharedPreferences("quiz_state", MODE_PRIVATE);
        currentQuestionIndex = prefs.getInt("currentIndex", 0);
        score = prefs.getInt("score", 0);
    }

    private void clearSavedState() {
        SharedPreferences prefs = getSharedPreferences("quiz_state", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private void resetQuizState() {
        clearSavedState();
        currentQuestionIndex = 0;
        score = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("quiz_state", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("currentIndex", currentQuestionIndex);
        editor.putInt("score", score);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        super.onDestroy();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_quiz);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_contador) {
                startActivity(new Intent(this, ContadorActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            } else if (id == R.id.nav_quiz) {
                return true;
            } else if (id == R.id.nav_shop) {
                startActivity(new Intent(this, ShopActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            } else if (id == R.id.nav_ruleta) {
                startActivity(new Intent(this, RouletteActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            }
            return false;
        });
    }
}
