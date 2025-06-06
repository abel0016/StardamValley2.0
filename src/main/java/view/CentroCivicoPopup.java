package view;

import app.GameContext;
import gestion.GestionCentroCivico;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import modelo.Bundle;
import modelo.Granja;
import modelo.Jugador;
import modelo.Semilla;

import java.util.Map;

public class CentroCivicoPopup extends VBox {

    public CentroCivicoPopup() {
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #333; -fx-padding: 20; -fx-border-color: white;");
        this.setAlignment(Pos.CENTER);

        Label titulo = new Label("Bundles del Centro Cívico");
        titulo.setStyle("-fx-font-size: 18; -fx-text-fill: white;");
        this.getChildren().add(titulo);

        ScrollPane scroll = new ScrollPane();
        VBox contenedor = new VBox(10);
        contenedor.setAlignment(Pos.CENTER);

        for (Bundle bundle : GestionCentroCivico.getInstancia().getBundles()) {
            BorderPane panel = new BorderPane();
            panel.setStyle("-fx-border-color: " + (bundle.estaCompletado() ? "green" : "red") + "; -fx-padding: 10;");

            HBox encabezado = new HBox(10);
            encabezado.setAlignment(Pos.CENTER_LEFT);
            ImageView icono = new ImageView(new Image(getClass().getResource("/imagenes/iconos/" + bundle.getTipo() + ".png").toExternalForm()));
            icono.setFitWidth(32);
            icono.setFitHeight(32);
            Label nombre = new Label(bundle.getNombre());
            nombre.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            encabezado.getChildren().addAll(icono, nombre);
            panel.setTop(encabezado);

            VBox contenido = new VBox(5);
            for (Map.Entry<String, Boolean> entrada : bundle.getElementosRequeridos().entrySet()) {
                Label etiqueta = new Label("• " + entrada.getKey() + (entrada.getValue() ? " ✓" : ""));
                etiqueta.setStyle("-fx-text-fill: " + (entrada.getValue() ? "lightgreen;" : "white;"));
                contenido.getChildren().add(etiqueta);
            }
            panel.setCenter(contenido);

            contenedor.getChildren().add(panel);
        }

        scroll.setContent(contenedor);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        this.getChildren().add(scroll);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);

        Button rellenar = new Button("Rellenar");
        rellenar.setOnAction(e -> {
            rellenarConInventarioJugador();
            Pane parent = (Pane) getParent();
            parent.getChildren().remove(this);
            parent.getChildren().add(new CentroCivicoPopup());
            parent.requestFocus(); // devolver el foco al cerrar
        });

        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> {
            Pane parent = (Pane) getParent();
            parent.getChildren().remove(this);
            parent.requestFocus(); // devolver el foco al cerrar
        });

        botones.getChildren().addAll(rellenar, cerrar);
        this.getChildren().add(botones);
    }

    private void rellenarConInventarioJugador() {
        Jugador jugador = GameContext.getGranja().getJugador();

        for (Bundle bundle : GestionCentroCivico.getInstancia().getBundles()) {
            for (Map.Entry<String, Boolean> entrada : bundle.getElementosRequeridos().entrySet()) {
                String clave = entrada.getKey();
                boolean entregado = entrada.getValue();
                if (entregado) continue;

                switch (bundle.getTipo()) {
                    case "fruto":
                        for (Map.Entry<Semilla, Integer> entry : jugador.getFrutosRecolectados().entrySet()) {
                            if (entry.getKey().getNombre().equalsIgnoreCase(clave) && entry.getValue() > 0) {
                                jugador.retirarFruto(entry.getKey(), 1);
                                bundle.entregar(clave);
                                break;
                            }
                        }
                        break;

                    case "producto":
                        if (jugador.getProductosGanaderos().getOrDefault(clave, 0) > 0) {
                            jugador.retirarProductoGanadero(clave, 1);
                            bundle.entregar(clave);
                        }
                        break;

                    case "pez":
                        for (Map.Entry<String, Integer> entry : jugador.getPecesCapturados().entrySet()) {
                            String claveCompleta = entry.getKey();
                            int cantidad = entry.getValue();

                            if (cantidad <= 0) continue;

                            // Extraer solo el nombre del pez
                            String nombre = claveCompleta.split(" \\(")[0];

                            if (nombre.equalsIgnoreCase(clave)) {
                                jugador.retirarPez(claveCompleta, 1);
                                bundle.entregar(clave);
                                break;
                            }
                        }
                        break;


                }
            }
        }
    }
}
