package edu.ub.pis2425.projecte7owls.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Integer> getUserScore(String userId) {
        return userRepository.getUserScore(userId);
    }

    public void updateUserScore(String userId, int newScore) {
        userRepository.updateUserScore(userId, newScore);
    }

    public LiveData<Integer> observeUserScore(String userId) {
        return userRepository.getUserScoreLive(userId);
    }

}
