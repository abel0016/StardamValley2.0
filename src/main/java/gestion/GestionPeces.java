package gestion;

import modelo.Pez;
import modelo.Estacion;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class GestionPeces {

    private static GestionPeces instancia;
    private final Map<Estacion, List<Pez>> pecesPorEstacion = new HashMap<>();

    private GestionPeces() {
        cargarPeces();
    }

    public static GestionPeces getInstancia() {
        if (instancia == null) {
            instancia = new GestionPeces();
        }
        return instancia;
    }

    private void cargarPeces() {
        try {
            InputStream input = getClass().getResourceAsStream("/peces.xml");
            if (input == null) {
                throw new FileNotFoundException("No se pudo encontrar peces.xml en el classpath");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(input);
            doc.getDocumentElement().normalize();

            NodeList listaPeces = doc.getElementsByTagName("pez");

            for (int i = 0; i < listaPeces.getLength(); i++) {
                Node nodo = listaPeces.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String estacionStr = getTagContent(elemento, "estacion").toUpperCase();

                    Estacion estacion;
                    try {
                        estacion = Estacion.valueOf(estacionStr);
                    } catch (IllegalArgumentException e) {
                        System.err.println("⚠Estación inválida: '" + estacionStr + "' en pez " +
                                getTagContent(elemento, "nombre"));
                        continue;
                    }

                    try {
                        String nombre = getTagContent(elemento, "nombre");
                        int tamanioMinimo = Integer.parseInt(getTagContent(elemento, "tamanioMinimo"));
                        int tamanioMaximo = Integer.parseInt(getTagContent(elemento, "tamanioMaximo"));
                        String tipoDeAguas = getTagContent(elemento, "tipoDeAguas");
                        int valorBase = 10;
                        int valor = valorBase + tamanioMinimo / 2 + tamanioMaximo / 2;
                        Pez pez = new Pez(nombre, valor, tamanioMinimo, tamanioMaximo, tipoDeAguas);
                        pecesPorEstacion.computeIfAbsent(estacion, k -> new ArrayList<>()).add(pez);
                    } catch (Exception e) {
                        System.err.println("Error al crear pez en estación " + estacionStr + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar peces: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTagContent(Element elemento, String tagName) {
        NodeList list = elemento.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0 && list.item(0) != null) {
            return list.item(0).getTextContent().trim();
        } else {
            throw new IllegalArgumentException("Falta la etiqueta <" + tagName + ">");
        }
    }

    public List<Pez> obtenerPecesPorEstacion(Estacion estacion) {
        return pecesPorEstacion.getOrDefault(estacion, new ArrayList<>());
    }

    public Pez obtenerPezAleatorioPorTipo(Estacion estacion, String tipoDeAguas) {
        List<Pez> lista = obtenerPecesPorEstacion(estacion);
        List<Pez> filtrados = lista.stream()
                .filter(p -> p.getTipoDeAguas().equalsIgnoreCase(tipoDeAguas))
                .toList();

        if (filtrados.isEmpty()) return null;
        return filtrados.get(new Random().nextInt(filtrados.size()));
    }

    public Pez obtenerPezAleatorio(Estacion estacion) {
        List<Pez> lista = obtenerPecesPorEstacion(estacion);
        if (lista.isEmpty()) return null;

        Random random = new Random();
        return lista.get(random.nextInt(lista.size()));
    }
}
