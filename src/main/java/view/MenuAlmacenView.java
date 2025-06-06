package view;

import app.GameContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.Almacen;
import modelo.Jugador;
import modelo.Semilla;

import java.util.Map;

public class MenuAlmacenView {

    public static void mostrar(Pane parent) {
        parent.getChildren().removeIf(n -> n instanceof VBox && n.getLayoutX() == 150 && n.getLayoutY() == 100);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 20; -fx-border-color: black;");
        layout.setLayoutX(150);
        layout.setLayoutY(100);

        Label titulo = new Label("Almacén - Depositar productos");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        layout.getChildren().add(titulo);

        var jugador = GameContext.getGranja().getJugador();
        var almacen = GameContext.getGranja().getAlmacen();

        boolean tieneContenido = false;

        // Frutos del huerto
        Map<Semilla, Integer> frutosJugador = jugador.getFrutosRecolectados();
        if (!frutosJugador.isEmpty()) {
            layout.getChildren().add(new Label("Frutos cosechados:"));
            for (Map.Entry<Semilla, Integer> entry : frutosJugador.entrySet()) {
                Semilla fruto = entry.getKey();
                int cantidad = entry.getValue();
                if (cantidad > 0) {
                    tieneContenido = true;
                    layout.getChildren().add(crearFilaFruto(fruto, cantidad, jugador, almacen, parent));
                }
            }
        }

        // Productos ganaderos
        Map<String, Integer> productosGanaderos = jugador.getProductosGanaderos();
        if (!productosGanaderos.isEmpty()) {
            layout.getChildren().add(new Label("Productos ganaderos:"));
            for (Map.Entry<String, Integer> entry : productosGanaderos.entrySet()) {
                String nombre = entry.getKey();
                int cantidad = entry.getValue();
                if (cantidad > 0) {
                    tieneContenido = true;
                    layout.getChildren().add(crearFilaGanadero(nombre, cantidad, jugador, almacen, parent));
                }
            }
        }

        if (!tieneContenido) {
            layout.getChildren().add(new Label("No tienes productos para depositar"));
        }

        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> {
            parent.getChildren().remove(layout);
            parent.requestFocus();
        });

        layout.getChildren().add(cerrar);
        parent.getChildren().add(layout);
    }

    private static HBox crearFilaFruto(Semilla fruto, int cantidad, Jugador jugador, Almacen almacen, Pane parent) {
        HBox fila = new HBox(10);
        Label label = new Label(fruto.getNombre() + " (" + cantidad + ")");
        Spinner<Integer> spinner = new Spinner<>(1, cantidad, 1);
        Button btn = new Button("Depositar");
        btn.setOnAction(e -> {
            int seleccionadas = spinner.getValue();
            jugador.retirarFruto(fruto, seleccionadas);
            almacen.agregarFruto(fruto, seleccionadas);
            mostrar(parent);
        });
        fila.getChildren().addAll(label, spinner, btn);
        return fila;
    }

    private static HBox crearFilaGanadero(String nombre, int cantidad, Jugador jugador, Almacen almacen, Pane parent) {
        HBox fila = new HBox(10);
        Label label = new Label(nombre + " (" + cantidad + ")");
        Spinner<Integer> spinner = new Spinner<>(1, cantidad, 1);
        Button btn = new Button("Depositar");
        btn.setOnAction(e -> {
            int seleccionadas = spinner.getValue();
            jugador.retirarProductoGanadero(nombre, seleccionadas);
            almacen.agregarProductoGanadero(nombre, seleccionadas);
            mostrar(parent);
        });
        fila.getChildren().addAll(label, spinner, btn);
        return fila;
    }
}
