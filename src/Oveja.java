import java.time.LocalDate;

/**
 * La clase Oveja representa una oveja en la granja que puede producir lana.
 * Hereda de la clase Animal y tiene la lógica específica para la producción de lana.
 */
public class Oveja extends Animal {
    private LocalDate fechaEsquilado;

    /**
     * Constructor de la clase Oveja.
     *
     * @param id Identificador único de la oveja.
     * @param nombre Nombre de la oveja.
     * @param diaInsercion Día en el que se insertó la oveja en la granja.
     * @param idAlimento Identificador del alimento que puede consumir la oveja.
     * @param idProducto Identificador del producto que produce la oveja.
     */
    public Oveja(int id, String nombre, int diaInsercion, int idAlimento, int idProducto) {
        super(id, nombre, diaInsercion, idAlimento, idProducto);
    }

    /**
     * Verifica si la oveja puede producir lana.
     * La oveja puede producir lana si ha comido y han pasado al menos 2 días desde el último esquilado.
     *
     * @return true si puede producir lana, false en caso contrario.
     */
    public boolean puedeProducirLana() {
        // Comprobamos que ha comido si no no puede producir
        if (!isHaComido()) {
            return false;
        }
        // Comprobamos que hayan pasado al menos 2 días desde el último esquilado
        if (fechaEsquilado == null) {
            return true;
        }
        LocalDate hoy = LocalDate.now();
        int diasDiferencia = hoy.getDayOfYear() - fechaEsquilado.getDayOfYear();
        if (diasDiferencia >= 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metodo que alimenta a la oveja. La oveja consume 1 unidad de alimento.
     *
     * @return La cantidad de alimento consumido.
     */
    @Override
    public int alimentar() {
        return 1;
    }

    /**
     * Metodo que produce lana si la oveja ha comido y han pasado al menos 2 días reales desde el último esquilado.
     * Si la oveja puede producir lana, se le asigna la fecha de esquilado y se actualiza su producción.
     *
     * @param estacionActual La estación actual en el juego.
     * @param diaJuegoActual El día de juego actual.
     * @return La cantidad de lana producida (5 unidades) o 0 si no puede producir.
     */
    @Override
    public int producir(Estacion estacionActual, int diaJuegoActual) {
        if (puedeProducirLana()) {
            setFechaEsquilado(LocalDate.now());
            System.out.println(getNombre() + " ha producido lana 5 unidades de lana");
            setHaProducidoHoy(true);
            return 5;
        } else {
            System.out.println(getNombre() + " no puede producir lana hoy");
            return 0;
        }
    }

    /**
     * Obtiene la fecha del último esquilado de la oveja.
     *
     * @return La fecha del último esquilado.
     */
    public LocalDate getFechaEsquilado() {
        return fechaEsquilado;
    }

    /**
     * Establece la fecha del último esquilado de la oveja.
     *
     * @param fechaEsquilado La fecha en que se esquiló a la oveja.
     */
    public void setFechaEsquilado(LocalDate fechaEsquilado) {
        this.fechaEsquilado = fechaEsquilado;
    }
}
