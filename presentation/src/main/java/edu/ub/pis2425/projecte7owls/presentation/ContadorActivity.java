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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.ub.pis2425.projecte7owls.R;

public class ContadorActivity extends AppCompatActivity {

    private TextView diasTextView;
    private TextView adviceTextView;  // Nuevo TextView para los mensajes de consejo
    private Button resetButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Guardamos la fecha de registro una vez obtenida
    private Timestamp fechaRegistro;
    private Timestamp ultimoRegistro;
    private int numQuiz;

    // Lista para almacenar los consejos cargados desde Firestore.
    private final List<AdviceMessage> adviceList = new ArrayList<>();

    // Handler y Runnable para actualizar la UI periódicamente
    private final Handler handler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (fechaRegistro != null) {
                long diasSimulados = calcularDias(fechaRegistro, Timestamp.now());
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

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupBottomNavigation();
        resetButton.setOnClickListener(v -> mostrarConfirmacionReset());

        loadAdviceMessages();
        comprovarUltimoRegistro();
        actualizarUltimoRegistro();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Arranca la actualización periódica
        handler.post(updateRunnable);

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            obtenerFechaRegistro(userId);
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
                            // Guardamos la fecha inicial para que el Runnable actualice la UI.
                            fechaRegistro = ts;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos del usuario", e));
    }
    private void obtenerFechaUltimoRegistro(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("ultimoRegistro")) {
                        Timestamp ts = document.getTimestamp("ultimoRegistro");
                        if (ts != null) {
                            ultimoRegistro = ts;
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
    // Comprueba si la fecha del ultimo registro es de un dia diferente al actual para restringir el numero de Quiz al dia
    private void comprovarUltimoRegistro() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            obtenerFechaUltimoRegistro(userId);

            // Usar un Handler con un pequeño retraso
            new Handler().postDelayed(() -> {
                if (ultimoRegistro != null) {
                    String ultimoRegistroS = convertirTimeStampDia(ultimoRegistro);
                    String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                    if (!fechaActual.equals(ultimoRegistroS)) {
                        db.collection("usuarios").document(userId).update("numQuiz", 0)
                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating numQuiz", e));
                    }
                }
            }, 500); // Esperar 500ms antes de ejecutar la lógica
        }
    }
    private void actualizarUltimoRegistro() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection("usuarios").document(userId).update("ultimoRegistro", FieldValue.serverTimestamp())
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating ultimoRegistro", e));
        }
    }
    private String convertirTimeStampDia(Timestamp ts){
        String fechaTsString=null;
        if(ts != null){
            Date fecha = ts.toDate();
            // Definir el formato dd/MM/yyyy
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            // Convertir la fecha a String
            fechaTsString = dateFormat.format(fecha);
        }
        return fechaTsString;
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
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
