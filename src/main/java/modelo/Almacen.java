package modelo;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Almacen implements Serializable {

    private HashMap<Semilla, Integer> almacen = new HashMap<>();
    private final Map<Semilla, Integer> frutosAlmacenados = new HashMap<>();
    private Map<String, Integer> productosGanaderos = new HashMap<>();
    private Map<String, Integer> peces = new HashMap<>();
    private static Almacen instancia;


    private Almacen() {}


    public static Almacen getInstancia() {
        if (instancia == null) {
            instancia = new Almacen();
        }
        return instancia;
    }

    public void mostrarAlmacen() {
        if (almacen.isEmpty()) {
            System.out.println("El almacén está vacío");
        } else {
            for (Semilla semilla : almacen.keySet()) {
                System.out.print(semilla.getNombre() + ": " + almacen.get(semilla) + " frutos ");
            }
        }
    }

    public void añadirCosecha(Map<Semilla, Integer> nuevaCosecha) {
        for (Map.Entry<Semilla, Integer> entrada : nuevaCosecha.entrySet()) {
            Semilla semilla = entrada.getKey();
            int cantidad = entrada.getValue();
            almacen.put(semilla, almacen.getOrDefault(semilla, 0) + cantidad);
            System.out.println("Se han añadido " + cantidad + " frutos de " + semilla.getNombre() + " al almacén");
        }
    }

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
    public Map<Semilla, Integer> getContenido() {
        return Collections.unmodifiableMap(almacen);
    }
    public void agregarFruto(Semilla semilla, int cantidad) {
        almacen.put(semilla, almacen.getOrDefault(semilla, 0) + cantidad);
    }
    public void retirarFruto(Semilla fruto, int cantidad) {
        int actual = frutosAlmacenados.getOrDefault(fruto, 0);
        int nuevo = Math.max(0, actual - cantidad);
        if (nuevo == 0) {
            frutosAlmacenados.remove(fruto);
        } else {
            frutosAlmacenados.put(fruto, nuevo);
        }
    }

    public Map<Semilla, Integer> getFrutosAlmacenados() {
        return new HashMap<>(frutosAlmacenados);
    }
    public void agregarProductoGanadero(String nombre, int cantidad) {
        productosGanaderos.put(nombre, productosGanaderos.getOrDefault(nombre, 0) + cantidad);
    }
    public int venderProductosGanaderos() {
        int total = 0;
        for (Map.Entry<String, Integer> entry : productosGanaderos.entrySet()) {
            String nombre = entry.getKey();
            int cantidad = entry.getValue();
            int precio = switch (nombre.toLowerCase()) {
                case "huevo" -> 20;
                case "leche" -> 30;
                case "lana" -> 40;
                case "trufa" -> 60;
                default -> 10;
            };
            int ganancia = cantidad * precio;
            System.out.println("Se han vendido " + cantidad + " de " + nombre + " por " + ganancia + "€");
            total += ganancia;
        }
        productosGanaderos.clear();
        return total;
    }
    public void agregarPez(String clave, int cantidad) {
        peces.put(clave, peces.getOrDefault(clave, 0) + cantidad);
    }

    public int venderPeces() {
        int total = 0;
        for (Map.Entry<String, Integer> entry : peces.entrySet()) {
            String clave = entry.getKey();
            int cantidad = entry.getValue();

            // Extraer el valor base y tamaño
            String nombre = clave.split(" \\(")[0];
            int tamanio = Integer.parseInt(clave.replaceAll(".*\\((\\d+) cm\\).*", "$1"));
            int valor = (int) Math.round(tamanio * 0.75);

            int ganancia = cantidad * valor;
            System.out.println("Se han vendido " + cantidad + " de " + clave + " por " + ganancia + "€");
            total += ganancia;
        }
        peces.clear();
        return total;
    }


}
