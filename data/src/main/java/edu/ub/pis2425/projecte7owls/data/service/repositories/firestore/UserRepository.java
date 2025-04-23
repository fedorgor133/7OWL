package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private final FirebaseFirestore db;
    private final MutableLiveData<Integer> userScoreLiveData;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        userScoreLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getUserScore(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("points")) {
                        Long points = document.getLong("points");
                        userScoreLiveData.setValue(points != null ? points.intValue() : 0);
                    } else {
                        userScoreLiveData.setValue(0);
                    }
                })
                .addOnFailureListener(e -> userScoreLiveData.setValue(0));
        return userScoreLiveData;
    }

    public void updateUserScore(String userId, int newScore) {
        db.collection("usuarios").document(userId)
                .update("points", newScore)
                .addOnFailureListener(e -> {
                    // Maneja el error si es necesario
                });
    }

    public MutableLiveData<Integer> getUserScoreLive(String userId) {
        MutableLiveData<Integer> scoreLiveData = new MutableLiveData<>();

        db.collection("usuarios").document(userId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Error loading points", e);
                scoreLiveData.postValue(0); // Error case: Defaults to 0
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.contains("points")) {
                Long points = documentSnapshot.getLong("points");
                scoreLiveData.postValue(points != null ? points.intValue() : 0);
            } else {
                scoreLiveData.postValue(0); // Default if no points found
            }
        });

        return scoreLiveData;
    }

}

