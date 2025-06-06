package gestion;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.io.Serializable;

public class GestionFPropiedades implements Serializable {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".stardamvalley";
    public static final String RUTA_FICHERO_CONF = CONFIG_DIR + File.separator + "default_config.properties";
    public static final String RUTA_FICHERO_CONF_PERS = CONFIG_DIR + File.separator + "personalized_config.properties";

    private static Properties propiedades = new Properties();
    private static GestionFPropiedades instancia;

    private GestionFPropiedades() {}

    public static GestionFPropiedades getInstancia() {
        if (instancia == null) {
            instancia = new GestionFPropiedades();
        }
        return instancia;
    }

    public void crearFicheroPropiedades() {

        propiedades.setProperty("filasHuerto", "6");
        propiedades.setProperty("columnasHuerto", "6");
        propiedades.setProperty("presupuestoInicial", "1000");
        propiedades.setProperty("estacionInicial", "PRIMAVERA");
        propiedades.setProperty("diasPorEstacion", "30");

        try (FileOutputStream fos = new FileOutputStream(RUTA_FICHERO_CONF)) {
            propiedades.store(fos, "Configuración por defecto");
        } catch (IOException e) {
            System.out.println("No se ha podido guardar la configuración por defecto");
            e.printStackTrace();
        }
    }

    public void guardarConfiguracionPersonalizada(String presupuesto, String estacion, String dias) {

        propiedades.setProperty("filasHuerto","6");
        propiedades.setProperty("columnasHuerto","6");
        propiedades.setProperty("presupuestoInicial", presupuesto);
        propiedades.setProperty("estacionInicial", estacion.toUpperCase());
        propiedades.setProperty("diasPorEstacion", dias);

        try (FileOutputStream fos = new FileOutputStream(RUTA_FICHERO_CONF_PERS)) {
            propiedades.store(fos, "Configuración personalizada");
            System.out.println("Configuración personalizada guardada con éxito");
        } catch (IOException e) {
            System.out.println("No se ha podido guardar la configuración personalizada");
            e.printStackTrace();
        }
    }

    public String getPropiedad(String clave) {
        propiedades.clear();
        Path pathPers = Paths.get(RUTA_FICHERO_CONF_PERS);
        Path pathDef = Paths.get(RUTA_FICHERO_CONF);

        try {
            if (Files.exists(pathPers)) {
                try (FileInputStream fis = new FileInputStream(RUTA_FICHERO_CONF_PERS)) {
                    propiedades.load(fis);
                }
            } else if (Files.exists(pathDef)) {
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

    public void eliminarFicheroPropiedades() {
        try {
            Path pathPers = Paths.get(RUTA_FICHERO_CONF_PERS);
            Path pathDef = Paths.get(RUTA_FICHERO_CONF);

            if (Files.exists(pathPers)) {
                Files.delete(pathPers);
            } else if (Files.exists(pathDef)) {
                Files.delete(pathDef);
            }
        } catch (IOException e) {
            System.out.println("Error en la eliminación de los archivos .properties");
            e.printStackTrace();
        }
    }

    public void establecerPropiedad(String clave, String valor) {
        propiedades.setProperty(clave, valor);
    }

    public Properties cargarDesdeArchivo(String ruta) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(ruta)) {
            props.load(fis);
            propiedades = props;
            return props;
        } catch (IOException e) {
            System.err.println("Error al cargar archivo de configuración: " + ruta);
            e.printStackTrace();
            return null;
        }
    }
}
