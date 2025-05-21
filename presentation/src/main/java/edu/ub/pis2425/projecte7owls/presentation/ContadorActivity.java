package edu.ub.pis2425.projecte7owls.presentation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.AdviceMessageContador;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.ContadorViewModel;

public class ContadorActivity extends AppCompatActivity {

    private TextView diasTextView;
    private TextView adviceTextView;
    private TextView userInfotextView;
    private Button resetButton;
    private FirebaseAuth auth;
    private ContadorViewModel contadorViewModel;
    private Timestamp fechaReset;
    private Timestamp fechaRegistro;
    private long resetCount = 0;
    private List<AdviceMessageContador> adviceList;
    private Timestamp ultimoRegistro;
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
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        diasTextView = findViewById(R.id.diasTextView);
        adviceTextView = findViewById(R.id.adviceTextView);
        userInfotextView = findViewById(R.id.textViewUserInfo);
        resetButton = findViewById(R.id.resetButton);

        auth = FirebaseAuth.getInstance();
        contadorViewModel = new ViewModelProvider(this).get(ContadorViewModel.class);

        setupBottomNavigation();

        resetButton.setOnClickListener(v -> mostrarConfirmacionReset());


    }

    private boolean updateRunnableStarted = false;

    @Override
    protected void onResume() {
        super.onResume();

        String uid = auth.getCurrentUser().getUid();

        // 1) Cargar fechaRegistro
        contadorViewModel.getFechaRegistro(uid).observe(this, tsRegistro -> {
            if (tsRegistro != null) {
                fechaRegistro = tsRegistro;
                Log.d("Contador", "fechaRegistro cargada: " + fechaRegistro.toDate());

                // 2) Cargar fechaReset
                contadorViewModel.getFechaReset(uid).observe(this, tsReset -> {
                    if (tsReset != null) {
                        fechaReset = tsReset;
                        Log.d("Contador", "fechaReset cargada: " + fechaReset.toDate());
                    } else {
                        // Si nunca hubo reset, inicializar a fechaRegistro
                        fechaReset = fechaRegistro;
                        contadorViewModel.setFechaReset(uid, fechaRegistro);
                        Log.d("Contador", "Inicializado fechaReset a fechaRegistro");
                    }

                    // 3) Solo iniciar updateRunnable una vez
                    if (!updateRunnableStarted) {
                        handler.post(updateRunnable);
                        updateRunnableStarted = true;
                        Log.d("Contador", "Iniciado updateRunnable");
                    }
                });

                // Actualizar datos de usuario
                updateUserInfo();
            } else {
                Log.w("Contador", "fechaRegistro aún es null");
            }
        });

        // Otros observers únicos
        contadorViewModel.getResetCount(uid).observe(this, count -> {
            if (count != null) {
                resetCount = count;
                updateUserInfo();
                Log.d("Contador", "resetCount: " + resetCount);
            }
        });

        contadorViewModel.getAdviceMessages().observe(this, advices -> {
            adviceList = advices;
            Log.d("Contador", "adviceList cargada, total: " + (advices != null ? advices.size() : 0));
        });

        contadorViewModel.getUltimoRegistro(uid).observe(this, tsUlt -> {
            if (tsUlt != null) {
                ultimoRegistro = tsUlt;
                updateUserInfo();
                comprovarUltimoRegistro(uid);
                contadorViewModel.updateUltimoRegistro(uid);
                Log.d("Contador", "ultimoRegistro: " + ultimoRegistro.toDate());
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
        updateRunnableStarted = false; // Permitirá reiniciar si vuelve a Resume
        Log.d("Contador", "Handler detenido");
    }

    private void mostrarConfirmacionReset() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Counter")
                .setMessage("Are you sure you want to reset the counter? Every day is a new challenge, don't give up!")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String uid = auth.getCurrentUser().getUid();
                    contadorViewModel.resetContador(uid, task -> {
                        // task puede ser null si hubo error de lectura previa
                        if (task != null && task.isSuccessful()) {
                            Log.d("Contador", "Reset completado correctamente");

                            // refrescar los datos manualmente
                            contadorViewModel.getFechaReset(uid).observe(this, ts -> {
                                if (ts != null) {
                                    fechaReset = ts;
                                }
                            });

                            contadorViewModel.getResetCount(uid).observe(this, count -> {
                                if (count != null) {
                                    resetCount = count;
                                    updateUserInfo();
                                }
                            });
                        } else {
                            Log.e("Contador", "Error al hacer reset");
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void updateUserInfo() {
        if (fechaRegistro == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String fechaRegistroStr = sdf.format(fechaRegistro.toDate());
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

    // Simulación: Cada 10 segundos reales equivalen a 1 día simulado.
    private long calcularDias(Timestamp inicio, Timestamp fin) {
        long diffMillis = fin.toDate().getTime() - inicio.toDate().getTime();
        long segundos = TimeUnit.MILLISECONDS.toSeconds(diffMillis);
        return segundos / 10; // 10 segundos reales = 1 día simulado
    }

    // Devuelve el mensaje adecuado según los días simulados.
    private String getAdviceForDays(long days) {
        if (adviceList == null) return null;
        for (AdviceMessageContador advice : adviceList) {
            if (days >= advice.getMinDays() && days <= advice.getMaxDays()) {
                return advice.getMessage();
            }
        }
        return null;
    }
    // Comprueba si la fecha del ultimo registro es de un dia diferente al actual para restringir el numero de Quiz al dia
    private void comprovarUltimoRegistro(String uid) {
        String ultimoRegistroS = convertirTimeStampDia(ultimoRegistro);
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        if (!fechaActual.equals(ultimoRegistroS)) {
            contadorViewModel.resetNumQuiz(uid);
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

            if (id == R.id.nav_contador) return true;
            if (id == R.id.nav_quiz) startActivity(new Intent(this, QuizActivity.class));
            else if (id == R.id.nav_shop) startActivity(new Intent(this, ShopActivity.class));
            else if (id == R.id.nav_ruleta) startActivity(new Intent(this, RouletteActivity.class));
            else if (id == R.id.nav_profile) startActivity(new Intent(this, ProfileActivity.class));

            return true;
        });
    }


}
