package edu.ub.pis2425.projecte7owls.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.ContadorRepository;
import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.IContadorRepository;
import edu.ub.pis2425.projecte7owls.domain.entities.AdviceMessage;
import edu.ub.pis2425.projecte7owls.domain.entities.AdviceMessageContador;

public class ContadorViewModel extends ViewModel {
    private final IContadorRepository repository = new ContadorRepository();

    public LiveData<Timestamp> getFechaRegistro(String uid) {
        return repository.getFechaRegistro(uid);
    }
    public LiveData<Timestamp> getUltimoRegistro(String uid) {
        return repository.getUltimoRegistro(uid);
    }
    public LiveData<Timestamp> getFechaReset(String uid) {
        return repository.getFechaReset(uid);
    }

    public LiveData<Long> getResetCount(String uid) {
        return repository.getResetCount(uid);
    }

    public LiveData<List<AdviceMessageContador>> getAdviceMessages() {
        return repository.getAdviceMessages();
    }

    public void resetContador(String uid, OnCompleteListener<Void> callback) {
        repository.resetContador(uid, callback);
    }
    public void updateUltimoRegistro(String uid) {
        repository.updateUltimoRegistro(uid);
    }
    public void resetNumQuiz(String uid) {
        repository.resetNumQuiz(uid);
    }

    public void setFechaReset(String uid, Timestamp fecha) {
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .update("fechaReset", fecha)
                .addOnSuccessListener(aVoid -> Log.d("ViewModel", "FechaReset creada con éxito"))
                .addOnFailureListener(e -> Log.e("ViewModel", "Error al crear fechaReset", e));
    }



}
