package modelo;

import app.GameContext;
import gestion.GestionCentroCivico;
import gestion.GestionPeces;

import java.io.Serializable;
import java.util.*;

public class Jugador implements Serializable {

    private int energiaMaxima = 100;
    private int energiaActual;
    private boolean tieneCañaLarga = false;

    private int nivelRiego;
    private int nivelPesca;
    private int nivelCosecha;
    private int nivelGanaderia;
    private int expRiego = 0;
    private int expPesca = 0;
    private int expCosecha = 0;
    private int expGanaderia = 0;

    private Map<Semilla, Integer> semillasInventario = new HashMap<>();
    private Map<Semilla, Integer> frutosRecolectados = new HashMap<>();
    private Map<String, Integer> productosGanaderos = new HashMap<>();
    private Map<Integer, Integer> alimentos = new HashMap<>();
    private Map<String, Integer> pecesCapturados = new HashMap<>();

    private Random random = new Random();

    public Jugador() {
        this.energiaActual = energiaMaxima;
        this.nivelRiego = 0;
        this.nivelPesca = 0;
        this.nivelCosecha = 0;
        this.nivelGanaderia = 0;
    }

    public void nuevoDia() {
        energiaActual = energiaMaxima;
    }

    public boolean usarEnergia(int cantidad) {
        if (energiaActual >= cantidad) {
            energiaActual -= cantidad;
            return true;
        } else {
            System.out.println("No tienes suficiente energía (" + energiaActual + "/" + energiaMaxima + ")");
            return false;
        }
    }

    // ----------- RIEGO -----------

    public void subirHabilidadRiego() {
        nivelRiego++;
    }

    public boolean intentarRegar(int casillasRegadas) {
        double probabilidad = nivelRiego * 0.05;
        if (random.nextDouble() < probabilidad) {
            System.out.println("¡Has regado sin gastar energía gracias a tu habilidad!");
        } else if (!usarEnergia(5)) {
            return false;
        }
        ganarExpRiego(casillasRegadas * 5);
        return true;
    }

    public void ganarExpRiego(int cantidad) {
        expRiego += cantidad;
        while (expRiego >= 100) {
            expRiego -= 100;
            nivelRiego++;
            System.out.println("¡Has subido al nivel " + nivelRiego + " de Riego!");
            GameContext.mostrarNotificacion("Nivel Riego Up");
        }
    }

    // ----------- PESCA -----------

    public void subirHabilidadPesca() {
        nivelPesca++;
    }

    public Pez pescar(Estacion estacionActual, String tipoDeAguas) {
        if (!usarEnergia(10)) return null;

        Pez base = GestionPeces.getInstancia().obtenerPezAleatorioPorTipo(estacionActual, tipoDeAguas);
        if (base == null) {
            System.out.println("No hay peces disponibles esta estación para aguas " + tipoDeAguas);
            return null;
        }

        boolean trofeo = random.nextDouble() < (nivelPesca * 0.05);

        int rango = base.getTamanioMaximo() - base.getTamanioMinimo() + 1;
        int tamañoReal = trofeo
                ? base.getTamanioMaximo()
                : base.getTamanioMinimo() + random.nextInt(Math.max(1, rango));

        int valorReal = (int) (base.getValorBase() * (0.5 + ((double)(tamañoReal - base.getTamanioMinimo()) / rango) * 0.5));

        Pez capturado = new Pez(base.getNombre(), base.getValorBase(), base.getTamanioMinimo(), base.getTamanioMaximo(), base.getTipoDeAguas());
        capturado.setTamanioReal(tamañoReal);

        registrarPez(capturado);
        añadirPezAlInventario(capturado);

        System.out.println("Has pescado un " + capturado.getNombre() + " de " + tamañoReal + " cm (valor: " + valorReal + ")");
        return capturado;
    }

    private Map<String, Integer> pecesPescados = new HashMap<>();

    public void registrarPez(Pez pez) {
        pecesPescados.merge(pez.getNombre(), 1, Integer::sum);
    }

    public void añadirPezAlInventario(Pez pez) {
        String clave = pez.getNombre() + " (" + pez.getTamanioReal() + " cm)";
        pecesCapturados.put(clave, pecesCapturados.getOrDefault(clave, 0) + 1);
    }

    public Map<String, Integer> getPecesCapturados() {
        return pecesCapturados;
    }

