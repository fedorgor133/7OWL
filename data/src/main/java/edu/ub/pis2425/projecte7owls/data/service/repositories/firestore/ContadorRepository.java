package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.domain.entities.AdviceMessageContador;

public class ContadorRepository implements IContadorRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public LiveData<Timestamp> getFechaRegistro(String uid) {
        MutableLiveData<Timestamp> liveData = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> liveData.setValue(doc.getTimestamp("fechaRegistro")))
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<Timestamp> getUltimoRegistro(String uid) {
        MutableLiveData<Timestamp> liveData = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> liveData.setValue(doc.getTimestamp("ultimoRegistro")))
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    @Override
    public LiveData<Timestamp> getFechaReset(String uid) {
        MutableLiveData<Timestamp> liveData = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> liveData.setValue(doc.getTimestamp("fechaReset")))
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    @Override
    public LiveData<Long> getResetCount(String uid) {
        MutableLiveData<Long> liveData = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    Long count = doc.contains("resetCount") ? doc.getLong("resetCount") : 0L;
                    liveData.setValue(count);
                })
                .addOnFailureListener(e -> liveData.setValue(0L));
        return liveData;
    }

    @Override
    public LiveData<List<AdviceMessageContador>> getAdviceMessages() {
        MutableLiveData<List<AdviceMessageContador>> adviceLive = new MutableLiveData<>();
        db.collection("advice").get().addOnSuccessListener(snapshot -> {
            List<AdviceMessageContador> adviceList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                AdviceMessageContador msg = doc.toObject(AdviceMessageContador.class);
                adviceList.add(msg);
            }
            adviceLive.setValue(adviceList);
        }).addOnFailureListener(e -> adviceLive.setValue(new ArrayList<>()));
        return adviceLive;
    }


    @Override
    public void resetContador(String uid, OnCompleteListener<Void> onComplete) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);

        userRef.get().addOnSuccessListener(doc -> {
            long currentCount = doc.contains("resetCount") ? doc.getLong("resetCount") : 0;
            Map<String, Object> updates = new HashMap<>();
            updates.put("fechaReset", FieldValue.serverTimestamp());
            updates.put("resetCount", currentCount + 1);

            userRef.update(updates).addOnCompleteListener(onComplete);
        }).addOnFailureListener(e -> {
            Log.e("ContadorRepository", "Error al leer documento", e);
            // Llama al callback con fallo simulado si falla antes
            onComplete.onComplete(null);
        });
    }

    @Override
    public void updateUltimoRegistro(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);
        userRef.update("ultimoRegistro", FieldValue.serverTimestamp())
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating ultimoRegistro", e));
    }
    public void resetNumQuiz(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);
        userRef.update("numQuiz", 0)
                .addOnFailureListener(e -> Log.e("Firestore", "Error reseteando numQuiz", e));
    }


}

