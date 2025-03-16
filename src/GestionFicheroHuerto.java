import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * La clase GestionFicheroHuerto se encarga de gestionar el archivo binario donde se almacena el estado del huerto
 * en Stardam Valley, incluyendo el riego y el crecimiento de las plantas. Esta clase sigue el patrón Singleton para
 * asegurar que solo exista una única instancia.
 * La clase implementa la interfaz Serializable para permitir que su estado se guarde y se restaure
 * mediante serialización.
 */
public class GestionFicheroHuerto implements Serializable {

    private static final String RUTA_FICHERO = "./resources/huerto.dat";
    private static final int TAMANIO_ID_SEMILLA = Integer.BYTES;
    private static final int TAMANIO_REGADA = 1;
    private static final int TAMANIO_NUM_DIAS_CRECIMIENTO = Integer.BYTES;
    private static final int TAMANIO_REGISTRO = TAMANIO_ID_SEMILLA + TAMANIO_REGADA + TAMANIO_NUM_DIAS_CRECIMIENTO;
    private static final int VALOR_DEFECTO_ENTERO = -1;
    private static final Boolean VALOR_DEFECTO_BOOLEAN = false;
    private static GestionFicheroHuerto instancia;
    private GestionFPropiedades conf = GestionFPropiedades.getInstancia();

    /**
     * Constructor privado para evitar la creación de múltiples instancias y así implementar el patrón Singleton.
     */
    private GestionFicheroHuerto() {}

    /**
     * Devuelve la instancia única de GestionFicheroHuerto.
     * @return instancia de GestionFicheroHuerto
     */
    public static GestionFicheroHuerto getInstancia() {
        if (instancia == null) {
            instancia = new GestionFicheroHuerto();
        }
        return instancia;
    }

    /**
     * Inicializa el archivo del huerto con valores por defecto, preparando cada celda para almacenar información de
     * semillas. Cada celda contiene un ID de semilla, estado de riego, y días de crecimiento.
     * @throws RuntimeException si ocurre un error de entrada/salida durante la inicialización
     */
    public void inicializarHuerto() {
        int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        int tamanio_huerto = filasHuerto * columnasHuerto;

        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            raf.setLength(0);
            raf.seek(0);

            for (int i = 0; i < tamanio_huerto; i++) {
                raf.writeInt(VALOR_DEFECTO_ENTERO);
                raf.writeBoolean(VALOR_DEFECTO_BOOLEAN);
                raf.writeInt(VALOR_DEFECTO_ENTERO);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar el huerto con valores por defecto", e);
        }
    }

