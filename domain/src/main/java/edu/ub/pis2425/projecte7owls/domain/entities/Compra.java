package edu.ub.pis2425.projecte7owls.domain.entities;

public class Compra {
    private String nombre;
    private int precio;
    private String imagen;
    private String fechaCompra;

    public Compra() {
    }

    public Compra(String nombre, int precio, String imagen, String fechaCompra) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.fechaCompra = fechaCompra;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }
}

