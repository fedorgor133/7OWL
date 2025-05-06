package edu.ub.pis2425.projecte7owls.presentation.repositories;

import edu.ub.pis2425.projecte7owls.presentation.model.User;
import edu.ub.pis2425.projecte7owls.presentation.utils.Callback;


public interface UserRepository {
    void registerUser(String email, String password, Callback<String> callback);
    void loginUser(String email, String password, Callback<String> callback);
    void getUserData(String uid, Callback<User> callback);
    void resetStartDate(String uid, Callback<Void> callback);
    void getUserPoints(String uid, Callback<Integer> callback);
    void updateUserPoints(String uid, int newPoints, Callback<Void> callback);
}

