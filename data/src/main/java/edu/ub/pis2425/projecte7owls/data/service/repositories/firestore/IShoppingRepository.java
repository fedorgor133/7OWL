package edu.ub.pis2425.projecte7owls.data.service.repositories.firestore;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.domain.entities.Compra;
import edu.ub.pis2425.projecte7owls.domain.entities.Product;

public interface IShoppingRepository {
    LiveData<List<Product>> getAvailableProducts();
    void registerPurchase(String uid, Map<Product, Integer> products, String fecha, OnCompleteListener<Void> onComplete);
    LiveData<List<Compra>> getPurchaseHistory(String uid);

}
