import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

/**
 * La clase GestionFPropiedades se encarga de gestionar los archivos de configuración para el juego Stardam Valley.
 * Permite crear configuraciones predeterminadas y personalizadas, acceder a propiedades, y eliminar archivos de configuración.
 * Esta clase sigue el patrón Singleton para asegurar que solo haya una instancia.
 * La clase implementa la interfaz Serializable para permitir que su estado se guarde y se restaure
 * mediante serialización.
 */
public class GestionFPropiedades implements Serializable {
    public static final String RUTA_FICHERO_CONF = "./resources/default_config.properties";
    public static final String RUTA_FICHERO_CONF_PERS = "./resources/personalized_config.properties";
    private static Properties propiedades = new Properties();
    private static GestionFPropiedades instancia;
    /**
     * Constructor privado para evitar la creación de múltiples instancias (Singleton).
     */
    private GestionFPropiedades() {}

    /**
     * Obtiene la única instancia de GestionFPropiedades.
     * @return instancia de GestionFPropiedades
     */
    public static GestionFPropiedades getInstancia() {
        if (instancia == null) {
            instancia = new GestionFPropiedades();
        }
        return instancia;
    }

    /**
     * Crea el archivo de configuración con valores predeterminados.
     * Guarda los valores predeterminados para el huerto y el juego en el archivo especificado.
     */
    public void crearFicheroPropiedades() {
        propiedades.setProperty("filasHuerto", "4");
        propiedades.setProperty("columnasHuerto", "4");
        propiedades.setProperty("presupuestoInicial", "1000");
        propiedades.setProperty("estacionInicial", "PRIMAVERA");
        propiedades.setProperty("diasPorEstacion", "30");

        try (FileOutputStream fos = new FileOutputStream(RUTA_FICHERO_CONF)) {
            propiedades.store(fos, "Configuracion por defecto");
        } catch (IOException e) {
            System.out.println("No se ha podido guardar la configuración por defecto");
            e.printStackTrace();
        }
    }

    /**
     * Crea el archivo de configuración personalizado basado en la entrada del usuario.
     * Solicita valores específicos para el huerto y el juego a través de un Scanner.
     * @param scanner Scanner para leer la entrada del usuario
     */
    public void crearFicheroPropiedadesPers(Scanner scanner) {
        System.out.print("¿De cuántas filas quieres el huerto? ");
        String filasHuerto = scanner.nextLine();
        propiedades.setProperty("filasHuerto", filasHuerto);

        System.out.print("¿De cuántas columnas tiene el huerto? ");
        String columnasHuerto = scanner.nextLine();
        propiedades.setProperty("columnasHuerto", columnasHuerto);

        System.out.print("¿Cuál es el presupuesto inicial? ");
        String presupuestoInicial = scanner.nextLine();
        propiedades.setProperty("presupuestoInicial", presupuestoInicial);

        System.out.print("¿Cuál es la estación inicial (PRIMAVERA, VERANO, OTONIO, INVIERNO)? ");
        String estacionInicial = scanner.nextLine().toUpperCase();
        propiedades.setProperty("estacionInicial", estacionInicial);

        System.out.print("¿Cuántos días dura cada estación? ");
        String diasPorEstacion = scanner.nextLine();
        propiedades.setProperty("diasPorEstacion", diasPorEstacion);

        try (FileOutputStream fos = new FileOutputStream(RUTA_FICHERO_CONF_PERS)) {
            propiedades.store(fos, "Configuracion personalizada");
            System.out.println("Configuración personalizada guardada con éxito");
        } catch (IOException e) {
            System.out.println("No se ha podido guardar la configuración personalizada");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el valor de una propiedad específica desde el archivo de configuración.
     * Prioriza el archivo personalizado si está presente; de lo contrario, usa el archivo de configuración predeterminado.
     * @param clave La clave de la propiedad que se desea obtener
     * @return El valor de la propiedad o null si no se encuentra la clave
     */
    public String getPropiedad(String clave) {
        propiedades.clear();
        Path pathPersonalizado = Paths.get(RUTA_FICHERO_CONF_PERS);
        Path pathPorDefecto = Paths.get(RUTA_FICHERO_CONF);
        try {
            if (Files.exists(pathPersonalizado)) {
                try (FileInputStream fis = new FileInputStream(RUTA_FICHERO_CONF_PERS)) {
                    propiedades.load(fis);
                }
            } else if (Files.exists(pathPorDefecto)) {
                try (FileInputStream fis = new FileInputStream(RUTA_FICHERO_CONF)) {
                    propiedades.load(fis);
                }
            }
            return propiedades.getProperty(clave);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Elimina el archivo de configuración personalizado si existe; si no, elimina el archivo de configuración predeterminado.
     * @throws IOException si ocurre un error al eliminar el archivo
     */
    public void eliminarFicheroPropiedades() {
        try {
            Path pathPersonalizado = Paths.get(RUTA_FICHERO_CONF_PERS);
            Path pathPorDefecto = Paths.get(RUTA_FICHERO_CONF);
            if (Files.exists(pathPersonalizado)) {
                Files.delete(pathPersonalizado);
            } else if (Files.exists(pathPorDefecto)) {
                Files.delete(pathPorDefecto);
            }
        } catch (IOException e) {
            System.out.println("Error en la eliminación de los archivos .properties");
            e.printStackTrace();
        }
    }
}
