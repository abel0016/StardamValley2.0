import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * La clase ConexionBD se encarga de gestionar la conexión a la base de datos.
 * Utiliza el patrón Singleton para asegurarse de que solo haya una instancia de la conexión en toda la aplicación.
 * También se encarga de cargar las configuraciones de la base de datos desde un archivo de propiedades.
 */
public class ConexionBD {
    private static ConexionBD instance;
    private Connection connection;

    /**
     * Constructor privado para evitar la creación de múltiples instancias.
     * Este metodo carga la configuración de la base de datos desde un archivo de propiedades
     * y establece la conexión a la base de datos.
     */
    private ConexionBD() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("./resources/configuracionBD.properties")) {
            properties.load(input);
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la instancia única de ConexionBD. Si la instancia aun no existe
     * la crea y establece la conexión con la base de datos.
     *
     * @return La instancia de ConexionBD.
     */
    public static ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    /**
     * Obtiene la conexión a la base de datos.
     *
     * @return La conexión a la base de datos.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties properties = new Properties();
                try (FileInputStream input = new FileInputStream("./resources/configuracionBD.properties")) {
                    properties.load(input);
                    String url = properties.getProperty("db.url");
                    String user = properties.getProperty("db.user");
                    String password = properties.getProperty("db.password");
                    connection = DriverManager.getConnection(url, user, password);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
