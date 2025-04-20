package edu.ub.pis2425.projecte7owls.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.Timestamp;
import java.util.concurrent.TimeUnit;

import edu.ub.pis2425.projecte7owls.R;

public class ContadorActivity extends AppCompatActivity {

    private TextView diasTextView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);
        diasTextView = findViewById(R.id.diasTextView);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupBottomNavigation();


        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            obtenerFechaRegistro(userId);
        }
    }

    private void obtenerFechaRegistro(String userId) {
        db.collection("usuarios").document(userId).get().addOnSuccessListener(document -> {
            if (document.exists() && document.contains("fechaRegistro")) {
                Timestamp fechaRegistro = document.getTimestamp("fechaRegistro");
                if (fechaRegistro != null) {
                    long diasUsados = calcularDias(fechaRegistro, Timestamp.now());
                    diasTextView.setText("Llevas " + diasUsados + " día(s) usando la app.");
                }
            } else {
                diasTextView.setText("No se encontró la fecha de registro.");
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos del usuario", e));
    }

    private long calcularDias(Timestamp inicio, Timestamp fin) {
        long diffInMillis = fin.toDate().getTime() - inicio.toDate().getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_quiz);

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
                startActivity(new Intent(this, RuletaActivity.class));
                return true;
            }
            return false;
        });
    }

}