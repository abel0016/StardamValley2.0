package gestion;

import modelo.Bundle;
import modelo.Estacion;

import java.util.*;

public class GestionCentroCivico {

    private static GestionCentroCivico instancia;

    private List<Bundle> bundles = new ArrayList<>();
    private boolean desbloqueadoPescaProfunda = false;

    private final Map<Estacion, String> frutosPorEstacion = Map.of(
            Estacion.PRIMAVERA, "Fresa",
            Estacion.VERANO, "Melón",
            Estacion.OTONIO, "Calabaza",
            Estacion.INVIERNO, "Nabo"
    );

    private final Map<Estacion, String> pecesPorEstacion = Map.of(
            Estacion.PRIMAVERA, "Trucha",
            Estacion.VERANO, "Pez Globo",
            Estacion.OTONIO, "Salmón",
            Estacion.INVIERNO, "Carpa"
    );

    private GestionCentroCivico() {
        inicializarBundles();
    }

    public static GestionCentroCivico getInstancia() {
        if (instancia == null) instancia = new GestionCentroCivico();
        return instancia;
    }

    private void inicializarBundles() {
        // Bundle de frutos
        Bundle frutos = new Bundle("Frutos de Estaciones", "fruto");
        frutosPorEstacion.values().forEach(frutos::agregarElemento);

        // Bundle de peces
        Bundle pesca = new Bundle("Peces de Estaciones", "pez");
        pecesPorEstacion.values().forEach(pesca::agregarElemento);

        // Bundle de productos animales
        Bundle animales = new Bundle("Productos Animales", "producto");
        animales.agregarElemento("Huevo");
        animales.agregarElemento("Leche");
        animales.agregarElemento("Lana");
        animales.agregarElemento("Trufa");

        bundles.add(frutos);
        bundles.add(pesca);
        bundles.add(animales);
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

    public boolean entregarElemento(String tipo, String nombre) {
        for (Bundle b : bundles) {
            if (b.getTipo().equals(tipo)) {
                if (b.entregar(nombre)) {
                    if (b.getTipo().equals("pez") && b.estaCompletado()) {
                        desbloqueadoPescaProfunda = true;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPescaProfundaDesbloqueada() {
        return desbloqueadoPescaProfunda;
    }
}
