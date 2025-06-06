package modelo;

import gestion.GestionEstablo;
import gestion.GestionFPropiedades;
import gestion.GestionFicheroHuerto;

import java.io.Serializable;
import java.util.*;

public class Granja implements Serializable {
    private int dia_juego;
    private int diaPorEstacion;
    private double presupuesto;
    private Estacion estacion;
    private GestionFicheroHuerto huerto;
    private GestionEstablo establo;
    private GestionFPropiedades conf;
    private boolean atendidoHoy;
    private Map<Integer, Semilla> semillasPorClave = new HashMap<>();
    private Almacen almacen = Almacen.getInstancia();
    private Jugador jugador = new Jugador();
    private int playerX = 5;
    private int playerY = 5;
    private Set<String> edificiosConstruidos = new HashSet<>();
    private int contadorAnimales = 1;

    public Granja(Estacion estacionInicial, int presupuesto) {
        this.dia_juego = 1;
        this.diaPorEstacion = 1;
        this.presupuesto = presupuesto;
        this.estacion = estacionInicial;
        this.huerto = GestionFicheroHuerto.getInstancia();
        this.establo = new GestionEstablo(this);
        this.conf = GestionFPropiedades.getInstancia();
        TiendaSemillas.getInstancia().generarNuevaTienda(estacion);
        this.atendidoHoy = false;
    }

    public void iniciar_nuevo_dia() {
        dia_juego++;
        diaPorEstacion++;
        atendidoHoy = false;
        jugador.nuevoDia();

        for (Animal animal : establo.getAnimales()) {
            animal.setHaProducidoHoy(false);
            animal.setHaComido(false);
        }

        double gananciaFrutos = almacen.venderFrutos();
        double gananciaProductos = almacen.venderProductosGanaderos();
        double ganancia = gananciaFrutos + gananciaProductos;

        if (ganancia > 0) {
            this.presupuesto += ganancia;
        }

        int duracionEstacion = Integer.parseInt(conf.getPropiedad("diasPorEstacion"));
        if (diaPorEstacion > duracionEstacion) {
            cambiarEstacion();
            diaPorEstacion = 1;
            huerto.actualizarHuertoNuevoDia();
        } else {
            huerto.actualizarHuertoNuevoDia();
        }

        TiendaSemillas.getInstancia().generarNuevaTienda(estacion);
        jugador.limpiarSemillasConCero();

        if (establo != null) {
            Iterator<Animal> it = establo.getAnimales().iterator();
            while (it.hasNext()) {
                Animal animal = it.next();
                animal.avanzarDia();
                establo.actualizarEdadYComida(animal);
                if (animal.getEdad() == 18) {
                    animal.setEstadoEdad(Animal.EstadoEdad.ANCIANO);
                } else if (animal.getEdad() >= 21) {
                    establo.eliminarAnimal(animal.getNombre());
                    it.remove();
                }

            }
        }
    }

    private void cambiarEstacion() {
        estacion = getSiguienteEstacion(estacion);
        System.out.println("Cambio de estación a: " + estacion);
        huerto.inicializarHuerto();
        diaPorEstacion = 1;
    }

    private Estacion getSiguienteEstacion(Estacion estacionActual) {
        switch (estacionActual) {
            case PRIMAVERA: return Estacion.VERANO;
            case VERANO: return Estacion.OTONIO;
            case OTONIO: return Estacion.INVIERNO;
            case INVIERNO: return Estacion.PRIMAVERA;
            default: throw new IllegalStateException("Estación desconocida: " + estacionActual);
        }
    }

    public void atenderCultivos() {
        if (atendidoHoy) {
            System.out.println("Ya has atendido los cultivos hoy");
            return;
        }

        Map<Semilla, Integer> cosechados = huerto.cuidarHuerto(semillasPorClave);

        for (Map.Entry<Semilla, Integer> entrada : cosechados.entrySet()) {
            Semilla semilla = entrada.getKey();
            int cantidad = entrada.getValue();
            jugador.agregarFruto(semilla, cantidad);
        }

        int total = cosechados.values().stream().mapToInt(Integer::intValue).sum();
        jugador.ganarExpCosecha(total * 10);

        atendidoHoy = true;
    }

    public void agregarAnimal(Animal animal) {
        establo.insertarAnimal(animal);
    }

    public void restarDinero(double cantidad) {
        this.presupuesto -= cantidad;
    }

    public boolean edificioDisponible(String tipoAnimal) {
        switch (tipoAnimal.toLowerCase()) {
            case "gallina":
                return edificiosConstruidos.contains("Gallinero");
            case "vaca":
                return edificiosConstruidos.contains("Vacas");
            case "cerdo":
                return edificiosConstruidos.contains("Cerdos");
            case "oveja":
                return edificiosConstruidos.contains("Ovejas");
            default:
                return false;
        }
    }

    public int generarIdAnimal() {
        return contadorAnimales++;
    }

    public void agregarAlimento(int idAlimento, int cantidad) {
        jugador.agregarAlimento(idAlimento, cantidad);
    }

    public boolean consumirAlimento(int idAlimento, int cantidad) {
        return jugador.consumirAlimento(idAlimento, cantidad);
    }

    // Getters y setters
    public Estacion getEstacion() {
        return estacion;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public GestionEstablo getEstablo() {
        if (establo == null) {
            establo = new GestionEstablo(this);
        }
        return establo;
    }

    public void setEstablo(GestionEstablo establo) {
        this.establo = establo;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public double getPresupuesto() {
        return this.presupuesto;
    }

    public int getDia_juego() {
        return dia_juego;
    }

    public int getDiaPorEstacion() {
        return diaPorEstacion;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Map<Integer, Semilla> getSemillasPorClave() {
        return semillasPorClave;
    }

    public GestionFicheroHuerto getHuerto() {
        return huerto;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    public Set<String> getEdificiosConstruidos() {
        return edificiosConstruidos;
    }

    public void setEdificiosConstruidos(Set<String> edificiosConstruidos) {
        this.edificiosConstruidos = edificiosConstruidos;
    }

    public void agregarSemilla(Semilla semilla, int cantidad) {
        jugador.agregarSemilla(semilla, cantidad);
        semillasPorClave.putIfAbsent(semilla.getId(), semilla);
    }

    public GestionFPropiedades getConf() {
        return conf;
    }
}