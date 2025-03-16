import java.io.Serializable;

public class Alimento implements Serializable {
    private int id;
    private String nombre;
    private double precioCompra;
    private int cantidadDisponible;

    // Constructor
    public Alimento(int id, String nombre, double precioCompra, int cantidadDisponible) {
        this.id = id;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.cantidadDisponible = cantidadDisponible;
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
        return "Alimento{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", precioCompra=" + precioCompra + ", cantidadDisponible=" + cantidadDisponible + '}';
    }
}