    public void retirarPez(String clave, int cantidad) {
        int actual = pecesCapturados.getOrDefault(clave, 0);
        int nuevo = Math.max(0, actual - cantidad);
        if (nuevo == 0) {
            pecesCapturados.remove(clave);
        } else {
            pecesCapturados.put(clave, nuevo);
        }
    }
    public int venderPeces() {
        int total = 0;
        Map<String, Integer> pecesVendidos = new HashMap<>();

        for (Map.Entry<String, Integer> entry : pecesCapturados.entrySet()) {
            String clave = entry.getKey(); // Ej: "Trucha (48 cm)"
            int cantidad = entry.getValue();

            // Extraer nombre y tamaño del pez
            try {
                String[] partes = clave.split(" \\(");
                String nombre = partes[0];
                String tamanioStr = partes[1].replace(" cm)", "").trim();
                int tamanio = Integer.parseInt(tamanioStr);

                // Obtener pez base
                Pez base = GestionPeces.getInstancia()
                        .obtenerPecesPorEstacion(GameContext.getGranja().getEstacion()) // Para buscar en la estación actual
                        .stream().filter(p -> p.getNombre().equals(nombre)).findFirst().orElse(null);

                if (base != null) {
                    int rango = base.getTamanioMaximo() - base.getTamanioMinimo() + 1;
                    int valor = (int) (base.getValorBase() * (0.5 + ((double)(tamanio - base.getTamanioMinimo()) / rango) * 0.5));
                    int ganancia = valor * cantidad;
                    total += ganancia;

                    System.out.println("Vendido " + cantidad + "x " + clave + " por " + ganancia + "€");
                    pecesVendidos.put(clave, cantidad);
                }
            } catch (Exception e) {
                System.err.println("Error al vender el pez: " + clave);
            }
        }

        for (String clave : pecesVendidos.keySet()) {
            pecesCapturados.remove(clave);
        }

        return total;
    }


    // ----------- COSECHA -----------

    public void subirHabilidadCosecha() {
        nivelCosecha++;
    }

    public int cosechar(int cantidadBase) {
        if (!usarEnergia(5)) return 0;
        int extra = 0;
        double probabilidad = nivelCosecha * 0.05;
        if (random.nextDouble() < probabilidad) {
            extra = 1;
            System.out.println("¡Gracias a tu habilidad, se ha vuelto a plantar una semilla!");
        }
        ganarExpCosecha(cantidadBase * 10);
        return cantidadBase + extra;
    }

    public void ganarExpCosecha(int cantidad) {
        expCosecha += cantidad;
        while (expCosecha >= 100) {
            expCosecha -= 100;
            nivelCosecha++;
            System.out.println("¡Has subido al nivel " + nivelCosecha + " de Cosecha!");
            GameContext.mostrarNotificacion("Nivel Cosecha Up");
        }
    }

    // ----------- GANADERÍA -----------

    public boolean alimentarAnimalesManual(int cantidadAnimales) {
        int energiaRequerida = cantidadAnimales * 3;

        if (!usarEnergia(energiaRequerida)) {
            return false;
        }

        ganarExpGanaderia(cantidadAnimales * 5); // 5 de experiencia por animal alimentado
        return true;
    }

    private void ganarExpGanaderia(int cantidad) {
        expGanaderia += cantidad;
        while (expGanaderia >= 100) {
            expGanaderia -= 100;
            nivelGanaderia++;
            System.out.println("¡Has subido al nivel " + nivelGanaderia + " de Ganadería!");
            GameContext.mostrarNotificacion("Nivel Ganadería ↑");
        }
    }

    public void subirHabilidadGanaderia() {
        nivelGanaderia++;
    }

    public double getProbabilidadProductoCalidad() {
        return nivelGanaderia * 0.05;
    }

    // ----------- SEMILLAS -----------

    public void añadirSemillas(Semilla semilla, int cantidad) {
        semillasInventario.put(semilla, semillasInventario.getOrDefault(semilla, 0) + cantidad);
    }

    public boolean usarSemilla(Semilla semilla) {
        int cantidad = semillasInventario.getOrDefault(semilla, 0);
        if (cantidad > 0) {
            semillasInventario.put(semilla, cantidad - 1);
            return true;
        }
        return false;
    }

    public void agregarSemilla(Semilla semilla, int cantidad) {
        semillasInventario.put(semilla, semillasInventario.getOrDefault(semilla, 0) + cantidad);
    }

    public void reducirSemillas(Semilla semilla, int cantidad) {
        int actual = semillasInventario.getOrDefault(semilla, 0);
        if (cantidad >= actual) {
            semillasInventario.remove(semilla);
        } else {
            semillasInventario.put(semilla, actual - cantidad);
        }
    }

    public Map<Semilla, Integer> getSemillasInventario() {
        return semillasInventario;
    }

    public void limpiarSemillasConCero() {
        semillasInventario.entrySet().removeIf(entry -> entry.getValue() == 0);
    }

