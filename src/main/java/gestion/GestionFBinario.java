package gestion;

import modelo.Almacen;
import modelo.Granja;
import modelo.TiendaSemillas;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * La clase gestion.GestionFBinario se encarga de gestionar el archivo binario de almacenamiento para guardar y cargar el estado
 * de la partida en Stardam Valley. Implementa el patrón Singleton para asegurar una única instancia de la clase.
 * La clase implementa la interfaz Serializable para permitir que su estado se guarde y se restaure
 * mediante serialización. Esto es útil para mantener la persistencia del estado del juego.
 */
public class GestionFBinario implements Serializable {

    /**
     * Ruta del archivo binario donde se guarda la partida.
     */
    private static final String BINARIO_DIR = System.getProperty("user.home") + File.separator + ".stardamvalley";
    public static final String RUTA_FICHERO_BINARIO = BINARIO_DIR + File.separator + "stardam_valley.bin";
    private static GestionFBinario instancia;

    /**
     * Constructor privado para evitar la creación de múltiples instancias en el contexto del patrón Singleton.
     */
    private GestionFBinario() {}

    /**
     * Devuelve la instancia única de gestion.GestionFBinario.
     *
     * @return instancia de gestion.GestionFBinario
     */
    public static GestionFBinario getInstancia() {
        if (instancia == null) {
            instancia = new GestionFBinario();
        }
        return instancia;
    }

    /**
     * Elimina el archivo de la partida guardada, si existe en la ruta especificada.
     * Este metodo no genera errores si el archivo no existe.
     *
     * @throws IOException si ocurre un error al intentar eliminar el archivo
     */
    public void eliminarPartidaGuardada() {
        try {
            Path pathBinario = Paths.get(RUTA_FICHERO_BINARIO);
            if (existeFicheroBinario()) {
                Files.delete(pathBinario);
            }
        } catch (IOException e) {
            System.out.println("No se pudo eliminar el archivo de partida guardada");
            e.printStackTrace();
        }
    }

    /**
     * Verifica si existe el archivo de la partida guardada en la ruta especificada.
     *
     * @return true si el archivo existe, false en caso contrario
     */
    public boolean existeFicheroBinario() {
        return Files.exists(Paths.get(RUTA_FICHERO_BINARIO));
    }

    /**
     * Carga la partida guardada desde el archivo binario y devuelve la instancia de la clase modelo.Granja.
     * Si ocurre un error al cargar el archivo, devuelve null.
     *
     * @return la instancia de modelo.Granja cargada desde el archivo, o null si no se pudo cargar.
     * @throws IOException si hay un problema con la entrada/salida al leer el archivo
     * @throws ClassNotFoundException si el archivo no contiene una clase modelo.Granja
     */
    public Granja cargarPartidaGuardada() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_FICHERO_BINARIO))) {
            Granja granja = (Granja) ois.readObject();
            TiendaSemillas.getInstancia().generarNuevaTienda(granja.getEstacion());
            Almacen.setInstancia(granja.getAlmacen());
            return granja;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar la partida");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Guarda el estado actual de la partida en el archivo binario.
     *
     * @param granja instancia de modelo.Granja que representa el estado actual de la partida
     * @return true si la partida se guardó correctamente, false en caso de error
     * @throws IOException si ocurre un error al escribir en el archivo
     */
    public boolean guardarPartida(Granja granja) {
        try (FileOutputStream fos = new FileOutputStream(RUTA_FICHERO_BINARIO);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(granja);
            System.out.println("Partida guardada");
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar la partida");
            e.printStackTrace();
        }
        return false;
    }
}
