package edu.ub.pis2425.projecte7owls.presentation.repositories;

import edu.ub.pis2425.projecte7owls.presentation.model.Question;
import edu.ub.pis2425.projecte7owls.presentation.utils.Callback;



import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuestionFirestoreRepository implements QuestionRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void getUnansweredQuestions(String uid, int limit, Callback<List<Question>> callback) {
        db.collection("preguntas_usuari")
                .whereEqualTo("userId", uid)
                .whereEqualTo("contestadaCorrecta", false)
                .limit(limit)
                .get()
                .addOnSuccessListener(query -> {
                    List<Question> questions = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        questions.add(doc.toObject(Question.class));
                    }
                    callback.onSuccess(questions);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void markQuestionAnswered(String questionId, boolean isCorrect, Callback<Void> callback) {
        db.collection("preguntas_usuari").document(questionId)
                .update("contestadaCorrecta", isCorrect)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}
