package modelo;

import java.io.Serializable;

public class Alimento implements Serializable {
    private int id;
    private String nombre;
    private double precioCompra;
    private int cantidadDisponible;

    // Constructor simple
    public Alimento(String nombre, double precio) {
        this.nombre = nombre;
        this.precioCompra = precio;
        this.cantidadDisponible = 0;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    @Override
    public String toString() {
        return "Alimento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioCompra=" + precioCompra +
                ", cantidadDisponible=" + cantidadDisponible +
                '}';
    }
}
