package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.databinding.ActivityProfileBinding;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.UserViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int currentPoints;
    private String uid;
    private TextView pointsTextViewProfile;
    private TextView emailTextView;
    private TextView entryDateTextView;
    private TextView pointsHistoryTextView;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        pointsHistoryTextView = findViewById(R.id.pointsHistoryTextView);
        pointsTextViewProfile = findViewById(R.id.pointsTextViewProfile);
        emailTextView = findViewById(R.id.emailTextView);
        entryDateTextView = findViewById(R.id.entryDateTextView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        setupBottomNavigation();
        loadUserPoints();
        loadUserInfo();
        loadScoreHistory();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_contador) {
                startActivity(new Intent(this, ContadorActivity.class));
                return true;
            } else if (id == R.id.nav_quiz) {
                startActivity(new Intent(this, QuizActivity.class));
                return true;
            } else if (id == R.id.nav_shop) {
                startActivity(new Intent(this, ShopActivity.class));
                return true;
            } else if (id == R.id.nav_ruleta) {
                startActivity(new Intent(this, RouletteActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadUserPoints() {
        if (mAuth.getCurrentUser() != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
                if (document.exists() && document.contains("points")) {
                    Long pointsValue = document.getLong("points");
                    currentPoints = pointsValue != null ? pointsValue.intValue() : 0;
                } else {
                    currentPoints = 0;
                }
                pointsTextViewProfile.setText("Points: " + currentPoints);
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error loading points", e);
            });
        }
    }
    private void loadUserInfo(){
        if (mAuth.getCurrentUser() != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
                if (document.exists()) {
                    if (document.contains("email")) {
                        emailTextView.setText(document.getString("email"));
                    }
                    if (document.contains("fechaRegistro")) {
                        Timestamp timestamp = document.getTimestamp("fechaRegistro"); // Obtiene el Timestamp
                        if (timestamp != null) {
                            Date date = timestamp.toDate(); // Convierte a Date
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Formato de fecha
                            entryDateTextView.setText(sdf.format(date)); // Muestra la fecha formateada
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error loading userInfo", e);
            });
        }
    }
    private void loadScoreHistory() {
        userViewModel.getScoreHistory(uid).observe(this, historyList -> {
            StringBuilder historyText = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            for (Map<String, Object> entry : historyList) {
                Date date = ((Timestamp) entry.get("timestamp")).toDate();
                int scoreChange = ((Long) entry.get("scoreChange")).intValue();

                String formattedChange = (scoreChange > 0 ? "+" : "") + scoreChange;
                historyText.append(sdf.format(date)).append(": ").append(formattedChange).append(" puntos\n");
            }
            pointsHistoryTextView.setText(historyText.toString());
        });
    }

}
