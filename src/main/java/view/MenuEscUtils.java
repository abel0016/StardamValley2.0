package view;

import app.GameContext;
import gestion.GestionFBinario;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modelo.Granja;

public class MenuEscUtils {

    private static VBox menuEsc;
    private static boolean menuEscVisible = false;

    public static void mostrarMenuEsc(Pane parentPane, Runnable mostrarInventario) {
        if (menuEscVisible) return;

        menuEsc = new VBox(10);
        menuEscVisible = true;
        menuEsc.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15; -fx-border-color: black;");
        menuEsc.setLayoutX(parentPane.getWidth() - 230);
        menuEsc.setLayoutY(20);
        menuEsc.setPrefWidth(200);
        menuEsc.setAlignment(Pos.TOP_CENTER);

        Granja granja = GameContext.getGranja();
        String estacion = granja.getEstacion().toString();
        int dia = granja.getDiaPorEstacion();
        double dinero = granja.getPresupuesto();

        Label lblTitulo = new Label(estacion + " - Día " + dia);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblDinero = new Label("Dinero: " + dinero + " €");

        Button btnNuevoDia = new Button("Iniciar nuevo día");
        btnNuevoDia.setOnAction(e -> {
            granja.iniciar_nuevo_dia();
            parentPane.getChildren().remove(menuEsc);
            menuEscVisible = false;

            mostrarMenuEsc(parentPane, mostrarInventario);
        });


        Button btnHabilidades = new Button("Ver Habilidades");
        btnHabilidades.setOnAction(e2 -> MenuHabilidadesView.mostrar(parentPane));

        Button btnGuardar = new Button("Guardar partida");
        btnGuardar.setOnAction(e2 -> {
            boolean exito = GestionFBinario.getInstancia().guardarPartida(granja);
            Alert alert = new Alert(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle("Guardar Partida");
            alert.setHeaderText(null);
            alert.setContentText(exito ? "Partida guardada con éxito" : "Error al guardar la partida");
            alert.show();
        });

        Button btnInventario = new Button("Ver Inventario");
        btnInventario.setOnAction(e3 -> mostrarInventario.run());

        Button btnSalir = new Button("Cerrar menú");
        btnSalir.setOnAction(e4 -> {
            parentPane.getChildren().remove(menuEsc);
            menuEscVisible = false;
            parentPane.requestFocus();
        });

        menuEsc.getChildren().addAll(
                lblTitulo,
                lblDinero,
                btnNuevoDia,
                btnHabilidades,
                btnGuardar,
                btnInventario,
                btnSalir
        );

        parentPane.getChildren().add(menuEsc);
    }
}
