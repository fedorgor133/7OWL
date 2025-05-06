package edu.ub.pis2425.projecte7owls.presentation.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte7owls.data.service.repositories.firestore.UserRepository;

public class User {
    private String uid;
    private String email;
    private String name;
    private int points;

    public User() {}

    public User(String uid, String email, String name, int points) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.points = points;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
