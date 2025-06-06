package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MenuPrincipal extends Application {

    private static final String RUTA_PARTIDA = "resources/partida.dat";
    private static final String RUTA_CONFIG_DEFAULT = "resources/default_config.properties";

    @Override
    public void start(Stage primaryStage) {
        Button btnNueva = new Button("Nueva Partida");
        Button btnCargar = new Button("Cargar Partida");
        Button btnSalir = new Button("Salir");

        btnCargar.setDisable(!new File(RUTA_PARTIDA).exists());

        btnNueva.setOnAction(e -> mostrarSelectorConfiguracion(primaryStage));
        btnCargar.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cargar Partida");
            alert.setHeaderText(null);
            alert.setContentText("Funcionalidad de carga pendiente");
            alert.showAndWait();
        });
        btnSalir.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(15, btnNueva, btnCargar, btnSalir);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-background-color: #DFF0F5;");

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Stardam Valley - Menú Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mostrarSelectorConfiguracion(Stage stageAnterior) {
        Stage ventanaConfig = new Stage();
        ventanaConfig.setTitle("Seleccionar Configuración");

        Button btnDefault = new Button("Usar Configuración por Defecto");
        Button btnPersonalizada = new Button("Configurar Manualmente");

        btnDefault.setOnAction(e -> {
            Properties propiedades = cargarPropiedades(RUTA_CONFIG_DEFAULT);
            if (propiedades != null) {
                iniciarJuegoConPropiedades(stageAnterior, propiedades);
                ventanaConfig.close();
            }
        });

        btnPersonalizada.setOnAction(e -> {
            ventanaConfig.close();
            ConfiguracionPersonalizada.mostrar(stageAnterior);
        });

        VBox layout = new VBox(15, btnDefault, btnPersonalizada);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #E8FFE8;");

        Scene scene = new Scene(layout, 350, 200);
        ventanaConfig.setScene(scene);
        ventanaConfig.initOwner(stageAnterior);
        ventanaConfig.show();
    }

    private Properties cargarPropiedades(String ruta) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(ruta)) {
            props.load(in);
            return props;
        } catch (IOException e) {
            mostrarError("Error al cargar configuración por defecto");
            return null;
        }
    }

    private void iniciarJuegoConPropiedades(Stage stage, Properties props) {
        System.out.println("Iniciando juego con configuración:");
        props.forEach((k, v) -> System.out.println(k + " = " + v));
        // Aquí se instanciaría la clase Granja y se cargaría la siguiente vista
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
