package edu.ub.pis2425.projecte7owls.presentation;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.Compra;
import edu.ub.pis2425.projecte7owls.presentation.adapters.PurchaseHistoryAdapter;

public class HistorialDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private PurchaseHistoryAdapter adapter;
    private List<Compra> historial;
    private FirebaseFirestore db;
    private String uid;

    public HistorialDialogFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_compras, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historial = new ArrayList<>();
        adapter = new PurchaseHistoryAdapter(historial);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cargarHistorial();
        Button buttonCerrar = view.findViewById(R.id.buttonCerrar);
        buttonCerrar.setOnClickListener(v -> dismiss());


        return view;
    }

    private void cargarHistorial() {
        db.collection("usuarios")
                .document(uid)
                .collection("historial_compras")
                .get()
                .addOnSuccessListener(query -> {
                    historial.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Compra compra = doc.toObject(Compra.class);
                        historial.add(compra);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("HistorialDialog", "Error cargando historial", e));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Expandir ancho del diálogo al máximo del padre
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}

