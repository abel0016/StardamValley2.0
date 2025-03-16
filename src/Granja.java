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
    private Map<Integer, Semilla> semillasPorClave;
    private Almacen almacen = Almacen.getInstancia();

    public Granja(Estacion estacionInicial, int presupuesto) {
        this.dia_juego = 1;
        this.diaPorEstacion = 1;
        this.presupuesto = presupuesto;
        this.estacion = estacionInicial;
        this.huerto = GestionFicheroHuerto.getInstancia();
        this.establo = GestionEstablo.getInstance(this);
        this.establo.setGranja(this);
        this.conf = GestionFPropiedades.getInstancia();
        Tienda.getInstancia().generarNuevaTienda(estacion);
        this.atendidoHoy = false;
    }

    public void iniciar_nuevo_dia() {
        dia_juego++;
        diaPorEstacion++;
        atendidoHoy = false;

        //reseteamos el estado de hacomido y ha producido a false de los animales
        for (Animal animal : establo.getAnimales()) {
            animal.setHaProducidoHoy(false);
            animal.setHaComido(false);
        }
        int duracionEstacion = Integer.parseInt(conf.getPropiedad("diasPorEstacion"));
        if (diaPorEstacion > duracionEstacion) {
            cambiarEstacion();
        } else {
            huerto.actualizarHuertoNuevoDia();
        }


        Tienda.getInstancia().generarNuevaTienda(estacion);
    }

    public void mostrarGranjaInfo() {
        System.out.println("INFORMACIÓN DE LA GRANJA:");
        System.out.println("- Día de juego: " + dia_juego);
        System.out.println("- Dinero disponible: " + presupuesto);
        System.out.println("- Estación: " + estacion);

        System.out.print("- Semillas en venta: ");
        boolean haySemillas = false;
        int position = 0;
        for (Semilla semilla : Tienda.getInstancia().getTiendaSemillas()) {
            if (presupuesto >= semilla.getPrecioCompraSemilla()) {
                if (position == 2) {
                    System.out.print(semilla.getNombre() + " ");
                } else {
                    System.out.print(semilla.getNombre() + " - ");
                }
                haySemillas = true;
                position++;
            }
        }
        if (!haySemillas) System.out.print("No hay semillas asequibles hoy.");
        System.out.println();

        System.out.print("- Frutos en almacén: ");
        Almacen.getInstancia().mostrarAlmacen();
        System.out.println();
        System.out.println("- Estado del huerto:");
        huerto.mostrarHuerto();
        System.out.println("- Información del establo:");
        establo.mostrarAnimales();
    }
    public void plantarCultivosPorColumna() {
        Scanner scanner = new Scanner(System.in);
        int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));

        List<Semilla> semillasAsequibles = new ArrayList<>();
        for (Semilla semilla : Tienda.getInstancia().getTiendaSemillas()) {
            if (semilla.getPrecioCompraSemilla() <= presupuesto) {
                semillasAsequibles.add(semilla);
            }
        }

        if (semillasAsequibles.isEmpty()) {
            System.out.println("No tienes suficiente presupuesto para comprar ninguna semilla");
            return;
        }

        System.out.println("Selecciona una semilla para plantar:");
        for (int i = 0; i < semillasAsequibles.size(); i++) {
            System.out.println((i + 1) + ". " + semillasAsequibles.get(i).getNombre() + " - Precio: " + semillasAsequibles.get(i).getPrecioCompraSemilla());
        }

        int seleccionSemilla;
        while (true) {
            try {
                System.out.print("Ingresa el número de la semilla: ");
                seleccionSemilla = scanner.nextInt() - 1;
                if (seleccionSemilla >= 0 && seleccionSemilla < semillasAsequibles.size()) {
                    break;
                } else {
                    System.out.println("Selección inválida, elige un número entre 1 y " + semillasAsequibles.size());
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingresa un número válido");
                scanner.next();
            }
        }

        Semilla semillaSeleccionada = semillasAsequibles.get(seleccionSemilla);

        int columna;
        while (true) {
            try {
                System.out.print("Ingresa el número de la columna (1 a " + columnasHuerto + "): ");
                columna = scanner.nextInt();
                if (columna >= 1 && columna <= columnasHuerto) {
                    columna--;
                    break;
                } else {
                    System.out.println("¡Esa columna no existe! Elige un número entre 1 y " + columnasHuerto);
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingresa un número válido");
                scanner.next();
            }
        }

        if (huerto.isColumnaVacia(columna)) {
            huerto.plantarSemillaColumna(semillaSeleccionada, columna);
            presupuesto -= semillaSeleccionada.getPrecioCompraSemilla();
            System.out.println("Se ha plantado " + semillaSeleccionada.getNombre() + " en la columna " + (columna + 1));
            System.out.println("Presupuesto restante: " + presupuesto + " €");
        } else {
            System.out.println("La columna " + (columna + 1) + " ya tiene cultivos");
        }
    }

    public void venderFrutos() {
        int gananciasTotales = Almacen.getInstancia().venderFrutos();
        presupuesto += gananciasTotales;
        System.out.println("Presupuesto actual: " + presupuesto + " €");
    }

    public void atenderCultivos() {
        if (atendidoHoy) {
            System.out.println("Ya has atendido los cultivos hoy.");
            return;
        }

        semillasPorClave = new HashMap<>();
        for (Semilla semilla : Tienda.getInstancia().obtenerSemillasPorEstacion().get(estacion)) {
            semillasPorClave.put(semilla.getId(), semilla);
        }

        Map<String, Integer> cosechaPorNombre = huerto.cuidarHuerto(semillasPorClave);

        Map<Semilla, Integer> cosechaParaAlmacen = new HashMap<>();
        for (Map.Entry<String, Integer> entrada : cosechaPorNombre.entrySet()) {
            String nombreSemilla = entrada.getKey();
            int cantidad = entrada.getValue();

            Semilla semilla = null;
            for (Semilla s : semillasPorClave.values()) {
                if (s.getNombre().equals(nombreSemilla)) {
                    semilla = s;
                    break;
                }
            }
            if (semilla != null) {
                cosechaParaAlmacen.put(semilla, cantidad);
            }
        }
        Almacen.getInstancia().añadirCosecha(cosechaParaAlmacen);

        atendidoHoy = true;
    }

    public void plantarCultivosEnColumnasVacias() {
        Scanner scanner = new Scanner(System.in);
        int columnasVacias = 0;
        for (int columna = 0; columna < Integer.parseInt(conf.getPropiedad("columnasHuerto")); columna++) {
            if (huerto.isColumnaVacia(columna)) {
                columnasVacias++;
            }
        }
        if (columnasVacias == 0) {
            System.out.println("No hay columnas vacías disponibles para plantar.");
            return;
        }

        List<Semilla> semillasAsequibles = obtenerSemillasAsequibles(columnasVacias);

        if (semillasAsequibles.isEmpty()) {
            System.out.println("No tienes suficiente presupuesto para comprar ninguna semilla.");
            return;
        }
        System.out.println("Selecciona una semilla para plantar en columnas vacías:");
        for (int i = 0; i < semillasAsequibles.size(); i++) {
            Semilla semilla = semillasAsequibles.get(i);
            int costoTotal = semilla.getPrecioCompraSemilla() * columnasVacias;
            System.out.println((i + 1) + ". " + semilla.getNombre() + " - Costo total: " + costoTotal + " €");
        }

        int seleccionSemilla;
        while (true) {
            System.out.print("Ingresa el número de la semilla: ");
            seleccionSemilla = scanner.nextInt() - 1;
            if (seleccionSemilla >= 0 && seleccionSemilla < semillasAsequibles.size()) {
                break;
            } else {
                System.out.println("Elige un número entre 1 y " + semillasAsequibles.size());
            }
        }

        Semilla semillaSeleccionada = semillasAsequibles.get(seleccionSemilla);
        int costoTotal = semillaSeleccionada.getPrecioCompraSemilla() * columnasVacias;
        if (presupuesto < costoTotal) {
            System.out.println("No tienes suficiente dinero para plantar " + semillaSeleccionada.getNombre());
            return;
        }

        for (int columna = 0; columna < Integer.parseInt(conf.getPropiedad("columnasHuerto")); columna++) {
            if (huerto.isColumnaVacia(columna)) {
                huerto.plantarSemillaColumna(semillaSeleccionada, columna);
            }
        }

        presupuesto -= costoTotal;
        System.out.println("Se han plantado " + semillaSeleccionada.getNombre() + " en " + columnasVacias + " columnas vacías.");
        System.out.println("Presupuesto restante: " + presupuesto + " €");
    }

    private void cambiarEstacion() {
        estacion = getSiguienteEstacion(estacion);
        System.out.println("Cambio de estación a: " + estacion);
        huerto.inicializarHuerto();
        diaPorEstacion = 1;
    }

    private Estacion getSiguienteEstacion(Estacion estacionActual) {
        switch (estacionActual) {
            case PRIMAVERA:
                return Estacion.VERANO;
            case VERANO:
                return Estacion.OTONIO;
            case OTONIO:
                return Estacion.INVIERNO;
            case INVIERNO:
                return Estacion.PRIMAVERA;
            default:
                throw new IllegalStateException("Estación desconocida: " + estacionActual);
        }
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public GestionEstablo getEstablo() {
        return establo;
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

    private List<Semilla> obtenerSemillasAsequibles(int columnasVacias) {
        List<Semilla> semillasAsequibles = new ArrayList<>();
        for (Semilla semilla : Tienda.getInstancia().getTiendaSemillas()) {
            int costoTotal = semilla.getPrecioCompraSemilla() * columnasVacias;
            if (costoTotal <= presupuesto) {
                semillasAsequibles.add(semilla);
            }
        }
        return semillasAsequibles;
    }
}
