import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * La clase Tienda representa la tienda del juego Stardam Valley,
 * donde se pueden comprar semillas. Gestiona las semillas disponibles
 * según la estación actual y permite cargar las semillas desde un archivo XML.
 */
public class Tienda implements Serializable {

    private static final int NUM_SEMILLAS_TIENDA = 3;
    private HashSet<Semilla> tiendaSemillas = new HashSet<>();
    private Map<Estacion, List<Semilla>> semillasPorEstacion;
    private static Tienda instancia;

    /**
     * Constructor privado para evitar la creación de múltiples instancias.
     * Carga las semillas desde el archivo XML.
     */
    private Tienda() {
        this.semillasPorEstacion = new HashMap<>();
        cargarSemillasDesdeXML("./resources/semillas.xml");
    }

    /**
     * Devuelve la instancia única de `Tienda`.
     *
     * @return instancia de `Tienda`
     */
    public static Tienda getInstancia() {
        if (instancia == null) {
            instancia = new Tienda();
        }
        return instancia;
    }

    /**
     * Genera una nueva tienda para la estación actual,
     * seleccionando semillas aleatorias disponibles en esa estación.
     *
     * @param estacionActual La estación actual del juego.
     */
    public void generarNuevaTienda(Estacion estacionActual) {
        Random random = new Random();
        tiendaSemillas.clear();
        List<Semilla> semillasDisponibles = semillasPorEstacion.getOrDefault(estacionActual, new ArrayList<>());
        if (semillasDisponibles.isEmpty()) {
            System.out.println("No hay semillas para la estación actual: " + estacionActual);
            return;
        }

        while (tiendaSemillas.size() < NUM_SEMILLAS_TIENDA && !semillasDisponibles.isEmpty()) {
            Semilla semillaAleatoria = semillasDisponibles.get(random.nextInt(semillasDisponibles.size()));
            tiendaSemillas.add(semillaAleatoria);
        }
    }

    /**
     * Carga las semillas desde un archivo XML en la ruta especificada.
     * Las semillas se organizan por las estaciones en las que se pueden cultivar.
     *
     * @param rutaArchivo La ruta del archivo XML que contiene la información de las semillas.
     */
    private void cargarSemillasDesdeXML(String rutaArchivo) {
        try {
            File archivoXML = new File(rutaArchivo);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivoXML);
            doc.getDocumentElement().normalize();

            NodeList listaSemillas = doc.getElementsByTagName("semilla");

            for (int i = 0; i < listaSemillas.getLength(); i++) {
                Node nodoSemilla = listaSemillas.item(i);
                if (nodoSemilla.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoSemilla = (Element) nodoSemilla;

                    int id = Integer.parseInt(elementoSemilla.getAttribute("id"));
                    String nombre = elementoSemilla.getElementsByTagName("nombre").item(0).getTextContent();
                    List<Estacion> estaciones = obtenerEstaciones(elementoSemilla.getElementsByTagName("estacion").item(0).getTextContent());
                    int diasCrecimiento = Integer.parseInt(elementoSemilla.getElementsByTagName("diasCrecimiento").item(0).getTextContent());
                    int precioCompraSemilla = Integer.parseInt(elementoSemilla.getElementsByTagName("precioCompraSemilla").item(0).getTextContent());
                    int precioVentaFruto = Integer.parseInt(elementoSemilla.getElementsByTagName("precioVentaFruto").item(0).getTextContent());
                    int maxFrutos = Integer.parseInt(elementoSemilla.getElementsByTagName("maxFrutos").item(0).getTextContent());

                    Semilla semilla = new Semilla(id, nombre, estaciones, diasCrecimiento, precioCompraSemilla, precioVentaFruto, maxFrutos);
                    for (Estacion estacion : estaciones) {
                        semillasPorEstacion.computeIfAbsent(estacion, k -> new ArrayList<>()).add(semilla);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convierte una cadena de texto en una lista de estaciones.
     *
     * @param textoEstaciones La cadena de texto que contiene las estaciones separadas por guiones.
     * @return Una lista de estaciones.
     */
    private List<Estacion> obtenerEstaciones(String textoEstaciones) {
        List<Estacion> estaciones = new ArrayList<>();
        for (String estacion : textoEstaciones.split("-")) {
            estaciones.add(Estacion.valueOf(estacion.toUpperCase()));
        }
        return estaciones;
    }

    /**
     * Obtiene las semillas que están actualmente en la tienda.
     *
     * @return Un conjunto de semillas disponibles en la tienda.
     */
    public HashSet<Semilla> getTiendaSemillas() {
        return tiendaSemillas;
    }
    /**
     * Obtiene un mapa que relaciona cada estación con su lista de semillas disponibles.
     *
     * @return Un mapa de semillas organizadas por estación.
     */
    public Map<Estacion, List<Semilla>> obtenerSemillasPorEstacion() {
        return semillasPorEstacion;
    }
}
