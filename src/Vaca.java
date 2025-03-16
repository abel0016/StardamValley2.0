/**
 * La clase Vaca representa una vaca en la granja. Hereda de la clase Animal y tiene atributos y comportamientos específicos
 * para la vaca, como el peso y su capacidad para producir leche.
 */
public class Vaca extends Animal {
    private double peso;

    /**
     * Constructor de la clase Vaca.
     *
     * @param id Identificador único de la vaca.
     * @param nombre Nombre de la vaca.
     * @param diaInsercion Día en que la vaca fue insertada en el juego.
     * @param peso Peso de la vaca, utilizado para calcular la cantidad de leche producida.
     * @param idAlimento Identificador del alimento de la vaca.
     * @param idProducto Identificador del producto que la vaca puede generar (en este caso, leche).
     */
    public Vaca(int id, String nombre, int diaInsercion, double peso, int idAlimento, int idProducto) {
        super(id, nombre, diaInsercion, idAlimento, idProducto);
        this.peso = peso;
    }
    /**
     * Metodo para alimentar a la vaca. La cantidad de alimento consumido depende de los días en el juego.
     *
     * @return La cantidad de alimento consumido por la vaca.
     */
    @Override
    public int alimentar() {
        int diasEnJuego = getDiaInsercion();
        int cantidadConsumida;

        if (diasEnJuego < 10) {
            cantidadConsumida = 1;
        } else if (diasEnJuego >= 10 && diasEnJuego < 40) {
            cantidadConsumida = 3;
        } else {
            cantidadConsumida = 2;
        }
        setHaComido(true);
        return cantidadConsumida;
    }
    /**
     * Metodo que permite a la vaca producir leche, basado en su peso y los días de juego.
     * La vaca solo puede producir leche una vez al día y debe haber comido previamente.
     *
     * @param estacionActual La estación actual en el juego.
     * @param diaJuegoActual El día de juego actual.
     * @return La cantidad de leche producida por la vaca.
     */
    @Override
    public int producir(Estacion estacionActual, int diaJuegoActual) {
        if (haProducidoHoy()) {
            System.out.println(getNombre() + " ya ha producido leche hoy");
            return 0;
        }
        if (diaJuegoActual - getDiaInsercion() >= 1) {
            int cantidadLeche = (int) (peso * 0.01);
            System.out.println(getNombre() + " ha producido " + cantidadLeche + " de leche");
            setHaProducidoHoy(true);
            return cantidadLeche;
        }
        System.out.println(getNombre() + " no puede producir leche hoy");
        return 0;
    }
}
