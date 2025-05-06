package edu.ub.pis2425.projecte7owls.presentation.repositories;

import edu.ub.pis2425.projecte7owls.presentation.utils.Callback;


public interface GameRepository {
    void updateGameResult(String uid, int newPoints, Callback<Void> callback);
}
