package view;

import app.GameContext;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modelo.Jugador;
import modelo.Semilla;
import modelo.TiendaGanadera;

import java.util.HashMap;
import java.util.Map;

public class MenuInventarioView extends VBox {

    public MenuInventarioView(Runnable onClose) {
        setSpacing(10);
        setStyle("-fx-background-color: #fff; -fx-padding: 20; -fx-border-color: black;");
        setLayoutX(100);
        setLayoutY(100);
        setPrefWidth(400);
        setPrefHeight(400);
        setAlignment(Pos.TOP_LEFT);

        Label titulo = new Label("Inventario del Jugador");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        getChildren().add(titulo);

        Jugador jugador = GameContext.getGranja().getJugador();

        Map<Semilla, Integer> semillas = jugador.getSemillasInventario();
        Map<String, Integer> frutos = new HashMap<>();
        for (Map.Entry<Semilla, Integer> entry : jugador.getFrutosRecolectados().entrySet()) {
            frutos.put(entry.getKey().getNombre(), entry.getValue());
        }

        Map<String, Integer> productosGanaderos = jugador.getProductosGanaderos();

        Map<Integer, Integer> alimentosIds = jugador.getAlimentos();
        Map<Integer, String> nombresAlimentos = TiendaGanadera.getInstancia().getNombreAlimentos();

        Map<String, Integer> alimentos = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : alimentosIds.entrySet()) {
            String nombre = nombresAlimentos.getOrDefault(entry.getKey(), "Desconocido");
            alimentos.put(nombre, entry.getValue());
        }

        if (semillas.isEmpty() && frutos.isEmpty() && productosGanaderos.isEmpty() && alimentos.isEmpty()) {
            getChildren().add(new Label("Inventario vacío"));
        } else {
            if (!semillas.isEmpty()) {
                getChildren().add(new Label("Semillas:"));
                for (Map.Entry<Semilla, Integer> entrada : semillas.entrySet()) {
                    int cantidad = entrada.getValue();
                    if (cantidad > 0) {
                        getChildren().add(new Label("Semilla de " + entrada.getKey().getNombre() + ": " + cantidad));
                    }
                }
            }

            if (!frutos.isEmpty()) {
                getChildren().add(new Label("Frutos cosechados:"));
                for (Map.Entry<String, Integer> entrada : frutos.entrySet()) {
                    int cantidad = entrada.getValue();
                    if (cantidad > 0) {
                        getChildren().add(new Label(entrada.getKey() + ": " + cantidad));
                    }
                }
            }

            if (!productosGanaderos.isEmpty()) {
                getChildren().add(new Label("Productos Ganaderos:"));
                for (Map.Entry<String, Integer> entrada : productosGanaderos.entrySet()) {
                    int cantidad = entrada.getValue();
                    if (cantidad > 0) {
                        getChildren().add(new Label(entrada.getKey() + ": " + cantidad));
                    }
                }
            }
            Map<String, Integer> peces = jugador.getPecesCapturados();
            if (!peces.isEmpty()) {
                getChildren().add(new Label("Peces pescados:"));
                for (Map.Entry<String, Integer> entrada : peces.entrySet()) {
                    int cantidad = entrada.getValue();
                    if (cantidad > 0) {
                        getChildren().add(new Label(entrada.getKey() + " x" + cantidad));
                    }
                }
            }


            if (!alimentos.isEmpty()) {
                getChildren().add(new Label("Alimentos para animales:"));
                for (Map.Entry<String, Integer> entrada : alimentos.entrySet()) {
                    int cantidad = entrada.getValue();
                    if (cantidad > 0) {
                        getChildren().add(new Label("- " + entrada.getKey() + " x" + cantidad));
                    }
                }
            }
        }

        Button cerrar = new Button("Cerrar Inventario");
        cerrar.setOnAction(e -> onClose.run());
        getChildren().add(cerrar);
    }
}