package modelo;

import java.io.Serializable;
import java.util.*;

public class TiendaGanadera implements Serializable {

    private static TiendaGanadera instancia;
    private Map<String, Integer> preciosAnimales;
    private Map<Integer, Integer> preciosAlimentos;
    private Map<Integer, String> nombreAlimentos;

    private TiendaGanadera() {
        preciosAnimales = new HashMap<>();
        preciosAlimentos = new HashMap<>();
        nombreAlimentos = new HashMap<>();

        // Precios de animales
        preciosAnimales.put("Gallina", 200);
        preciosAnimales.put("Vaca", 800);
        preciosAnimales.put("Oveja", 600);
        preciosAnimales.put("Cerdo", 1000);

        // Precios de alimentos
        preciosAlimentos.put(1, 20);
        preciosAlimentos.put(2, 30);
        preciosAlimentos.put(3, 40);

        nombreAlimentos.put(1, "Maíz");
        nombreAlimentos.put(2, "Heno");
        nombreAlimentos.put(3, "Bellotas");
    }

    public static TiendaGanadera getInstancia() {
        if (instancia == null) {
            instancia = new TiendaGanadera();
        }
        return instancia;
    }

    public boolean comprarAnimal(String tipo, Granja granja) {
        int precio = preciosAnimales.getOrDefault(tipo, -1);
        if (precio == -1) {
            System.out.println("Animal no válido");
            return false;
        }

        if (granja.getPresupuesto() < precio) {
            System.out.println("No tienes suficiente dinero");
            return false;
        }

        if (!granja.edificioDisponible(tipo)) {
            System.out.println("No tienes construido el edificio para " + tipo);
            return false;
        }

        String nombre = tipo + "#" + UUID.randomUUID().toString().substring(0, 4);
        Animal nuevoAnimal = crearAnimal(tipo, nombre);
        granja.agregarAnimal(nuevoAnimal);
        granja.restarDinero(precio);

        System.out.println("Has comprado una " + tipo + " por " + precio + " monedas");
        return true;
    }
    public boolean comprarAlimento(int idAlimento, int cantidad, Granja granja) {
        int precioUnitario = preciosAlimentos.getOrDefault(idAlimento, -1);
        if (precioUnitario == -1) {
            System.out.println("ID de alimento no válido");
            return false;
        }

        int costeTotal = precioUnitario * cantidad;
        if (granja.getPresupuesto() < costeTotal) {
            System.out.println("No tienes suficiente dinero");
            return false;
        }

        granja.restarDinero(costeTotal);
        granja.agregarAlimento(idAlimento, cantidad);

        String nombre = nombreAlimentos.getOrDefault(idAlimento, "Desconocido");
        System.out.println("Has comprado " + cantidad + " de " + nombre + " por " + costeTotal + " monedas");
        return true;
    }


    private Animal crearAnimal(String tipo, String nombre) {
        switch (tipo) {
            case "Gallina":
                return new Gallina(nombre);
            case "Vaca":
                return new Vaca(nombre);
            case "Oveja":
                return new Oveja(nombre);
            case "Cerdo":
                return new Cerdo(nombre);
            default:
                throw new IllegalArgumentException("Tipo de animal no válido: " + tipo);
        }
    }

    public Map<String, Integer> getPreciosAnimales() {
        return preciosAnimales;
    }

    public Map<Integer, Integer> getPreciosAlimentos() {
        return preciosAlimentos;
    }

    public Map<Integer, String> getNombreAlimentos() {
        return nombreAlimentos;
    }

    public List<String> getAnimalesDisponibles() {
        return new ArrayList<>(preciosAnimales.keySet());
    }

    public List<Integer> getAlimentosDisponibles() {
        return new ArrayList<>(preciosAlimentos.keySet());
    }

    public int getPrecioAnimal(String tipo) {
        return preciosAnimales.getOrDefault(tipo, -1);
    }

    public int getPrecioAlimento(int idAlimento) {
        return preciosAlimentos.getOrDefault(idAlimento, -1);
    }

    public String getNombreAlimento(int idAlimento) {
        return nombreAlimentos.getOrDefault(idAlimento, "Desconocido");
    }
}