    /**
     * Gestiona el crecimiento de las plantas en el huerto, incrementando los días de crecimiento de las semillas
     * regadas y cosechando aquellas que cumplen con los días necesarios.
     * @param mapaSemillasPorClave Mapa que asocia el ID de cada semilla con su respectiva instancia de Semilla.
     * @return Mapa con los frutos cosechados, donde la clave es el nombre de la semilla y el valor es la cantidad cosechada.
     * @throws RuntimeException si ocurre un error de entrada/salida durante el proceso
     */
    public Map<String, Integer> cuidarHuerto(Map<Integer, Semilla> mapaSemillasPorClave) {
        int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        Map<String, Integer> almacen = new HashMap<>();
        Random random = new Random();

        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            raf.seek(0);

            for (int columna = 0; columna < columnasHuerto; columna++) {
                for (int fila = 0; fila < filasHuerto; fila++) {
                    long desplazamiento = (fila * columnasHuerto + columna) * TAMANIO_REGISTRO;
                    raf.seek(desplazamiento);
                    int idSemilla = raf.readInt();
                    boolean regado = raf.readBoolean();
                    int diasPlantado = raf.readInt();

                    if (idSemilla != VALOR_DEFECTO_ENTERO && mapaSemillasPorClave.containsKey(idSemilla)) {
                        Semilla semilla = mapaSemillasPorClave.get(idSemilla);

                        if (regado) {
                            diasPlantado++;
                        }

                        if (diasPlantado >= semilla.getDiasCrecimiento()) {
                            int cantidadFrutos = random.nextInt(semilla.getMaxFrutos()) + 1;
                            almacen.put(semilla.getNombre(), almacen.getOrDefault(semilla.getNombre(), 0) + cantidadFrutos);
                            raf.seek(desplazamiento);
                            raf.writeInt(VALOR_DEFECTO_ENTERO);
                            raf.writeBoolean(VALOR_DEFECTO_BOOLEAN);
                            raf.writeInt(VALOR_DEFECTO_ENTERO);
                        } else {
                            raf.seek(desplazamiento + TAMANIO_ID_SEMILLA);
                            raf.writeBoolean(true);
                            raf.writeInt(diasPlantado);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cuidar el huerto", e);
        }

        return almacen;
    }
    /**
     * Planta una semilla en todas las celdas de una columna si esta está vacía.
     * @param semilla Semilla a plantar en la columna
     * @param columna Índice de la columna donde plantar la semilla
     * @throws RuntimeException si ocurre un error de entrada/salida durante el proceso
     */
    public void plantarSemillaColumna(Semilla semilla, int columna) {
        if (isColumnaVacia(columna)) {
            int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
            int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
            try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
                for (int fila = 0; fila < filasHuerto; fila++) {
                    long desplazamiento = (fila * columnasHuerto + columna) * TAMANIO_REGISTRO;
                    raf.seek(desplazamiento);

                    raf.writeInt(semilla.getId());
                    raf.writeBoolean(VALOR_DEFECTO_BOOLEAN);
                    raf.writeInt(0);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al plantar la semilla en la columna", e);
            }
        } else {
            System.out.println("La columna " + columna + " ya tiene semillas plantadas!");
        }
    }

    /**
     * Verifica si una columna especificada está vacía (sin semillas).
     * @param columna Índice de la columna a verificar
     * @return true si la columna está vacía, false en caso contrario
     * @throws RuntimeException si ocurre un error de entrada/salida durante el proceso
     */
    public boolean isColumnaVacia(int columna) {
        int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        try (RandomAccessFile raf = new RandomAccessFile(RUTA_FICHERO, "r")) {
            for (int fila = 0; fila < filasHuerto; fila++) {
                long desplazamiento = (fila * Integer.parseInt(conf.getPropiedad("columnasHuerto")) + columna) * TAMANIO_REGISTRO;
                raf.seek(desplazamiento);

                int idSemilla = raf.readInt();
                if (idSemilla != VALOR_DEFECTO_ENTERO) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error al verificar si la columna está vacía", e);
        }
    }

    /**
     * Actualiza el estado del huerto al comienzo de un nuevo día, reiniciando el estado de riego de todas las plantas.
     * @throws RuntimeException si ocurre un error de entrada/salida durante el proceso
     */
    public void actualizarHuertoNuevoDia() {
        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            raf.seek(0);

            while (raf.getFilePointer() < raf.length()) {
                int idSemilla = raf.readInt();
                boolean regado = raf.readBoolean();
                int diasPlantado = raf.readInt();
                if (idSemilla != VALOR_DEFECTO_ENTERO) {
                    if (regado) {
                        diasPlantado++;
                    }

                    raf.seek(raf.getFilePointer() - TAMANIO_REGADA - TAMANIO_NUM_DIAS_CRECIMIENTO);
                    raf.writeBoolean(false);
                    raf.writeInt(diasPlantado);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar el huerto para el nuevo día.", e);
        }
    }

    /**
     * Muestra el estado actual del huerto en la consola, con información sobre cada celda.
     * @throws RuntimeException si ocurre un error de entrada/salida durante el proceso
     */
    public void mostrarHuerto() {
        try (RandomAccessFile raf = new RandomAccessFile(RUTA_FICHERO, "r")) {
            raf.seek(0);
            int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
            int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));

            for (int i = 0; i < filasHuerto; i++) {
                for (int j = 0; j < columnasHuerto; j++) {
                    int idSemilla = raf.readInt();
                    boolean regado = raf.readBoolean();
                    int diasPlantado = raf.readInt();

                    String estadoRiego;
                    if (regado) {
                        estadoRiego = "T";
                    } else {
                        estadoRiego = "F";
                    }

                    String celda;
                    if (idSemilla == VALOR_DEFECTO_ENTERO) {
                        celda = "[SS]";
                    } else {
                        celda = "[" + idSemilla + "-" + estadoRiego + "-" + diasPlantado + "]";
                    }

                    System.out.print(celda);
                }
                System.out.println();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al mostrar el huerto", e);
        }
    }


    /**
     * Elimina el archivo del huerto si existe
     */
    public void eliminarFicheroHuerto() {
        File fichero = new File(RUTA_FICHERO);
        if (fichero.exists()) {
            fichero.delete();
        }
    }
}
