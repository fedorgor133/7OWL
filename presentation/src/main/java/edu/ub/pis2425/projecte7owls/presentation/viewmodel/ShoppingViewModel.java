package edu.ub.pis2425.projecte7owls.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.IShoppingRepository;
import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.ShoppingRepository;
import edu.ub.pis2425.projecte7owls.domain.entities.Compra;
import edu.ub.pis2425.projecte7owls.domain.entities.Product;

public class ShoppingViewModel extends ViewModel {
    private final IShoppingRepository repository = new ShoppingRepository();

    public LiveData<List<Product>> getProducts() {
        return repository.getAvailableProducts();
    }

    public void registerPurchase(String uid, Map<Product, Integer> products, String fecha, OnCompleteListener<Void> callback) {
        repository.registerPurchase(uid, products, fecha, callback);
    }

    public LiveData<List<Compra>> getPurchaseHistory(String uid) {
        return repository.getPurchaseHistory(uid);
    }

}

