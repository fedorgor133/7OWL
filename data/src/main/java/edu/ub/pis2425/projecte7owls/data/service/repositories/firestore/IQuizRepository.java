package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface IQuizRepository {
    void getUserQuestions(String uid, int limit, OnQuestionsLoaded callback);
    void updateQuestionResult(String questionId, boolean correct);
    void saveQuizResult(String uid, int score);
    void getUserPoints(String uid, OnPointsLoaded callback);
    LiveData<Integer> getNumQuiz(String uid);
    void resetAllUserQuestions(String uid);
    void increaseNumQuiz(String uid, long numquiz);
    interface OnQuestionsLoaded {
        void onSuccess(List<DocumentSnapshot> questions);
        void onFailure(Exception e);
    }

    interface OnPointsLoaded {
        void onLoaded(int points);
        void onError(Exception e);
    }
}
