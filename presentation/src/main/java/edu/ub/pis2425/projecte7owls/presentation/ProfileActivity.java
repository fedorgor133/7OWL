package edu.ub.pis2425.projecte7owls.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private Button scoreHistorybttn;
    private FloatingActionButton helpButton;
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int currentPoints;
    private String uid;
    private TextView pointsTextViewProfile;
    private TextView emailTextView;
    private TextView entryDateTextView;
    private TextView pointsHistoryTextView;
    private ImageView profileImageView;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        pointsTextViewProfile = findViewById(R.id.pointsTextViewProfile);
        emailTextView = findViewById(R.id.emailTextView);
        entryDateTextView = findViewById(R.id.entryDateTextView);
        profileImageView = findViewById(R.id.profileImageView);

        // Dentro de onCreate()
        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Cerrar sesión en Firebase
            Intent intent = new Intent(ProfileActivity.this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finalizar ProfileActivity para que no quede en la pila de actividades
        });


        binding.profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImagesActivity.class);
            startActivityForResult(intent, 1);
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        scoreHistorybttn = findViewById(R.id.scoreButton);
        scoreHistorybttn.setOnClickListener(v -> {
            ScoreDialogFragment scoreDialogFragment = ScoreDialogFragment.newInstance(uid);
            scoreDialogFragment.show(getSupportFragmentManager(), "scoreDialogFragment");
        });

        helpButton = findViewById(R.id.helpFab);
        helpButton.setOnClickListener(v -> {
            HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
            helpDialogFragment.show(getSupportFragmentManager(), "helpDialogFragment");
        });
        setupBottomNavigation();
        loadUserPoints();
        loadUserInfo();



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            int imageResource = data.getIntExtra("selectedImage", R.drawable.owl1);
            binding.profileImageView.setImageResource(imageResource);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_contador) {
                Intent intent = new Intent(this, ContadorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_quiz) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_shop) {
                Intent intent = new Intent(this, ShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_ruleta) {
                Intent intent = new Intent(this, RouletteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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
                    if(document.contains("imagenPerfil")){
                        int imageId = document.getLong("imagenPerfil").intValue();
                        if(imageId != 0){
                            binding.profileImageView.setImageResource(imageId);
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error loading userInfo", e);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
