package edu.ub.pis2425.projecte7owls.domain.entities;

public class Product {
    private String id;
    private String name;
    private String description;
    private int price;
    private String imageUrl; // Ahora es URL de imagen online

    // Necesario para Firestore
    public Product() {}

    public Product(String name, String description, int price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }
    public Product(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}

