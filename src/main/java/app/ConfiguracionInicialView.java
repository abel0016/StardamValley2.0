package app;

import gestion.GestionFPropiedades;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.Estacion;
import modelo.Granja;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfiguracionInicialView {

    public static void mostrar(Window parent, SceneManager sceneManager) {
        Stage ventana = new Stage();
        ventana.setTitle("Elegir configuración");

        Button btnDefault = new Button("Usar configuración por defecto");
        Button btnPersonalizada = new Button("Configurar manualmente");

        btnDefault.setOnAction(e -> {
            GestionFPropiedades gf = GestionFPropiedades.getInstancia();
            gf.eliminarFicheroPropiedades();
            if (!Files.exists(Paths.get(GestionFPropiedades.RUTA_FICHERO_CONF))) {
                gf.crearFicheroPropiedades();
            }

            gf.cargarDesdeArchivo(GestionFPropiedades.RUTA_FICHERO_CONF);

            int dinero = Integer.parseInt(gf.getPropiedad("presupuestoInicial"));
            Estacion estacion = Estacion.valueOf(gf.getPropiedad("estacionInicial"));
            Granja granja = new Granja(estacion, dinero);
            granja.getHuerto().inicializarHuerto();
            GameContext.setGranja(granja);

            ventana.close();
            sceneManager.showGameView();
        });

        btnPersonalizada.setOnAction(e -> {
            ConfiguracionPersonalizada.mostrar((Stage) parent);
            ventana.close();
        });

        VBox layout = new VBox(15, btnDefault, btnPersonalizada);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(layout, 300, 200);
        ventana.setScene(scene);
        ventana.initOwner(parent);
        ventana.show();
    }
}
