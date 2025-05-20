package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.domain.entities.Compra;
import edu.ub.pis2425.projecte7owls.domain.entities.Product;

public class ShoppingRepository implements IShoppingRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public LiveData<List<Product>> getAvailableProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

        db.collection("productos").get().addOnSuccessListener(snapshot -> {
            List<Product> products = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                products.add(doc.toObject(Product.class));
            }
            productsLiveData.setValue(products);
        }).addOnFailureListener(e -> productsLiveData.setValue(Collections.emptyList()));

        return productsLiveData;
    }

    @Override
    public void registerPurchase(String uid, Map<Product, Integer> products, String fecha, OnCompleteListener<Void> onComplete) {
        CollectionReference historialRef = db.collection("usuarios").document(uid).collection("historial_compras");

        WriteBatch batch = db.batch();

        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            int cantidad = entry.getValue();

            for (int i = 0; i < cantidad; i++) {
                Map<String, Object> compra = new HashMap<>();
                compra.put("nombre", product.getName());
                compra.put("precio", product.getPrice());
                compra.put("imagen", product.getImageUrl());
                compra.put("fechaCompra", fecha);

                DocumentReference newDoc = historialRef.document();
                batch.set(newDoc, compra);
            }
        }

        batch.commit().addOnCompleteListener(onComplete);
    }

    @Override
    public LiveData<List<Compra>> getPurchaseHistory(String uid) {
        MutableLiveData<List<Compra>> historialLive = new MutableLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(uid)
                .collection("historial_compras")
                .get()
                .addOnSuccessListener(query -> {
                    List<Compra> historial = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Compra compra = doc.toObject(Compra.class);
                        historial.add(compra);
                    }
                    historialLive.setValue(historial);
                })
                .addOnFailureListener(e -> {
                    historialLive.setValue(new ArrayList<>()); // vacío si falla
                });

        return historialLive;
    }


}

