package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class QuizRepository implements IQuizRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void getUserQuestions(String uid, int limit, OnQuestionsLoaded callback) {
        db.collection("preguntas_usuari")
                .whereEqualTo("userId", uid)
                .whereEqualTo("contestadaCorrecta", false)
                .limit(limit)
                .get()
                .addOnSuccessListener(query -> callback.onSuccess(query.getDocuments()))
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void updateQuestionResult(String questionId, boolean correct) {
        Map<String, Object> data = new HashMap<>();
        data.put("contestadaCorrecta", correct);
        db.collection("preguntas_usuari").document(questionId).update(data);
    }

    @Override
    public void saveQuizResult(String uid, int score) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", uid);
        result.put("score", score);
        result.put("timestamp", new Date());
        db.collection("results").add(result);
    }

    @Override
    public void getUserPoints(String uid, OnPointsLoaded callback) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    Long points = doc.getLong("points");
                    callback.onLoaded(points != null ? points.intValue() : 0);
                })
                .addOnFailureListener(callback::onError);
    }
    public LiveData<Integer> getNumQuiz(String uid) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    Long numQuizL = document.getLong("numQuiz");
                    int numQuiz = numQuizL.intValue();
                    liveData.setValue(numQuiz);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener numQuiz del usuario", e));
        return liveData;
    }

    public LiveData<Map<String, Object>> getUserData(String uid) {
        MutableLiveData<Map<String, Object>> dataLive = new MutableLiveData<>();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> dataLive.setValue(doc.getData()))
                .addOnFailureListener(e -> dataLive.setValue(null));
        return dataLive;
    }

    public void resetAllUserQuestions(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("preguntas_usuari")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        db.collection("preguntas_usuari")
                                .document(doc.getId())
                                .update("contestadaCorrecta", false)
                                .addOnFailureListener(e ->
                                        Log.e("QuizRepository", "Error updating question " + doc.getId(), e)
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("QuizRepository", "Error loading questions for reset", e)
                );
    }
    public void increaseNumQuiz(String uid,long numQuiz){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(uid)
                .update("numQuiz",numQuiz+1)
                .addOnFailureListener(e -> Log.e("Firestore", "Error incrementando numQuiz", e));
    }

}

