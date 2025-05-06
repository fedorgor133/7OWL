package edu.ub.pis2425.projecte7owls.presentation.repositories;


import java.util.List;

import edu.ub.pis2425.projecte7owls.presentation.model.Question;
import edu.ub.pis2425.projecte7owls.presentation.utils.Callback;


public interface QuestionRepository {
    void getUnansweredQuestions(String uid, int limit, Callback<List<Question>> callback);
    void markQuestionAnswered(String questionId, boolean isCorrect, Callback<Void> callback);
}
