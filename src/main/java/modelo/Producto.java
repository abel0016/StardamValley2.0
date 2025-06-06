package modelo;

import java.io.Serializable;

public class Producto implements Serializable {
    private int id;
    private String nombre;
    private double precioVenta;
    private int cantidadDisponible;

    public Producto(int id, String nombre, double precioVenta, int cantidadDisponible){
        this.id = id;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.cantidadDisponible = cantidadDisponible;
    }
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public double getPrecioVenta() {
        return precioVenta;
    }
    public int getCantidadDisponible() {
        return cantidadDisponible;
    }
    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
    @Override
    public String toString() {
        return "modelo.Producto{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", precioVenta=" + precioVenta + ", cantidadDisponible=" + cantidadDisponible + '}';
    }
}
