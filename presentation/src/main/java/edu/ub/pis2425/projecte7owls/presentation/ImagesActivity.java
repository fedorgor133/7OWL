package edu.ub.pis2425.projecte7owls.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.databinding.ActivityImagesBinding;

public class ImagesActivity extends AppCompatActivity {
    private ActivityImagesBinding binding;
    private int selectedImage = 0;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.owl1.setOnClickListener(v -> selectImage(R.drawable.owl1));
        binding.owl2.setOnClickListener(v -> selectImage(R.drawable.owl2));
        binding.owl3.setOnClickListener(v -> selectImage(R.drawable.owl3));
        binding.owl4.setOnClickListener(v -> selectImage(R.drawable.owl4));
        binding.owl5.setOnClickListener(v -> selectImage(R.drawable.owl5));
        binding.owl6.setOnClickListener(v -> selectImage(R.drawable.owl6));
        binding.owl7.setOnClickListener(v -> selectImage(R.drawable.owl7));

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();


        binding.btnSave.setOnClickListener(v -> {
            if (selectedImage != 0) {
                Intent intent = new Intent();
                intent.putExtra("selectedImage", selectedImage);
                saveImageIdToFirestore(selectedImage);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Select an image to save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage(int numImage) {
        selectedImage = numImage;
        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
    }
    private void saveImageIdToFirestore(int imageId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("imagenPerfil", imageId);

        db.collection("usuarios").document(uid).update(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
                });
    }

}

