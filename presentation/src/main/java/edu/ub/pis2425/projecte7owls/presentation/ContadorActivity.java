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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.ub.pis2425.projecte7owls.R;

public class ContadorActivity extends AppCompatActivity {

    private TextView diasTextView;
    private TextView adviceTextView;
    private TextView userInfotextView;
    private Button resetButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Timestamp fechaReset;
    private Timestamp fechaRegistro;
    // Lista para almacenar los consejos cargados desde Firestore.
    private final List<AdviceMessage> adviceList = new ArrayList<>();

    // Handler y Runnable para actualizar la UI periódicamente
    private final Handler handler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (fechaReset != null) {
                long diasSimulados = calcularDias(fechaReset, Timestamp.now());
                diasTextView.setText("You have been clean " + diasSimulados + " day(s).");
                String advice = getAdviceForDays(diasSimulados);
                if (advice != null) {
                    adviceTextView.setText(advice);
                } else {
                    adviceTextView.setText("Keep going! New challenges await you.");
                }
            }
            // Repetir cada segundo
            handler.postDelayed(this, 1000);
        }
    };

    // Clase para representar un consejo
    public static class AdviceMessage {
        public int minDays;
        public int maxDays;
        public String message;

        public AdviceMessage() { } // Constructor vacío para Firebase

        public AdviceMessage(int minDays, int maxDays, String message) {
            this.minDays = minDays;
            this.maxDays = maxDays;
            this.message = message;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);
        diasTextView = findViewById(R.id.diasTextView);
        adviceTextView = findViewById(R.id.adviceTextView);
        resetButton = findViewById(R.id.resetButton);
        userInfotextView = findViewById(R.id.textViewUserInfo);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupBottomNavigation();
        resetButton.setOnClickListener(v -> mostrarConfirmacionReset());

        loadAdviceMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Arranca la actualización periódica
        handler.post(updateRunnable);

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            obtenerFechaRegistro(userId);
            mostrarResetMensaje(userId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener actualizaciones al salir
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
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get()
                .addOnSuccessListener(document -> {
                    long currentCount = 0;
                    if (document.exists() && document.contains("resetCount")) {
                        currentCount = document.getLong("resetCount");
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("fechaReset", FieldValue.serverTimestamp());
                    updates.put("resetCount", currentCount + 1);

                    userRef.update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "fechaReset y resetCount actualizados.");
                                // Delay a bit before showing updated data
                                new Handler().postDelayed(() -> mostrarResetMensaje(userId), 500);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar datos del usuario", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener el documento del usuario", e));
    }



    private void obtenerFechaRegistro(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("fechaRegistro")) {
                        Timestamp ts = document.getTimestamp("fechaRegistro");
                        if (ts != null) {
                            // Guardamos la fecha inicial para que el Runnable actualice la UI.
                            fechaRegistro = ts;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos del usuario", e));
    }

    // Simulación: Cada 10 segundos reales equivalen a 1 día simulado.
    private long calcularDias(Timestamp inicio, Timestamp fin) {
        long diffMillis = fin.toDate().getTime() - inicio.toDate().getTime();
        long segundosReales = TimeUnit.MILLISECONDS.toSeconds(diffMillis);
        return segundosReales / 10;
    }

    // Carga todos los consejos de la colección "advice" en Firestore.
    private void loadAdviceMessages() {
        db.collection("advice")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    adviceList.clear();
                    queryDocumentSnapshots.getDocuments().forEach(document -> {
                        AdviceMessage advice = document.toObject(AdviceMessage.class);
                        adviceList.add(advice);
                    });
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading advice messages", e));

    }

    // Devuelve el mensaje adecuado según los días simulados.
    private String getAdviceForDays(long days) {
        for (AdviceMessage advice : adviceList) {
            if (days >= advice.minDays && days <= advice.maxDays) {
                return advice.message;
            }
        }
        return null;
    }

    private void mostrarResetMensaje(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Timestamp fechaRegistro = document.getTimestamp("fechaRegistro");
                        this.fechaReset = document.getTimestamp("fechaReset");
                        long resetCount = document.contains("resetCount") ? document.getLong("resetCount") : 0;

                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                        String fechaRegistroStr = (fechaRegistro != null) ? sdf.format(fechaRegistro.toDate()) : "unknown";
                        String fechaResetStr = (fechaReset != null) ? sdf.format(fechaReset.toDate()) : "never";

                        String mensaje = "You joined on " + fechaRegistroStr + ".\n";
                        mensaje += "Last reset: " + fechaResetStr + ".\n";
                        mensaje += "You've restarted your journey " + resetCount + " time(s). ";

                        if (resetCount == 0) {
                            mensaje += "Keep it up!";
                        } else if (resetCount < 3) {
                            mensaje += "Each reset is a step forward. Stay strong!";
                        } else if (resetCount < 5) {
                            mensaje += "Progress isn't linear. You're learning!";
                        } else {
                            mensaje += "Many resets? That means you keep trying. That's courage!";
                        }

                        userInfotextView.setText(mensaje);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading reset count", e));
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_contador);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_contador) {
                return true; // Ya estás en esta actividad, no haces nada
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
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }
}
