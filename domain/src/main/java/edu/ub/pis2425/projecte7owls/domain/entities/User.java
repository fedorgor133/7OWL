package edu.ub.pis2425.projecte7owls.domain.entities;

public class User {
    /* Attributes */
    private String id;
    private String username;
    private String password;

    /**
     * Constructor.
     * @param id id del user
     * @param password password del user
     */
    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Contructor buit
     */
    @SuppressWarnings("unused")
    public User() { }

    /**
     * Obte id del user
     */
    public String getId() {
        return id;
    }

    /**
     * Obté username del user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obté password del user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obté photoUrl del user
     */
}
