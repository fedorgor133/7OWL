package edu.ub.pis2425.projecte7owls.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.UserViewModel;

public class ScoreDialogFragment extends DialogFragment {

    private static final String ARG_UID = "uid";
    private String uid;
    private UserViewModel userViewModel;

    public static ScoreDialogFragment newInstance(String uid) {
        ScoreDialogFragment fragment = new ScoreDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_score_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
        }

        TextView historyTextView = view.findViewById(R.id.historyTextView);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getScoreHistory(uid).observe(getViewLifecycleOwner(), historyList -> {
            StringBuilder historyText = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            for (Map<String, Object> entry : historyList) {
                Timestamp ts = (Timestamp) entry.get("timestamp");
                Long scoreChange = (Long) entry.get("scoreChange");

                if (ts != null && scoreChange != null) {
                    Date date = ts.toDate();
                    String formattedChange = (scoreChange > 0 ? "+" : "") + scoreChange;
                    historyText.append(sdf.format(date))
                            .append(": ")
                            .append(formattedChange)
                            .append(" puntos\n");
                }
            }

            if (historyText.length() == 0) {
                historyTextView.setText("No hay historial disponible.");
            } else {
                historyTextView.setText(historyText.toString());
            }
        });
    }
}

