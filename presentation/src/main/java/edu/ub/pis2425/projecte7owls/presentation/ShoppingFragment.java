package edu.ub.pis2425.projecte7owls.presentation;

import android.os.Bundle;
import android.util.Log; // Añade Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.Product;
import edu.ub.pis2425.projecte7owls.presentation.adapters.ProductAdapter;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.ShoppingViewModel;
import edu.ub.pis2425.projecte7owls.presentation.viewmodel.UserViewModel;

public class ShoppingFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String TAG = "ShoppingFragment";

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private TextView textViewTotal;
    private TextView pointsTextView;
    private Button buttonCheckout;
    private int totalPrice = 0;

    private UserViewModel userViewModel;
    private String uid;
    private ShoppingViewModel shoppingViewModel;
    private int currentPoints = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        pointsTextView = view.findViewById(R.id.pointsTextView);
        shoppingViewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        Button btnHistorial =view.findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(v -> {
            Log.d("ShoppingFragment", "Botón Historial clickeado");
            Toast.makeText(getContext(), "Botón Historial pulsado", Toast.LENGTH_SHORT).show();
            HistorialDialogFragment dialog = new HistorialDialogFragment();
            dialog.show(requireActivity().getSupportFragmentManager(), "HistorialDialog");
        });




        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        shoppingViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productList.clear();
            productList.addAll(products);
            adapter.notifyDataSetChanged();
        });



        // Observa los puntos del usuario
        userViewModel.observeUserScore(uid).observe(getViewLifecycleOwner(), score -> {
            currentPoints = score != null ? score : 0;
            pointsTextView.setText("Your points: " + currentPoints + " owls");
        });


        buttonCheckout.setOnClickListener(v -> {
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), "Comprando productos: " + totalPrice + " points", android.widget.Toast.LENGTH_SHORT).show();
                handlePurchase(totalPrice);
                totalPrice = 0;
                textViewTotal.setText("Total: 0 owls");

            }
        });

        return view;
    }

    @Override
    public void onProductClick(Product product, int quantity) {
        totalPrice += product.getPrice();
        textViewTotal.setText(String.format("Total: %d points", totalPrice));
    }


    private void handlePurchase(int cost) {
        if (currentPoints >= cost) {
            int updatedPoints = currentPoints - cost;
            userViewModel.updateUserScore(uid, updatedPoints);

            String fechaCompra = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Map<Product, Integer> selectedProducts = adapter.getSelectedProducts();

            shoppingViewModel.registerPurchase(uid, selectedProducts, fechaCompra, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show();
                    adapter.clearSelection();
                    totalPrice = 0;
                    textViewTotal.setText("Total: 0 points");
                } else {
                    Toast.makeText(getContext(), "Error en la compra", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getContext(), "No tienes puntos suficientes", Toast.LENGTH_SHORT).show();
        }
    }







}
