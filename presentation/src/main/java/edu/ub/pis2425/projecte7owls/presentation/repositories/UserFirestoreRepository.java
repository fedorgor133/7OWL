package edu.ub.pis2425.projecte7owls.presentation.repositories;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.ub.pis2425.projecte7owls.presentation.model.User;
import edu.ub.pis2425.projecte7owls.presentation.utils.Callback;


public class UserFirestoreRepository implements UserRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void registerUser(String email, String password, Callback<String> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void loginUser(String email, String password, Callback<String> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getUserData(String uid, Callback<User> callback) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    callback.onSuccess(user);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void resetStartDate(String uid, Callback<Void> callback) {
        db.collection("usuarios").document(uid)
                .update("fechaRegistro", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getUserPoints(String uid, Callback<Integer> callback) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    Long points = doc.getLong("points");
                    callback.onSuccess(points != null ? points.intValue() : 0);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void updateUserPoints(String uid, int newPoints, Callback<Void> callback) {
        db.collection("usuarios").document(uid)
                .update("points", newPoints)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}
