package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int currentPoints;
    private String uid;
    private TextView pointsTextViewProfile;
    private TextView emailTextView;
    private TextView entryDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pointsTextViewProfile = findViewById(R.id.pointsTextViewProfile);
        emailTextView = findViewById(R.id.emailTextView);
        entryDateTextView = findViewById(R.id.entryDateTextView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        setupBottomNavigation();
        loadUserPoints();
        loadUserInfo();
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
}
