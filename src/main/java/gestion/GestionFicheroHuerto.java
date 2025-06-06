package gestion;

import modelo.Semilla;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GestionFicheroHuerto implements Serializable {

    private static final String RUTA_FICHERO = System.getProperty("user.home") + File.separator + ".stardamvalley" + File.separator + "huerto.dat";
    private static final int TAMANIO_ID_SEMILLA = Integer.BYTES;
    private static final int TAMANIO_REGADA = 1;
    private static final int TAMANIO_NUM_DIAS_CRECIMIENTO = Integer.BYTES;
    private static final int TAMANIO_REGISTRO = TAMANIO_ID_SEMILLA + TAMANIO_REGADA + TAMANIO_NUM_DIAS_CRECIMIENTO;
    private static final int TAMANIO_ID = Integer.BYTES;
    private static final int VALOR_DEFECTO_ENTERO = -1;
    private static final Boolean VALOR_DEFECTO_BOOLEAN = false;
    private static GestionFicheroHuerto instancia;
    private GestionFPropiedades conf = GestionFPropiedades.getInstancia();

    private GestionFicheroHuerto() {}

    public static GestionFicheroHuerto getInstancia() {
        if (instancia == null) {
            instancia = new GestionFicheroHuerto();
        }
        return instancia;
    }

    public void inicializarHuerto() {
        int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        int tamanio_huerto = filasHuerto * columnasHuerto;

        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            raf.setLength(0);
            for (int i = 0; i < tamanio_huerto; i++) {
                raf.writeInt(VALOR_DEFECTO_ENTERO);
                raf.writeBoolean(VALOR_DEFECTO_BOOLEAN);
                raf.writeInt(VALOR_DEFECTO_ENTERO);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar el huerto", e);
        }
    }

    public Map<Semilla, Integer> cuidarHuerto(Map<Integer, Semilla> mapaSemillasPorClave) {
        int filas = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnas = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        Map<Semilla, Integer> almacen = new HashMap<>();
        Random random = new Random();

        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            for (int i = 0; i < filas * columnas; i++) {
                long pos = i * TAMANIO_REGISTRO;
                raf.seek(pos);
                int id = raf.readInt();
                boolean regado = raf.readBoolean();
                int dias = raf.readInt();

                if (id != VALOR_DEFECTO_ENTERO && mapaSemillasPorClave.containsKey(id)) {
                    Semilla semilla = mapaSemillasPorClave.get(id);
                    if (regado) dias++;
                    if (dias >= semilla.getDiasCrecimiento()) {
                        int frutos = random.nextInt(semilla.getMaxFrutos()) + 1;
                        almacen.put(semilla, almacen.getOrDefault(semilla, 0) + frutos);
                        raf.seek(pos);
                        raf.writeInt(VALOR_DEFECTO_ENTERO);
                        raf.writeBoolean(VALOR_DEFECTO_BOOLEAN);
                        raf.writeInt(VALOR_DEFECTO_ENTERO);
                    } else {
                        raf.seek(pos + TAMANIO_ID_SEMILLA);
                        raf.writeBoolean(true);
                        raf.writeInt(dias);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cuidar huerto", e);
        }
        return almacen;
    }

    public void plantarSemillaColumna(Semilla semilla, int columna) {
        if (isColumnaVacia(columna)) {
            int filas = Integer.parseInt(conf.getPropiedad("filasHuerto"));
            int columnas = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
            try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
                for (int fila = 0; fila < filas; fila++) {
                    long pos = (fila * columnas + columna) * TAMANIO_REGISTRO;
                    raf.seek(pos);
                    raf.writeInt(semilla.getId());
                    raf.writeBoolean(false);
                    raf.writeInt(0);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al plantar columna", e);
            }
        }
    }

    public boolean isColumnaVacia(int columna) {
        int filas = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnas = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        try (RandomAccessFile raf = new RandomAccessFile(RUTA_FICHERO, "r")) {
            for (int fila = 0; fila < filas; fila++) {
                long pos = (fila * columnas + columna) * TAMANIO_REGISTRO;
                raf.seek(pos);
                if (raf.readInt() != VALOR_DEFECTO_ENTERO) return false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al verificar columna", e);
        }
        return true;
    }

    public void actualizarHuertoNuevoDia() {
        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                int id = raf.readInt();
                boolean regado = raf.readBoolean();
                int dias = raf.readInt();
                if (id != VALOR_DEFECTO_ENTERO) {
                    long posicionDias = raf.getFilePointer() - TAMANIO_REGADA;
                    if (regado) {
                        dias++;
                    }
                    raf.seek(raf.getFilePointer() - TAMANIO_REGADA - TAMANIO_NUM_DIAS_CRECIMIENTO);
                    raf.writeBoolean(false);  // Se desriega para el nuevo día
                    raf.writeInt(dias);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar huerto", e);
        }
    }

    public void plantarDesdeInventario(Map<Semilla, Integer> semillas, java.util.function.BiFunction<Semilla, Integer, Boolean> consumir) {
        int filas = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnas = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        try (RandomAccessFile raf = new RandomAccessFile(RUTA_FICHERO, "rw")) {
            for (int fila = 0; fila < filas; fila++) {
                for (int col = 0; col < columnas; col++) {
                    long pos = (fila * columnas + col) * TAMANIO_REGISTRO;
                    raf.seek(pos);
                    int id = raf.readInt();
                    if (id == VALOR_DEFECTO_ENTERO) {
                        for (Semilla s : semillas.keySet()) {
                            int cantidad = semillas.get(s);
                            if (cantidad > 0 && consumir.apply(s, 1)) {
                                raf.seek(pos);
                                raf.writeInt(s.getId());
                                raf.writeBoolean(false);
                                raf.writeInt(0);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al plantar desde inventario", e);
        }
    }

    public CeldaHuerto[][] obtenerEstadoHuerto() {
        int filas = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnas = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        CeldaHuerto[][] estado = new CeldaHuerto[filas][columnas];
        try (RandomAccessFile raf = new RandomAccessFile(RUTA_FICHERO, "r")) {
            for (int fila = 0; fila < filas; fila++) {
                for (int col = 0; col < columnas; col++) {
                    int id = raf.readInt();
                    boolean regado = raf.readBoolean();
                    int dias = raf.readInt();
                    estado[fila][col] = new CeldaHuerto(id, regado, dias);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer estado del huerto", e);
        }
        return estado;
    }

    public void eliminarFicheroHuerto() {
        File fichero = new File(RUTA_FICHERO);
        if (fichero.exists()) fichero.delete();
    }

    public static class CeldaHuerto {
        public final int idSemilla;
        public final boolean regado;
        public final int diasPlantado;

        public CeldaHuerto(int idSemilla, boolean regado, int diasPlantado) {
            this.idSemilla = idSemilla;
            this.regado = regado;
            this.diasPlantado = diasPlantado;
        }
    }
    public boolean plantarEnCelda(Semilla semilla, int fila, int columna) {
        int filasHuerto = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnasHuerto = Integer.parseInt(conf.getPropiedad("columnasHuerto"));

        if (fila < 0 || fila >= filasHuerto || columna < 0 || columna >= columnasHuerto) return false;

        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            long offset = (fila * columnasHuerto + columna) * TAMANIO_REGISTRO;
            raf.seek(offset);
            int id = raf.readInt();
            if (id != VALOR_DEFECTO_ENTERO) return false;

            raf.seek(offset);
            raf.writeInt(semilla.getId());
            raf.writeBoolean(false);
            raf.writeInt(0);
            return true;

        } catch (IOException e) {
            throw new RuntimeException("Error al plantar en celda", e);
        }
    }
    public int regarTodo() {
        int filas = Integer.parseInt(conf.getPropiedad("filasHuerto"));
        int columnas = Integer.parseInt(conf.getPropiedad("columnasHuerto"));
        int cantidadRegadas = 0;

        try (RandomAccessFile raf = new RandomAccessFile(new File(RUTA_FICHERO), "rw")) {
            for (int fila = 0; fila < filas; fila++) {
                for (int col = 0; col < columnas; col++) {
                    long pos = (fila * columnas + col) * TAMANIO_REGISTRO;
                    raf.seek(pos);
                    int id = raf.readInt();

                    if (id != VALOR_DEFECTO_ENTERO) {
                        boolean yaRegado = raf.readBoolean();
                        if (!yaRegado) {
                            raf.seek(pos + TAMANIO_ID);
                            raf.writeBoolean(true);
                            cantidadRegadas++;
                        }
                    } else {
                        raf.skipBytes(TAMANIO_REGADA);
                    }

                    raf.skipBytes(TAMANIO_NUM_DIAS_CRECIMIENTO);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al regar el huerto", e);
        }

        return cantidadRegadas;
    }


}
