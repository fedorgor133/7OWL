package edu.ub.pis2425.projecte7owls.presentation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.Timestamp;

import java.util.concurrent.TimeUnit;

import edu.ub.pis2425.projecte7owls.R;

public class ContadorActivity extends AppCompatActivity {

    private TextView diasTextView;
    private Button resetButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // guardamos la fecha de registro una vez
    private Timestamp fechaRegistro;

    // handler y runnable para actualizar la UI periódicamente
    private final Handler handler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (fechaRegistro != null) {
                long diasSimulados = calcularDias(fechaRegistro, Timestamp.now());
                diasTextView.setText("You have been clean " + diasSimulados + " day(s).");
            }
            // repetir cada segundo
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);
        diasTextView = findViewById(R.id.diasTextView);
        resetButton = findViewById(R.id.resetButton);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupBottomNavigation();

        resetButton.setOnClickListener(v -> mostrarConfirmacionReset());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // arrancamos la actualización periódica
        handler.post(updateRunnable);

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            obtenerFechaRegistro(userId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // detenemos actualizaciones al salir
        handler.removeCallbacks(updateRunnable);
    }

    private void mostrarConfirmacionReset() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Counter")
                .setMessage("Are you sure you want to reset the counter? Every day is a new challenge, don't give up!")
                .setPositiveButton("Yes", (dialog, which) -> resetearContador())
                .setNegativeButton("No", null)
                .show();
    }

    private void resetearContador() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();

        db.collection("usuarios").document(userId)
                .update("fechaRegistro", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Fecha de inicio reiniciada correctamente.");
                    obtenerFechaRegistro(userId);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al resetear contador", e));
    }

    private void obtenerFechaRegistro(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("fechaRegistro")) {
                        Timestamp ts = document.getTimestamp("fechaRegistro");
                        if (ts != null) {
                            // guardamos la fecha inicial y dejamos que el runnable actualice la UI
                            fechaRegistro = ts;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos del usuario", e));
    }

    // Cada 10 segundos reales = 1 día simulado.

    private long calcularDias(Timestamp inicio, Timestamp fin) {
        long diffMillis = fin.toDate().getTime() - inicio.toDate().getTime();
        long segundosReales = TimeUnit.MILLISECONDS.toSeconds(diffMillis);
        return segundosReales / 10;
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_contador);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_contador) {
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
            }
            return false;
        });
    }
}
