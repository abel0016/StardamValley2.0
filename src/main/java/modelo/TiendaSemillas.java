package modelo;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class TiendaSemillas implements Serializable {

    private static final int NUM_SEMILLAS_TIENDA = 3;
    private HashSet<Semilla> tiendaSemillas = new HashSet<>();
    private Map<Estacion, List<Semilla>> semillasPorEstacion;
    private static TiendaSemillas instancia;

    private TiendaSemillas() {
        this.semillasPorEstacion = new HashMap<>();
        cargarSemillasDesdeXML("semillas.xml");
    }

    public static TiendaSemillas getInstancia() {
        if (instancia == null) {
            instancia = new TiendaSemillas();
        }
        return instancia;
    }

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

    private void cargarSemillasDesdeXML(String nombreArchivo) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("semillas.xml");
            if (is == null) {
                System.err.println("No se encontró el archivo semillas.xml en resources.");
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
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

    private List<Estacion> obtenerEstaciones(String textoEstaciones) {
        List<Estacion> estaciones = new ArrayList<>();
        for (String estacion : textoEstaciones.split("-")) {
            estaciones.add(Estacion.valueOf(estacion.toUpperCase()));
        }
        return estaciones;
    }

    public HashSet<Semilla> getTiendaSemillas() {
        return tiendaSemillas;
    }

    public Map<Estacion, List<Semilla>> obtenerSemillasPorEstacion() {
        return semillasPorEstacion;
    }
}
