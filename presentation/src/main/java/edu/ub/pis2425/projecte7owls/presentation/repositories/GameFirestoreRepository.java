package edu.ub.pis2425.projecte7owls.presentation.repositories;
import edu.ub.pis2425.projecte7owls.presentation.utils.Callback;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameFirestoreRepository implements GameRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void updateGameResult(String uid, int newPoints, Callback<Void> callback) {
        db.collection("usuarios").document(uid)
                .update("points", newPoints, "lastUpdate", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}
