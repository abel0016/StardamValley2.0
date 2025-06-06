package modelo;

import java.io.Serializable;

/**
 * La clase modelo.Producto representa un producto disponible en la granja.
 * Cada producto tiene un identificador único, nombre, precio de venta y cantidad disponible.
 */
public class Producto implements Serializable {
    private int id;
    private String nombre;
    private double precioVenta;
    private int cantidadDisponible;

    /**
     * Constructor de la clase modelo.Producto.
     *
     * @param id Identificador único del producto.
     * @param nombre Nombre del producto.
     * @param precioVenta Precio de venta del producto.
     * @param cantidadDisponible Cantidad disponible del producto en el inventario.
     */
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