    // ----------- FRUTOS RECOLECTADOS -----------

    public void agregarFruto(Semilla semilla, int cantidad) {
        frutosRecolectados.put(semilla, frutosRecolectados.getOrDefault(semilla, 0) + cantidad);
    }

    public Map<Semilla, Integer> getFrutosRecolectados() {
        return frutosRecolectados;
    }

    public void eliminarFruto(Semilla semilla) {
        frutosRecolectados.remove(semilla);
    }

    public void limpiarFrutos() {
        frutosRecolectados.clear();
    }

    public int venderFrutos() {
        int total = 0;
        for (Map.Entry<Semilla, Integer> entry : frutosRecolectados.entrySet()) {
            Semilla semilla = entry.getKey();
            int cantidad = entry.getValue();
            int ganancia = semilla.getPrecioVentaFruto() * cantidad;
            System.out.println("Vendidos " + cantidad + " frutos de " + semilla.getNombre() + " por " + ganancia + "€");
            total += ganancia;
        }
        frutosRecolectados.clear();
        return total;
    }

    public void retirarFruto(Semilla fruto, int cantidad) {
        if (frutosRecolectados.containsKey(fruto)) {
            int actual = frutosRecolectados.get(fruto);
            int nuevoValor = actual - cantidad;
            if (nuevoValor <= 0) {
                frutosRecolectados.remove(fruto);
            } else {
                frutosRecolectados.put(fruto, nuevoValor);
            }
        }
    }

    // ----------- HABILIDADES -----------

    public Map<String, Integer> getHabilidades() {
        Map<String, Integer> habilidades = new HashMap<>();
        habilidades.put("Regar", nivelRiego);
        habilidades.put("Pesca", nivelPesca);
        habilidades.put("Cosechar", nivelCosecha);
        habilidades.put("Ganadería", nivelGanaderia);
        return habilidades;
    }

    public Map<String, Integer> getExperienciaHabilidades() {
        Map<String, Integer> exp = new HashMap<>();
        exp.put("Regar", expRiego);
        exp.put("Pesca", expPesca);
        exp.put("Cosechar", expCosecha);
        exp.put("Ganadería", expGanaderia);
        return exp;
    }

    // ----------- PRODUCTOS GANADEROS -----------

    public void agregarProductoGanadero(String nombre, int cantidad) {
        productosGanaderos.put(nombre, productosGanaderos.getOrDefault(nombre, 0) + cantidad);
    }

    public Map<String, Integer> getProductosGanaderos() {
        return productosGanaderos;
    }

    public void limpiarProductosGanaderos() {
        productosGanaderos.clear();
    }

    public void retirarProductoGanadero(String nombre, int cantidad) {
        int actual = productosGanaderos.getOrDefault(nombre, 0);
        int nuevoValor = actual - cantidad;
        if (nuevoValor <= 0) {
            productosGanaderos.remove(nombre);
        } else {
            productosGanaderos.put(nombre, nuevoValor);
        }
    }


    private int obtenerPrecioProducto(String nombre) {
        return switch (nombre.toLowerCase()) {
            case "huevo" -> 5;
            case "leche" -> 10;
            case "lana" -> 15;
            case "trufa" -> 20;
            default -> 5;
        };
    }

    // ----------- ALIMENTOS (USANDO ID) -----------

    public void agregarAlimento(int idAlimento, int cantidad) {
        alimentos.put(idAlimento, alimentos.getOrDefault(idAlimento, 0) + cantidad);
    }

    public boolean consumirAlimento(int idAlimento, int cantidad) {
        int disponible = alimentos.getOrDefault(idAlimento, 0);
        if (disponible >= cantidad) {
            alimentos.put(idAlimento, disponible - cantidad);
            if (alimentos.get(idAlimento) <= 0) {
                alimentos.remove(idAlimento);
            }
            return true;
        }
        return false;
    }

    public Map<Integer, Integer> getAlimentos() {
        return alimentos;
    }

    // ----------- ENERGÍA -----------

    public int getEnergiaActual() {
        return energiaActual;
    }

    public int getEnergiaMaxima() {
        return energiaMaxima;
    }

    public int getNivelRiego() {
        return nivelRiego;
    }

    public int getNivelPesca() {
        return nivelPesca;
    }

    public int getNivelCosecha() {
        return nivelCosecha;
    }

    public int getNivelGanaderia() {
        return nivelGanaderia;
    }
    public boolean tieneCañaLarga() {
        return tieneCañaLarga;
    }

    public void setTieneCañaLarga(boolean tieneCañaLarga) {
        this.tieneCañaLarga = tieneCañaLarga;
    }
}
