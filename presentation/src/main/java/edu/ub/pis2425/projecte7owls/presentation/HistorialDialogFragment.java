package edu.ub.pis2425.projecte7owls.presentation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.Compra;
import edu.ub.pis2425.projecte7owls.presentation.adapters.PurchaseHistoryAdapter;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.ShoppingViewModel;

public class HistorialDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private PurchaseHistoryAdapter adapter;
    private List<Compra> historial;
    private FirebaseFirestore db;
    private String uid;

    private ShoppingViewModel shoppingViewModel;

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
        Button buttonCerrar = view.findViewById(R.id.buttonCerrar);
        buttonCerrar.setOnClickListener(v -> dismiss());
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShoppingViewModel.class);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        shoppingViewModel.getPurchaseHistory(uid).observe(getViewLifecycleOwner(), compras -> {
            historial.clear();
            historial.addAll(compras);
            adapter.notifyDataSetChanged();
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //expandir ancho del diálogo al máximo del padre
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}

