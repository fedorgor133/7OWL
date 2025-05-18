package edu.ub.pis2425.firestorerepositoryexample.data.repositories.firestore;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.firestorerepositoryexample.data.dtos.firestore.ProductFirestoreDto;
import edu.ub.pis2425.firestorerepositoryexample.data.mappers.DTOToDomainMapper;
import edu.ub.pis2425.firestorerepositoryexample.domain.model.entities.Product;
import edu.ub.pis2425.firestorerepositoryexample.domain.repositories.ProductRepository;

public class ProductFirestoreRepository implements ProductRepository {
  /* Constants */
  private static final String CLIENTS_COLLECTION_NAME = "products";
  /* Attributes */
  private final FirebaseFirestore db;
  private final DTOToDomainMapper DTOToDomainMapper;

  /**
   * Empty constructor
   */
  public ProductFirestoreRepository() {
    db = FirebaseFirestore.getInstance();
    DTOToDomainMapper = new DTOToDomainMapper();
  }

  /**
   * Add a product.
   *
   * @param product The product to be added.
   * @param callback The callback to be called when the operation is done.
   */
  @SuppressWarnings("unused")
  public void add(Product product, Callback<Boolean> callback) {
    ProductFirestoreDto productDto = DTOToDomainMapper.map(product, ProductFirestoreDto.class);
    db.collection(CLIENTS_COLLECTION_NAME)
        .add(productDto)
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error adding product"));
        })
        .addOnSuccessListener(documentReference -> {
          callback.onSuccess(true);
        });
  }

  /**
   * Get a product by id.
   *
   * @param id The product id.
   * @param callback The callback to be called when the operation is done.
   */
  @SuppressWarnings("unused")
  public void getById(String id, Callback<Product> callback) {
    db.collection(CLIENTS_COLLECTION_NAME)
        .document(id)
        .get()
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error getting product by id"));
        })
        .addOnSuccessListener(documentSnapshot -> {
          ProductFirestoreDto productDto = documentSnapshot.toObject(ProductFirestoreDto.class);
          Product product = DTOToDomainMapper.map(productDto, Product.class);
          callback.onSuccess(product);
        });
  }

  /**
   * Get a product by name.
   * @param name The product name.
   * @param callback The callback to be called when the operation is done.
   */
  @SuppressWarnings("unused")
  public void getByName(String name, Callback<List<Product>> callback) {
    String nameLowerCase = name.toLowerCase();
    db.collection(CLIENTS_COLLECTION_NAME)
        .orderBy("nameLowerCase")
        .startAt(nameLowerCase)
        .endAt(nameLowerCase + "\uf8ff")
        .get()
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error getting product by name"));
        })
        .addOnSuccessListener(queryDocumentSnapshots -> {
          List<Product> products = new ArrayList<>();
          for (ProductFirestoreDto productDto : queryDocumentSnapshots.toObjects(ProductFirestoreDto.class)) {
            Product product = DTOToDomainMapper.map(productDto, Product.class);
            products.add(product);
          }
          callback.onSuccess(products);
        });
  }

  /**
   * Get all the products.
   * @param callback The callback to be called when the operation is done.
   */
  @SuppressWarnings("unused")
  public void getAll(Callback<List<Product>> callback) {
    db.collection(CLIENTS_COLLECTION_NAME)
        .get()
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error getting all products"));
        })
        .addOnSuccessListener(queryDocumentSnapshots -> {
          List<Product> products = new ArrayList<>();
          for (ProductFirestoreDto productDto : queryDocumentSnapshots.toObjects(ProductFirestoreDto.class)) {
            Product product = DTOToDomainMapper.map(productDto, Product.class);
            products.add(product);
          }
          callback.onSuccess(products);
        });
  }

  /* EXERCICI 2 */
  /**
   * Remove a product.
   * @param product The product to be removed.
   * @param callback The callback to be called when the operation is done.
   */
  @SuppressWarnings("unused")
  public void remove(Product product, Callback<Void> callback) {
    db.collection(CLIENTS_COLLECTION_NAME)
        .document(product.getId().toString())
        .delete()
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error removing product"));
        })
        .addOnSuccessListener(ignored -> {
          callback.onSuccess(null);
        });
  }
  // ...
}
