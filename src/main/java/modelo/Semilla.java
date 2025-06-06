package modelo;

import java.io.Serializable;
import java.util.List;

/**
 * La clase modelo.Semilla representa una semilla que se puede plantar en la granja
 * dentro del juego Stardam Valley. Contiene información sobre la semilla,
 * como su nombre, el precio de compra y venta, el número de días necesarios
 * para que crezca y las estaciones en las que puede ser sembrada.
 */
public class Semilla implements Serializable {
    private int id;
    private String nombre;
    private List<Estacion> estaciones;
    private int diasCrecimiento;
    private int precioCompraSemilla;
    private int precioVentaFruto;
    private int maxFrutos;

    /**
     * Constructor de la clase modelo.Semilla.
     *
     * @param id Identificador único de la semilla.
     * @param nombre Nombre de la semilla.
     * @param estaciones Lista de estaciones en las que se puede cultivar la semilla.
     * @param diasCrecimiento Días necesarios para que la semilla crezca.
     * @param precioCompraSemilla Precio de compra de la semilla.
     * @param precioVentaFruto Precio de venta de los frutos.
     * @param maxFrutos Máximo de frutos que puede producir.
     */
    public Semilla(int id, String nombre, List<Estacion> estaciones, int diasCrecimiento, int precioCompraSemilla, int precioVentaFruto, int maxFrutos) {
        this.id = id;
        this.nombre = nombre;
        this.estaciones = estaciones;
        this.diasCrecimiento = diasCrecimiento;
        this.precioCompraSemilla = precioCompraSemilla;
        this.precioVentaFruto = precioVentaFruto;
        this.maxFrutos = maxFrutos;
    }

    /**
     * Devuelve una representación en cadena de la semilla.
     *
     * @return Una cadena que representa el nombre de la semilla y su precio de compra.
     */
    @Override
    public String toString() {
        return nombre + " (Precio: " + precioCompraSemilla + "€)";
    }

    /**
     * Obtiene el identificador de la semilla.
     *
     * @return El identificador de la semilla.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la semilla.
     *
     * @return El nombre de la semilla.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la lista de estaciones en las que se puede cultivar la semilla.
     *
     * @return La lista de estaciones.
     */
    public List<Estacion> getEstaciones() {
        return estaciones;
    }

    /**
     * Obtiene el número de días necesarios para que la semilla crezca.
     *
     * @return Los días de crecimiento.
     */
    public int getDiasCrecimiento() {
        return diasCrecimiento;
    }

    /**
     * Obtiene el precio de compra de la semilla.
     *
     * @return El precio de compra.
     */
    public int getPrecioCompraSemilla() {
        return precioCompraSemilla;
    }

    /**
     * Obtiene el precio de venta de los frutos de la semilla.
     *
     * @return El precio de venta de los frutos.
     */
    public int getPrecioVentaFruto() {
        return precioVentaFruto;
    }

    /**
     * Obtiene el máximo de frutos que puede producir la semilla.
     *
     * @return El máximo de frutos.
     */
    public int getMaxFrutos() {
        return maxFrutos;
    }
}
