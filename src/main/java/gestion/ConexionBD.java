package gestion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConexionBD {
    private static ConexionBD instance;
    private Connection connection;


    private ConexionBD() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("configuracionBD.properties")) {
            properties.load(input);
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    public static ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }


    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties properties = new Properties();
                InputStream input = getClass().getClassLoader().getResourceAsStream("configuracionBD.properties");
                if (input == null) {
                    System.err.println("No se encontró el archivo configuracionBD.properties");
                    return null;
                }
                properties.load(input);
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
