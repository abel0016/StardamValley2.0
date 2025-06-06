package view;

import app.GameContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.Granja;
import modelo.Semilla;
import modelo.TiendaSemillas;

public class MenuTiendaSemillas extends VBox {

    public MenuTiendaSemillas(Runnable onClose) {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #fdf5e6; -fx-border-color: #8b5a2b; -fx-border-width: 2px;");
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(500);

        Label titulo = new Label("Mercado de Semillas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        getChildren().add(titulo);

        Button btnInfo = new Button("1. Información de todas las semillas");
        btnInfo.setOnAction(e -> mostrarInformacion());

        Button btnComprar = new Button("2. Comprar semillas");
        btnComprar.setOnAction(e -> mostrarCompra());

        Button btnCerrar = new Button("3. Cerrar");
        btnCerrar.setOnAction(e -> onClose.run());

        getChildren().addAll(btnInfo, btnComprar, btnCerrar);
    }

    private void mostrarInformacion() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Semillas disponibles por estación");
        info.setHeaderText("Catálogo de semillas");

        StringBuilder contenido = new StringBuilder();
        TiendaSemillas tiendaSemillas = TiendaSemillas.getInstancia();

        tiendaSemillas.obtenerSemillasPorEstacion().forEach((estacion, lista) -> {
            contenido.append(estacion).append(":\n");
            for (Semilla s : lista) {
                contenido.append(" - ").append(s.getNombre())
                        .append(" (").append(s.getPrecioCompraSemilla()).append("€)\n");
            }
            contenido.append("\n");
        });

        info.setContentText(contenido.toString());
        info.showAndWait();
    }

    private void mostrarCompra() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER_LEFT);

        TiendaSemillas tiendaSemillas = TiendaSemillas.getInstancia();
        Granja granja = GameContext.getGranja();

        for (Semilla semilla : tiendaSemillas.getTiendaSemillas()) {
            HBox fila = new HBox(10);
            fila.setAlignment(Pos.CENTER_LEFT);

            Label lbl = new Label(semilla.getNombre() + " (" + semilla.getPrecioCompraSemilla() + "€/ud):");
            Spinner<Integer> spinnerCantidad = new Spinner<>(1, 99, 1);
            Button btnComprar = new Button("Comprar");

            btnComprar.setOnAction(e -> {
                int cantidad = spinnerCantidad.getValue();
                int total = cantidad * semilla.getPrecioCompraSemilla();

                if (granja.getPresupuesto() >= total) {
                    granja.setPresupuesto(granja.getPresupuesto() - total);
                    granja.agregarSemilla(semilla, cantidad);

                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setHeaderText("Compra realizada");
                    exito.setContentText("Has comprado " + cantidad + " unidades de " + semilla.getNombre() + " por " + total + "€.");
                    exito.showAndWait();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Fondos insuficientes");
                    error.setContentText("No tienes suficiente dinero para esta compra.");
                    error.showAndWait();
                }
            });

            fila.getChildren().addAll(lbl, spinnerCantidad, btnComprar);
            layout.getChildren().add(fila);
        }

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);

        Alert ventanaCompra = new Alert(Alert.AlertType.NONE);
        ventanaCompra.setTitle("Comprar Semillas");
        ventanaCompra.getDialogPane().setContent(scroll);
        ventanaCompra.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        ventanaCompra.showAndWait();
    }
}
