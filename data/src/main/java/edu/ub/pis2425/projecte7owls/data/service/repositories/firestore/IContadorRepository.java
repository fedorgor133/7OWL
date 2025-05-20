package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;

import java.util.List;

import edu.ub.pis2425.projecte7owls.domain.entities.AdviceMessageContador;

public interface IContadorRepository {
    LiveData<Timestamp> getFechaRegistro(String uid);
    LiveData<Timestamp> getFechaReset(String uid);
    LiveData<Long> getResetCount(String uid);
    LiveData<List<AdviceMessageContador>> getAdviceMessages();
    void resetContador(String uid, OnCompleteListener<Void> onComplete);


}

