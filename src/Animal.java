import java.io.Serializable;

/**
 * La clase Animal representa a un animal dentro del juego. Es la clase base de la que heredan todos los animales.
 * Tiene los atributos comunes para todos los animales, como su nombre, ID, información sobre su alimentación,
 * y su capacidad de producir recursos.
 * Los animales deben implementar el metodo producir para definir lo que producen dependiendo
 * de su tipo y la estación del año, y el metodo alimentar para especificar lo que necesitan comer.
 */
public abstract class Animal implements Serializable {
    private int id;
    private String nombre;
    private int diaInsercion;
    private int idAlimento;
    private int idProducto;
    private boolean haComido;
    private boolean haProducidoHoy;

    /**
     * Constructor de la clase {@code Animal}.
     * Inicializa un animal con su ID, nombre, día de inserción, ID de alimento y ID de producto.
     * Los valores de haComido y haProducidoHoy se establecen como false por defecto.
     *
     * @param id             El identificador único del animal.
     * @param nombre         El nombre del animal.
     * @param diaInsercion   El día en el que el animal fue insertado en el juego.
     * @param idAlimento     El ID del alimento que necesita el animal.
     * @param idProducto     El ID del producto que el animal puede producir.
     */
    public Animal(int id, String nombre, int diaInsercion, int idAlimento, int idProducto) {
        this.id = id;
        this.nombre = nombre;
        this.diaInsercion = diaInsercion;
        this.idAlimento = idAlimento;
        this.idProducto = idProducto;
        this.haComido = false;
        this.haProducidoHoy = false;
    }

    /**
     * Este metodo abstracto define lo que produce el animal en un día de juego determinado,
     * dependiendo de la estación y el día de juego actual.
     * Cada animal implementa este metodo de forma diferente.
     *
     * @param estacionActual La estación actual del juego (primavera, verano, otoño, invierno).
     * @param diaJuegoActual El día de juego actual, lo necesitamos para treabajar con las subclases.
     * @return La cantidad del producto que el animal ha producido.
     */
    public abstract int producir(Estacion estacionActual, int diaJuegoActual);

    /**
     * Este metodo abstracto define lo que el animal necesita comer, y cuanto.
     * Cada animal implementa este metodo de forma diferente,por eso hemos definido la clase como abstracta.
     *
     * @return La cantidad de alimento que el animal consume.
     */
    public abstract int alimentar();

    /**
     * Obtiene el estado de si el animal ha producido algo hoy.
     * Se utiliza para evitar que un animal produzca más de una vez al día.
     *
     * @return (true) si el animal ha producido algo hoy, (false) si no.
     */
    public boolean haProducidoHoy() {
        return haProducidoHoy;
    }

    /**
     * Establece si el animal ha producido algo hoy o no.
     *
     * @param haProducidoHoy (true) si ha producido, (false) si no.
     */
    public void setHaProducidoHoy(boolean haProducidoHoy) {
        this.haProducidoHoy = haProducidoHoy;
    }

    //Getters y setters para los atributos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDiaInsercion() {
        return diaInsercion;
    }

    public int getIdAlimento() {
        return idAlimento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public boolean isHaComido() {
        return haComido;
    }

    public void setHaComido(boolean haComido) {
        this.haComido = haComido;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", diaInsercion=" + diaInsercion +
                ", idAlimento=" + idAlimento +
                ", idProducto=" + idProducto +
                ", haComido=" + haComido +
                '}';
    }
}
