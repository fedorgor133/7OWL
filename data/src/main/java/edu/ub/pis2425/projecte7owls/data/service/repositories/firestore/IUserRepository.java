package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

public interface IUserRepository {
    MutableLiveData<Integer> getUserScore(String userId);
    MutableLiveData<Integer> getUserScoreLive(String userId);
    void updateUserScore(String userId, int newScore);
    LiveData<Map<String, Object>> getUserData(String uid);
    LiveData<Integer> getUserPoints(String uid);
    interface OnUserDataLoaded {
        void onSuccess(Map<String, Object> userData);
        void onFailure(Exception e);
    }
}

