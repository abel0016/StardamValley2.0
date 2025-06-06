package app;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Granja;
import modelo.Estacion;

public class ConfiguracionPersonalizada {

    public static void mostrar(Stage stageAnterior) {
        Stage ventana = new Stage();
        ventana.setTitle("Configuración Personalizada");

        ComboBox<String> comboEstacion = new ComboBox<>();
        comboEstacion.getItems().addAll("PRIMAVERA", "VERANO", "OTONIO", "INVIERNO");
        comboEstacion.setValue("PRIMAVERA");

        Spinner<Integer> spinnerDias = new Spinner<>(10, 100, 30);
        spinnerDias.setEditable(true);

        Spinner<Integer> spinnerPresupuesto = new Spinner<>(100, 100000, 1000, 100);
        spinnerPresupuesto.setEditable(true);

        Button btnIniciar = new Button("Iniciar Partida");

        btnIniciar.setOnAction(e -> {
            String estacionStr = comboEstacion.getValue();
            int dias = spinnerDias.getValue();
            int presupuesto = spinnerPresupuesto.getValue();

            Estacion estacion = Estacion.valueOf(estacionStr);
            Granja granja = new Granja(estacion, presupuesto);
            granja.getHuerto().inicializarHuerto();
            GameContext.setGranja(granja);

            granja.getConf().establecerPropiedad("diasPorEstacion", String.valueOf(dias));
            granja.getConf().establecerPropiedad("presupuestoInicial", String.valueOf(presupuesto));
            granja.getConf().establecerPropiedad("estacionInicial", estacionStr);
            granja.getConf().guardarConfiguracionPersonalizada(
                    String.valueOf(presupuesto), estacionStr, String.valueOf(dias)
            );

            ventana.close();
            stageAnterior.close();

            // Iniciar nueva ventana con el juego (mapa)
            Stage stageJuego = new Stage();
            SceneManager sceneManager = new SceneManager(stageJuego);
            sceneManager.showGameView();
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FFF5D4;");
        layout.getChildren().addAll(
                new Label("Estación inicial:"), comboEstacion,
                new Label("Días por estación:"), spinnerDias,
                new Label("Presupuesto inicial:"), spinnerPresupuesto,
                btnIniciar
        );

        Scene escena = new Scene(layout, 350, 300);
        ventana.setScene(escena);
        ventana.initOwner(stageAnterior);
        ventana.show();
    }
}

