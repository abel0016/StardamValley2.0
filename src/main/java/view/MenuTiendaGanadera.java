package view;

import app.GameContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.Granja;
import modelo.TiendaGanadera;

import java.util.Map;

public class MenuTiendaGanadera extends VBox {

    public MenuTiendaGanadera(Runnable onClose) {
        Granja granja = GameContext.getGranja();
        TiendaGanadera tienda = TiendaGanadera.getInstancia();

        setPadding(new Insets(20));
        setStyle("-fx-background-color: #fdf5e6; -fx-border-color: #8b5a2b; -fx-border-width: 2px;");
        setAlignment(Pos.TOP_CENTER);
        setSpacing(10);

        Label tituloAnimales = new Label("Comprar Animales:");
        getChildren().add(tituloAnimales);

        Map<String, Integer> animales = tienda.getPreciosAnimales();
        for (Map.Entry<String, Integer> entrada : animales.entrySet()) {
            String tipo = entrada.getKey();
            int precio = entrada.getValue();
            Button btn = new Button("Comprar " + tipo + " - " + precio + " monedas");
            btn.setOnAction(e -> {
                boolean exito = tienda.comprarAnimal(tipo, granja);
                if (exito) {
                    GameContext.mostrarNotificacion("Has comprado una " + tipo + ".");
                } else {
                    GameContext.mostrarNotificacion("No se pudo comprar " + tipo + ".");
                }
            });
            getChildren().add(btn);
        }

        Label tituloAlimentos = new Label("Comprar Alimentos:");
        getChildren().add(tituloAlimentos);

        Map<Integer, Integer> preciosAlimentos = tienda.getPreciosAlimentos();
        Map<Integer, String> nombresAlimentos = tienda.getNombreAlimentos();

        for (Map.Entry<Integer, Integer> entrada : preciosAlimentos.entrySet()) {
            int id = entrada.getKey();
            int precio = entrada.getValue();
            String nombre = nombresAlimentos.get(id);

            HBox fila = new HBox(10);
            fila.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(nombre + " - " + precio + " monedas c/u");
            TextField cantidadField = new TextField("1");
            Button btnComprar = new Button("Comprar");

            btnComprar.setOnAction(e -> {
                try {
                    int cantidad = Integer.parseInt(cantidadField.getText());
                    if (cantidad <= 0) throw new NumberFormatException();
                    boolean exito = tienda.comprarAlimento(id, cantidad, granja);
                    if (exito) {
                        GameContext.mostrarNotificacion("Compraste " + cantidad + " de " + nombre);
                    } else {
                        GameContext.mostrarNotificacion("No se pudo comprar alimento");
                    }
                } catch (NumberFormatException ex) {
                    GameContext.mostrarNotificacion("Cantidad inválida");
                }
            });

            fila.getChildren().addAll(lbl, cantidadField, btnComprar);
            getChildren().add(fila);
        }

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> onClose.run());
        getChildren().add(btnCerrar);
    }
}
