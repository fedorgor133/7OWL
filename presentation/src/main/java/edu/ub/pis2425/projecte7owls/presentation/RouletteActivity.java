package edu.ub.pis2425.projecte7owls.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.AdviceMessage;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.UserViewModel;

public class RouletteActivity extends AppCompatActivity {

    private TextView pointsTextView, resultTextView;
    private Spinner numberSpinner;
    private EditText numberBetEditText, colorBetEditText, parityBetEditText;
    private RadioGroup colorRadioGroup, parityRadioGroup;
    private ImageView rouletteGifImageView;
    private Button spinButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private int currentPoints = 0;
    private String userId;
    private UserViewModel userViewModel;
    /*
    Variables per MPV4: Notificació de temps no net
     */
    private List<AdviceMessage> adviceList = new ArrayList<>();
    /*

     */
    private long totalPlayTimeMillis = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onResume();
        setContentView(R.layout.activity_roulette);

        // Inicializa los widgets
        pointsTextView = findViewById(R.id.pointsTextView);
        numberSpinner = findViewById(R.id.numberSpinner);
        numberBetEditText = findViewById(R.id.numberBetEditText);
        colorRadioGroup = findViewById(R.id.colorRadioGroup);
        colorBetEditText = findViewById(R.id.colorBetEditText);
        parityRadioGroup = findViewById(R.id.parityRadioGroup);
        parityBetEditText = findViewById(R.id.parityBetEditText);
        rouletteGifImageView = findViewById(R.id.rouletteGifImageView);
        spinButton = findViewById(R.id.spinButton);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        cargarConsejosDesdeFirestore();
        setupBottomNavigation();
        loadUserPoints();
        setupNumberSpinner();

