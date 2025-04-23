package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository implements IUserRepository {
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public MutableLiveData<Integer> getUserScore(String userId) {
        MutableLiveData<Integer> userScoreLiveData = new MutableLiveData<>();

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
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading points", e);
                    userScoreLiveData.setValue(0);
                });

        return userScoreLiveData;
    }

    @Override
    public MutableLiveData<Integer> getUserScoreLive(String userId) {
        MutableLiveData<Integer> scoreLiveData = new MutableLiveData<>();

        db.collection("usuarios").document(userId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Error loading points", e);
                scoreLiveData.postValue(0);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.contains("points")) {
                Long points = documentSnapshot.getLong("points");
                scoreLiveData.postValue(points != null ? points.intValue() : 0);
            } else {
                scoreLiveData.postValue(0);
            }
        });

        return scoreLiveData;
    }

    @Override
    public void updateUserScore(String userId, int newScore) {
        db.collection("usuarios").document(userId)
                .update("points", newScore)
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating points", e);
                });
    }
}
