package edu.ub.pis2425.firestorerepositoryexample.data.repositories.firestore;

import com.google.firebase.firestore.FirebaseFirestore;

import edu.ub.pis2425.firestorerepositoryexample.data.dtos.firestore.ClientFirestoreDto;
import edu.ub.pis2425.firestorerepositoryexample.data.mappers.DTOToDomainMapper;
import edu.ub.pis2425.firestorerepositoryexample.domain.model.entities.Client;
import edu.ub.pis2425.firestorerepositoryexample.domain.repositories.ClientRepository;

/**
 * Firebase repository for clients.
 */
public class ClientFirestoreRepository implements ClientRepository {
  /* Constants */
  private static final String CLIENTS_COLLECTION_NAME = "clients";
  /* Attributes */
  private final FirebaseFirestore db;
  private final DTOToDomainMapper DTOToDomainMapper;

  /**
   * Empty constructor
   */
  public ClientFirestoreRepository() {
    db = FirebaseFirestore.getInstance();
    DTOToDomainMapper = new DTOToDomainMapper();
  }

  /**
   * Add a client to the Firebase CloudFirestore.
   *
   * @param client The client to add.
   * @param callback The callback to be called when the operation is done.
   */
  public void add(Client client, Callback<Void> callback) {
    ClientFirestoreDto clientDto = DTOToDomainMapper.map(client, ClientFirestoreDto.class);

    db.collection(CLIENTS_COLLECTION_NAME)
        .document(client.getId().toString())
        .set(clientDto)
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error adding client"));
        })
        .addOnSuccessListener(ignored -> {
          callback.onSuccess(null);
        });
  }

  /**
   * Get a client by id.
   *
   * @param id The client id.
   * @param callback The callback to be called when the operation is done.
   */
  @SuppressWarnings("unused")
  public void getById(String id, Callback<Client> callback) {
    db.collection(CLIENTS_COLLECTION_NAME)
        .document(id)
        .get()
        .addOnFailureListener(exception -> {
          callback.onError(new Throwable("Error getting client"));
        })
        .addOnSuccessListener(ds -> {
          if (ds.exists()) {
            ClientFirestoreDto clientDto = ds.toObject(ClientFirestoreDto.class);
            Client client = DTOToDomainMapper.map(clientDto, Client.class);
            callback.onSuccess(client);
          } else {
            callback.onError(new Throwable("Client not found"));
          }
        });
  }

}
