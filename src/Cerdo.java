import java.time.LocalDate;

/**
 * La clase Cerdo representa a un cerdo en el juego. Este animal tiene la capacidad de producir trufas
 * dependiendo de la estación del año y si ha sido alimentado. Como todos los animales, el cerdo tiene un ciclo de
 * producción y requiere ser alimentado para poder producir trufas.
 */
public class Cerdo extends Animal {

    /**
     * Constructor de la clase Cerdo
     * Inicializa un cerdo con su ID, nombre, día de inserción, ID de alimento e ID de producto.
     *
     * @param id             El identificador único del cerdo.
     * @param nombre         El nombre del cerdo.
     * @param diaInsercion   El día en el que el cerdo fue insertado en el juego.
     * @param idAlimento     El ID del alimento que necesita el cerdo.
     * @param idProducto     El ID del producto que el cerdo puede producir.
     */
    public Cerdo(int id, String nombre, int diaInsercion, int idAlimento, int idProducto) {
        super(id, nombre, diaInsercion, idAlimento, idProducto);
    }

    /**
     * Metodo que simula la alimentación del cerdo.
     * El cerdo consume una unidad de alimento cada vez que se alimenta.
     *
     * @return La cantidad de alimento consumido.
     */
    @Override
    public int alimentar() {
        return 1;
    }

    /**
     * Este metodo permite que el cerdo produzca trufas dependiendo de la estación actual.
     * Solo puede producir trufas si ha sido alimentado y no ha producido trufas ese día.
     * La cantidad de trufas varía según la estación del año.
     * Además, una vez que el cerdo produce trufas, no puede producir más ese día.
     * @param estacionActual La estación actual del juego (primavera, verano, otoño, invierno).
     * @param diaJuegoActual El día de juego actual.
     * @return La cantidad de trufas producidas.
     */
    @Override
    public int producir(Estacion estacionActual, int diaJuegoActual) {
        if (haProducidoHoy()) {
            System.out.println(getNombre() + " ya ha producido trufas hoy");
            return 0;
        }

        if (!isHaComido()) {
            System.out.println(getNombre() + " no ha sido alimentado, por lo que no puede encontrar trufas");
            return 0;
        }

        int cantidadTrufas = 0;

        //Usamos la estación actual para determinar la cantidad de trufas
        switch (estacionActual) {
            case PRIMAVERA:
                cantidadTrufas = (int) (Math.random() * 2) + 2;
                break;
            case VERANO:
                //En primavera y verano produce entre 2 y 3 trufas
                cantidadTrufas = (int) (Math.random() * 2) + 2;
                break;
            case OTONIO:
                //En otoño produce entre 0 y 1 trufa
                cantidadTrufas = (int) (Math.random() * 2);
                break;
            case INVIERNO:
                //En invierno no produce trufas
                System.out.println(getNombre() + " no puede encontrar trufas porque es invierno");
                return 0;
        }

        //Marcar que el cerdo ha producido trufas hoy
        setHaProducidoHoy(true);
        System.out.println(getNombre() + " ha encontrado " + cantidadTrufas + " trufas");

        return cantidadTrufas;
    }
}
