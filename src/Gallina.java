/**
 * La clase Gallina representa a una gallina dentro del juego.
 * Hereda de la clase Animal y tiene la capacidad de producir huevos dependiendo de los días de juego y de si ha sido alimentada.
 */
public class Gallina extends Animal {

    /**
     * Constructor de la gallina. Crea una nueva gallina con los parámetros especificados.
     *
     * @param id El identificador único de la gallina.
     * @param nombre El nombre de la gallina.
     * @param diaInsercion El día en el que la gallina fue insertada en el juego.
     * @param idAlimento El identificador del alimento que consume.
     * @param idProducto El identificador del producto que puede producir.
     */
    public Gallina(int id, String nombre, int diaInsercion, int idAlimento, int idProducto) {
        super(id, nombre, diaInsercion, idAlimento, idProducto);
    }

    /**
     * Calcula la cantidad de huevos que la gallina puede producir según el número de días que ha pasado desde su inserción en el juego.
     * Si la gallina tiene menos de 3 días de juego, no produce huevos.
     * Entre 3 y 40 días de juego, produce 2 huevos.
     * Después de 40 días de juego, produce 1 huevo.
     *
     * @param diaJuegoActual El día de juego actual.
     * @return El número de huevos producidos.
     */
    private int calcularHuevosProducidos(int diaJuegoActual) {
        if (diaJuegoActual - getDiaInsercion() < 3) {
            return 0;
        }
        if (diaJuegoActual - getDiaInsercion() <= 40) {
            return 2;
        }
        return 1;
    }

    /**
     * Alimenta a la gallina. Cada vez que se le da de comer, consume 1 unidad de alimento.
     *
     * @return La cantidad de alimento consumido (siempre 1).
     */
    @Override
    public int alimentar() {
        return 1;
    }

    /**
     * Produce huevos si la gallina ha sido alimentada y no ha producido aún el día de juego actual.
     * Verifica si la gallina ha producido huevos ese día y si tiene suficiente tiempo en el juego para producir.
     *
     * @param estacion La estación actual (aunque no se usa directamente en este caso, se pasa por consistencia).
     * @param diaJuegoActual El día de juego actual para calcular la cantidad de huevos.
     * @return El número de huevos producidos en ese día, o 0 si no puede producir.
     */
    @Override
    public int producir(Estacion estacion, int diaJuegoActual) {
        if (!isHaComido()) {
            System.out.println(getNombre() + " no ha sido alimentada, por lo que no puede producir huevos");
            return 0;
        }
        if (haProducidoHoy()) {
            System.out.println(getNombre() + " ya ha producido huevos hoy");
            return 0;
        }
        int huevosProducidos = calcularHuevosProducidos(diaJuegoActual);
        if (huevosProducidos > 0) {
            System.out.println(getNombre() + " ha producido " + huevosProducidos + " huevos");
            setHaProducidoHoy(true);
            return huevosProducidos;
        } else {
            System.out.println(getNombre() + " no produce huevos hoy");
            return 0;
        }
    }
}
