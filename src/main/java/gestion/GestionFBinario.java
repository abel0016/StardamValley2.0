package gestion;

import modelo.Almacen;
import modelo.Granja;
import modelo.TiendaSemillas;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class GestionFBinario implements Serializable {

    private static final String BINARIO_DIR = System.getProperty("user.home") + File.separator + ".stardamvalley";
    public static final String RUTA_FICHERO_BINARIO = BINARIO_DIR + File.separator + "stardam_valley.bin";
    private static GestionFBinario instancia;


    private GestionFBinario() {}

    public static GestionFBinario getInstancia() {
        if (instancia == null) {
            instancia = new GestionFBinario();
        }
        return instancia;
    }

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

    public boolean existeFicheroBinario() {
        return Files.exists(Paths.get(RUTA_FICHERO_BINARIO));
    }

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
