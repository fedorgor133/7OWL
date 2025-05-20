package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface IQuizRepository {
    void getUserQuestions(String uid, int limit, OnQuestionsLoaded callback);
    void updateQuestionResult(String questionId, boolean correct);
    void saveQuizResult(String uid, int score);
    void getUserPoints(String uid, OnPointsLoaded callback);

    void resetAllUserQuestions(String uid);
    interface OnQuestionsLoaded {
        void onSuccess(List<DocumentSnapshot> questions);
        void onFailure(Exception e);
    }

    interface OnPointsLoaded {
        void onLoaded(int points);
        void onError(Exception e);
    }
}
