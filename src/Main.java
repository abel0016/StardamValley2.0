import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestionFPropiedades confPropiedades = GestionFPropiedades.getInstancia();
        GestionFBinario confBinario = GestionFBinario.getInstancia();
        GestionFicheroHuerto confHuerto = GestionFicheroHuerto.getInstancia();
        Granja granja;

        System.out.println("1. Cargar Partida Guardada");
        System.out.println("2. Nueva Partida");
        int opcion = 0;
        while (opcion != 1 && opcion != 2) {
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                if (opcion != 1 && opcion != 2) {
                    System.out.println("Opción no válida, por favor ingresa 1 o 2");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida, por favor ingresa un número.");
            }
        }

        if (opcion == 1 && confBinario.existeFicheroBinario()) {
            granja = confBinario.cargarPartidaGuardada();
            if (granja == null) {
                System.out.println("No se pudo cargar la partida guardada. Comenzando nueva partida...");
                granja = crearNuevaPartida(scanner, confBinario, confPropiedades, confHuerto);
            }
        } else {
            granja = crearNuevaPartida(scanner, confBinario, confPropiedades, confHuerto);
        }

        boolean continuarJuego = true;
        while (continuarJuego) {
            System.out.println("        STARDAM VALLEY        ");
            System.out.println("------------------------------");
            System.out.println("1. INICIAR NUEVO DIA");
            System.out.println("2. HUERTO");
            System.out.println("3. ESTABLOS");
            System.out.println("4. SALIR");
            System.out.print("Seleccione una opción: ");
            int opcionJuego = -1;
            while (opcionJuego < 1 || opcionJuego > 4) {
                try {
                    opcionJuego = Integer.parseInt(scanner.nextLine());
                    if (opcionJuego < 1 || opcionJuego > 4) {
                        System.out.println("Opción no válida, por favor ingresa una opción entre 1 y 4");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no válida, por favor ingresa un número");
                }
            }

            switch (opcionJuego) {
                case 1:
                    granja.iniciar_nuevo_dia();
                    break;
                case 2:
                    mostrarMenuHuerto(granja, scanner);
                    break;
                case 3:
                    mostrarMenuEstablos(granja, scanner);
                    break;
                case 4:
                    System.out.println("Guardando partida...");
                    if (confBinario.guardarPartida(granja)) {
                        System.out.println("Partida guardada ¡Hasta la próxima!");
                    } else {
                        System.out.println("Error al guardar la partida");
                    }
                    continuarJuego = false;
                    break;
            }
        }
    }

    private static void mostrarMenuHuerto(Granja granja, Scanner scanner) {
        int opcionHuerto = -1;
        while (opcionHuerto != 6) {
            System.out.println("HUERTO");
            System.out.println("------------------------------");
            System.out.println("1. ATENDER CULTIVOS");
            System.out.println("2. PLANTAR CULTIVOS EN COLUMNA");
            System.out.println("3. PLANTAR CULTIVOS EN COLUMNAS VACÍAS");
            System.out.println("4. VENDER COSECHA");
            System.out.println("5. MOSTRAR INFORMACIÓN DE LA GRANJA");
            System.out.println("6. VOLVER");
            System.out.print("Seleccione una opción: ");
            try {
                opcionHuerto = Integer.parseInt(scanner.nextLine());
                if (opcionHuerto < 1 || opcionHuerto > 6) {
                    System.out.println("Opción no válida, por favor intenta de nuevo");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida, por favor ingresa un número");
            }

            switch (opcionHuerto) {
                case 1:
                    granja.atenderCultivos();
                    break;
                case 2:
                    granja.plantarCultivosPorColumna();
                    break;
                case 3:
                    granja.plantarCultivosEnColumnasVacias();
                    break;
                case 4:
                    granja.venderFrutos();
                    break;
                case 5:
                    granja.mostrarGranjaInfo();
                    break;
                case 6:
                    System.out.println("Volviendo al menú principal...");
                    break;
            }
        }
    }

    private static void mostrarMenuEstablos(Granja granja, Scanner scanner) {
        int opcionEstablos = -1;
        while (opcionEstablos != 6) {
            System.out.println("ESTABLOS");
            System.out.println("------------------------------");
            System.out.println("1. PRODUCIR");
            System.out.println("2. ALIMENTAR");
            System.out.println("3. VENDER PRODUCTOS");
            System.out.println("4. RELLENAR COMEDERO");
            System.out.println("5. MOSTRAR ANIMALES");
            System.out.println("6. VOLVER");
            System.out.print("Seleccione una opción: ");
            try {
                opcionEstablos = Integer.parseInt(scanner.nextLine());
                if (opcionEstablos < 1 || opcionEstablos > 6) {
                    System.out.println("Opción no válida, por favor intenta de nuevo");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida, por favor ingresa un número");
            }

            switch (opcionEstablos) {
                case 1:
                    granja.getEstablo().producir();
                    break;
                case 2:
                    granja.getEstablo().alimentarAnimales();
                    break;
                case 3:
                    granja.getEstablo().venderProductos();
                    break;
                case 4:
                    granja.getEstablo().rellenarComedero();
                    break;
                case 5:
                    granja.getEstablo().mostrarAnimales();
                    break;
                case 6:
                    System.out.println("Volviendo al menú principal...");
                    break;
            }
        }
    }

    private static Granja crearNuevaPartida(Scanner scanner, GestionFBinario confBinario, GestionFPropiedades confPropiedades, GestionFicheroHuerto confHuerto) {
        confBinario.eliminarPartidaGuardada();
        confPropiedades.eliminarFicheroPropiedades();
        confHuerto.eliminarFicheroHuerto();
        System.out.println("        STARDAM VALLEY        ");
        System.out.println("------------------------------");
        System.out.println("1. Valores por Defecto");
        System.out.println("2. Valores Personalizados");

        int opcionConfig;
        while (true) {
            try {
                opcionConfig = Integer.parseInt(scanner.nextLine());
                if (opcionConfig == 1 || opcionConfig == 2) {
                    break;
                } else {
                    System.out.println("Opción no válida, por favor ingresa 1 o 2");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida, por favor ingresa un número");
            }
        }

        if (opcionConfig == 1) {
            confPropiedades.crearFicheroPropiedades();
        } else {
            confPropiedades.crearFicheroPropiedadesPers(scanner);
        }

        confHuerto.inicializarHuerto();
        int presupuestoInicial = Integer.parseInt(confPropiedades.getPropiedad("presupuestoInicial"));
        Estacion estacionInicial = Estacion.valueOf(confPropiedades.getPropiedad("estacionInicial"));
        return new Granja(estacionInicial, presupuestoInicial);
    }

}