        //botón de girar.
        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeBetAndSpin();
            }
        });
    }

    //números del 0 al 36.
    private void setupNumberSpinner() {
        Integer[] numbers = new Integer[37];
        for (int i = 0; i < 37; i++) {
            numbers[i] = i;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSpinner.setAdapter(adapter);
    }

    private void loadUserPoints() {
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            db.collection("usuarios").document(userId).get().addOnSuccessListener(document -> {
                if (document.exists() && document.contains("points")) {
                    Long pointsValue = document.getLong("points");
                    currentPoints = pointsValue != null ? pointsValue.intValue() : 0;
                } else {
                    currentPoints = 0;
                }
                pointsTextView.setText("Points: " + currentPoints);
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error loading points", e);
            });
        }
    }

    private void mostrarAvisoDeJuegoProlongado() {
        long minutos = TimeUnit.MILLISECONDS.toMinutes(totalPlayTimeMillis);
        String consejo = getAdviceForMinutes((int) minutos);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Too much time playing?")
                .setMessage("You have been playing " + minutos + " minute(s). \n\nIn that time you could have " + consejo + ".")
                .setPositiveButton("Get a rest", (dialogInterface, which) -> {
                     Intent intent = new Intent(RouletteActivity.this, ContadorActivity.class);
                     startActivity(intent);
                     finish();
                  })
                .setNegativeButton("Keep playing", null)
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.blue_dark));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.blue_dark));
    }
    private String getAdviceForMinutes(int minutos) {
        for (AdviceMessage advice : adviceList) {
            if (advice.isApplicable(minutos)) {
                return advice.getAdvice();
            }
        }
        return "rested or done something healthy";
    }

    // apuestas ingresadas, valida y luego inicia la animación de la ruleta.
    private void placeBetAndSpin() {
        totalPlayTimeMillis += 60_000;

        if (totalPlayTimeMillis % 120_000 == 0) {
            mostrarAvisoDeJuegoProlongado();
        }

        // Validación y extracción de apuestas
        int betNumber = 0, betColor = 0, betParity = 0;
        int chosenNumber = (Integer) numberSpinner.getSelectedItem();
        int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
        int selectedParityId = parityRadioGroup.getCheckedRadioButtonId();

        String numberBetStr = numberBetEditText.getText().toString().trim();
        String colorBetStr = colorBetEditText.getText().toString().trim();
        String parityBetStr = parityBetEditText.getText().toString().trim();

        try {
            betNumber = numberBetStr.isEmpty() ? 0 : Integer.parseInt(numberBetStr);
        } catch (NumberFormatException e) {
            betNumber = 0;
        }
        try {
            betColor = colorBetStr.isEmpty() ? 0 : Integer.parseInt(colorBetStr);
        } catch (NumberFormatException e) {
            betColor = 0;
        }
        try {
            betParity = parityBetStr.isEmpty() ? 0 : Integer.parseInt(parityBetStr);
        } catch (NumberFormatException e) {
            betParity = 0;
        }

        int totalBet = betNumber + betColor + betParity;
        if (totalBet == 0) {
            Toast.makeText(this, "Please place a bet.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalBet > currentPoints) {
            Toast.makeText(this, "You do not have enough points to bet that amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        spinButton.setEnabled(false);

        // Cargar el GIF de la ruleta usando Glide.
        Glide.with(this)
                .asGif()
                .load(R.drawable.roulette)
                .into(rouletteGifImageView);

        // Espera 5 segundos para simular la duración de la animación.
        int finalBetNumber = betNumber;
        int finalBetColor = betColor;
        int finalBetParity = betParity;
        new Handler().postDelayed(() -> {
            // Detener la animación del GIF.
            Glide.with(RouletteActivity.this).clear(rouletteGifImageView);
            // imagen estática para representar la ruleta detenida
            rouletteGifImageView.setImageResource(R.drawable.roulette);

            // Resolver la apuesta y mostrar el resultado
            resolveSpin(finalBetNumber, finalBetColor, finalBetParity, chosenNumber, selectedColorId, selectedParityId);
        }, 5000); // 5000 ms equivalen a 5 segundos.
    }


    // resultado aleatorio y muestra un AlertDialog con el resultado.
    private void resolveSpin(int betNumber, int betColor, int betParity, int chosenNumber, int selectedColorId, int selectedParityId) {
        Random random = new Random();
        int outcome = random.nextInt(37); // Resultado: 0 a 36

        // Para el 0, solo se mostrará el número (se ignoran color y paridad)
        String outcomeColor = outcome == 0 ? "" : getRouletteColor(outcome);
        String outcomeParity = (outcome == 0) ? "" : (outcome % 2 == 0 ? "Even" : "Odd");

        int winnings = 0;
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append("Result: ").append(outcome);
        if (outcome != 0) {
            resultMessage.append(" (").append(outcomeColor).append(", ").append(outcomeParity).append(")");
        }
        resultMessage.append("\n\n");

        // Apuesta por número: paga 35:1
        if (betNumber > 0) {
            if (chosenNumber == outcome) {
                int winAmount = betNumber * 35;
                winnings += winAmount;
                resultMessage.append("Number bet: You have won ").append(winAmount).append(" points.\n");
            } else {
                resultMessage.append("Number bet: You have lost ").append(betNumber).append(" points.\n");
            }
        }

        // Apuesta por color: paga 2:1
        if (betColor > 0 && selectedColorId != -1) {
            RadioButton selectedColorButton = findViewById(selectedColorId);
            String chosenColor = selectedColorButton.getText().toString();
            if (outcome != 0 && chosenColor.equalsIgnoreCase(outcomeColor)) {
                int winAmount = betColor * 2;  // Se duplican los puntos apostados (pago 2:1)
                winnings += winAmount;
                resultMessage.append("Color bet: You have wone ").append(winAmount).append(" points.\n");
            } else {
                resultMessage.append("Color bet: You have lost ").append(betColor).append(" points.\n");
            }
        }

        // Apuesta por paridad: paga 2:1
        if (betParity > 0 && selectedParityId != -1) {
            RadioButton selectedParityButton = findViewById(selectedParityId);
            String chosenParity = selectedParityButton.getText().toString();
            if (outcome != 0 && chosenParity.equalsIgnoreCase(outcomeParity)) {
                int winAmount = betParity * 2;  // Se duplican los puntos apostados (pago 2:1)
                winnings += winAmount;
                resultMessage.append("Parity bet: You have won  ").append(winAmount).append(" points.\n");
            } else {
                resultMessage.append("Parity bet: You have lost ").append(betParity).append(" points.\n");
            }
        }

        // Se calcula la ganancia neta: lo ganado menos lo apostado
        int totalBet = betNumber + betColor + betParity;
        int netChange = winnings - totalBet;
        currentPoints += netChange;

        resultMessage.append("\nTotal points earned: ").append(netChange).append(" points.\n");
        resultMessage.append("Your current points: ").append(currentPoints).append(" points.");

        // Actualiza Firestore con los nuevos puntos.
        db.collection("usuarios").document(userId)
                .update("points", currentPoints, "lastUpdate", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Puntos actualizados correctamente."))
                .addOnFailureListener(e -> Log.e("Firestore", "Error actualizando puntos", e));

        userViewModel.addScoreHistory(userId, netChange, "Roulette");
        // Muestra un AlertDialog con el resultado.
        new AlertDialog.Builder(RouletteActivity.this)
                .setTitle("Roulette Results")
                .setMessage(resultMessage.toString())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();

        // Actualiza la vista de puntos y resetea el botón de giro.
        pointsTextView.setText("Points: " + currentPoints);
        spinButton.setEnabled(true);
    }

    // Retorna el color de la ruleta para un número dado, 0 se considera "Verde".
    private String getRouletteColor(int number) {
        Set<Integer> reds = new HashSet<>(Arrays.asList(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36));
        if (number == 0) {
            return "Green";
        }
        return reds.contains(number) ? "Red" : "Black";
    }

    private void cargarConsejosDesdeFirestore(){
        db.collection("roulette_advice")
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

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_ruleta);

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
