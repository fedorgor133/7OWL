package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository implements IUserRepository {
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addScoreHistory(String userId, int scoreChange, String source) {
        Map<String, Object> scoreEntry = new HashMap<>();
        scoreEntry.put("scoreChange", scoreChange);
        scoreEntry.put("source", source); // "Quiz" o "Roulette"
        scoreEntry.put("timestamp", new java.util.Date());

        db.collection("usuarios").document(userId).collection("scoreHistory")
                .add(scoreEntry)
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding score history", e));
    }


    public MutableLiveData<List<Map<String, Object>>> getScoreHistory(String userId) {
        MutableLiveData<List<Map<String, Object>>> historyLiveData = new MutableLiveData<>();

        db.collection("usuarios").document(userId).collection("scoreHistory")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> historyList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        historyList.add(doc.getData());
                    }
                    historyLiveData.setValue(historyList);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading score history", e));

        return historyLiveData;
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


    public LiveData<Map<String, Object>> getUserData(String uid) {
        MutableLiveData<Map<String, Object>> dataLive = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> dataLive.setValue(doc.getData()))
                .addOnFailureListener(e -> dataLive.setValue(null));
        return dataLive;
    }


    public LiveData<Integer> getUserPoints(String uid) {
        MutableLiveData<Integer> pointsLive = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    Long points = doc.getLong("points");
                    pointsLive.setValue(points != null ? points.intValue() : 0);
                })
                .addOnFailureListener(e -> pointsLive.setValue(0));
        return pointsLive;
    }


}
