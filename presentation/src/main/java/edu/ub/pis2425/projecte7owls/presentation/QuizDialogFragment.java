package edu.ub.pis2425.projecte7owls.presentation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class QuizDialogFragment extends DialogFragment {

    public static QuizDialogFragment newInstance(int score) {
        QuizDialogFragment frag = new QuizDialogFragment();
        Bundle args = new Bundle();
        args.putInt("score", score);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int score = requireArguments().getInt("score", 0);

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Quiz Finished")
                .setMessage("You scored " + score + " owls.\nWhat would you like to do?")
                .setPositiveButton("Try Again", (dialog, which) -> {
                    if (getActivity() instanceof QuizActivityCallback) {
                        ((QuizActivityCallback) getActivity()).onRetryQuiz();
                    }
                })
                .setNegativeButton("Go to Profile", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                    getActivity().finish();
                })
                .setCancelable(false)
                .create();
    }

    public interface QuizActivityCallback {
        void onRetryQuiz();
    }
}
