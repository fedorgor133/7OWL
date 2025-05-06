package edu.ub.pis2425.projecte7owls.presentation.utils;

public interface Callback<T> {
    void onSuccess(T result);
    void onError(Throwable error);
}
