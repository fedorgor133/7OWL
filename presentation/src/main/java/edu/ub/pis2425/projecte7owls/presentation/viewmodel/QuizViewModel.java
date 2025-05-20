package edu.ub.pis2425.projecte7owls.presentation.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.QuizRepository;


public class QuizViewModel extends ViewModel {
    private final QuizRepository quizRepository;

    private final MutableLiveData<List<DocumentSnapshot>> questionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> pointsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public QuizViewModel() {
        this.quizRepository = new QuizRepository();
    }

    public LiveData<List<DocumentSnapshot>> getQuestions() {
        return questionsLiveData;
    }

    public LiveData<Integer> getUserPoints() {
        return pointsLiveData;
    }

    public LiveData<String> getErrors() {
        return errorLiveData;
    }

    public void loadQuestions(String uid, int totalQuestions) {
        quizRepository.getUserQuestions(uid, totalQuestions, new QuizRepository.OnQuestionsLoaded() {
            @Override
            public void onSuccess(List<DocumentSnapshot> questions) {
                questionsLiveData.setValue(questions);
            }

            @Override
            public void onFailure(Exception e) {
                errorLiveData.setValue("Error loading questions: " + e.getMessage());
            }
        });
    }

    public void loadUserPoints(String uid) {
        quizRepository.getUserPoints(uid, new QuizRepository.OnPointsLoaded() {
            @Override
            public void onLoaded(int points) {
                pointsLiveData.setValue(points);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue("Error loading points: " + e.getMessage());
            }
        });
    }

    public void updateQuestionResult(String questionId, boolean correct) {
        quizRepository.updateQuestionResult(questionId, correct);
    }

    public void saveQuizResult(String uid, int score) {
        quizRepository.saveQuizResult(uid, score);
    }

    public void resetUserQuestions(String uid) {
        quizRepository.resetAllUserQuestions(uid);
    }

}

