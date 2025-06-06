package modelo;

import java.io.Serializable;
import java.util.List;

public class Semilla implements Serializable {
    private int id;
    private String nombre;
    private List<Estacion> estaciones;
    private int diasCrecimiento;
    private int precioCompraSemilla;
    private int precioVentaFruto;
    private int maxFrutos;

    public Semilla(int id, String nombre, List<Estacion> estaciones, int diasCrecimiento, int precioCompraSemilla, int precioVentaFruto, int maxFrutos) {
        this.id = id;
        this.nombre = nombre;
        this.estaciones = estaciones;
        this.diasCrecimiento = diasCrecimiento;
        this.precioCompraSemilla = precioCompraSemilla;
        this.precioVentaFruto = precioVentaFruto;
        this.maxFrutos = maxFrutos;
    }

    @Override
    public String toString() {
        return nombre + " (Precio: " + precioCompraSemilla + "€)";
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }


    public List<Estacion> getEstaciones() {
        return estaciones;
    }

    public int getDiasCrecimiento() {
        return diasCrecimiento;
    }

    public int getPrecioCompraSemilla() {
        return precioCompraSemilla;
    }

    public int getPrecioVentaFruto() {
        return precioVentaFruto;
    }

    public int getMaxFrutos() {
        return maxFrutos;
    }
}
