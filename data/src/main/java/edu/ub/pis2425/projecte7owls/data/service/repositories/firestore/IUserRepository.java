package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;


import androidx.lifecycle.MutableLiveData;

public interface IUserRepository {
    MutableLiveData<Integer> getUserScore(String userId);
    MutableLiveData<Integer> getUserScoreLive(String userId);
    void updateUserScore(String userId, int newScore);
}

