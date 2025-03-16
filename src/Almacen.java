import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * La clase Almacen representa un almacén donde se guardan los frutos cosechados
 * de diferentes semillas. Utiliza el patrón Singleton para asegurar que solo exista
 * una única instancia del almacén a lo largo de la aplicación.
 */
public class Almacen implements Serializable {

    private HashMap<Semilla, Integer> almacen = new HashMap<>();
    private static Almacen instancia;

    /**
     * Constructor privado para evitar la creación de múltiples instancias.
     */
    private Almacen() {}

    /**
     * Devuelve la instancia única de Almacen.
     *
     * @return instancia de Almacen
     */
    public static Almacen getInstancia() {
        if (instancia == null) {
            instancia = new Almacen();
        }
        return instancia;
    }

    /**
     * Muestra el contenido actual del almacén. Si el almacén está vacío,
     * se imprime un mensaje correspondiente. De lo contrario, se enumeran
     * los frutos almacenados junto con sus cantidades.
     */
    public void mostrarAlmacen() {
        if (almacen.isEmpty()) {
            System.out.println("El almacén está vacío");
        } else {
            for (Semilla semilla : almacen.keySet()) {
                System.out.print(semilla.getNombre() + ": " + almacen.get(semilla) + " frutos ");
            }
        }
    }

    /**
     * Añade una nueva cosecha al almacén. Si la semilla ya existe en el almacén,
     * se incrementa la cantidad de frutos; de lo contrario, se agrega la semilla
     * con la cantidad cosechada.
     *
     * @param nuevaCosecha , mapa que asocia cada semilla con la cantidad de frutos cosechados.
     */
    public void añadirCosecha(Map<Semilla, Integer> nuevaCosecha) {
        for (Map.Entry<Semilla, Integer> entrada : nuevaCosecha.entrySet()) {
            Semilla semilla = entrada.getKey();
            int cantidad = entrada.getValue();
            almacen.put(semilla, almacen.getOrDefault(semilla, 0) + cantidad);
            System.out.println("Se han añadido " + cantidad + " frutos de " + semilla.getNombre() + " al almacén");
        }
    }
    /**
     * Vende todos los frutos almacenados y calcula las ganancias totales.
     * Se imprime un resumen de las ventas y se vacía el almacén después de la venta.
     *
     * @return Ganancias totales obtenidas por la venta de los frutos.
     */
    public int venderFrutos() {
        int gananciasTotales = 0;
        for (Semilla semilla : almacen.keySet()) {
            int cantidad = almacen.get(semilla);
            int gananciaSemilla = cantidad * semilla.getPrecioVentaFruto();
            System.out.println("Se han vendido " + cantidad + " unidades de " + semilla.getNombre() + " por " + gananciaSemilla + "€");
            gananciasTotales += gananciaSemilla;
        }
        almacen.clear();
        System.out.println("Ganancias totales: " + gananciasTotales + "€");
        return gananciasTotales;
    }
    public static void setInstancia(Almacen almacen) {
        instancia = almacen;
    }
}
