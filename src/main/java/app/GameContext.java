package app;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import modelo.Granja;

public class GameContext {
    private static Granja granjaActual;
    private static Pane rootPane; // Nuevo

    public static Granja getGranja() {
        return granjaActual;
    }

    public static void setGranja(Granja granja) {
        granjaActual = granja;
    }

    public static void moverJugadorA(int x, int y) {
        granjaActual.setPlayerX(x);
        granjaActual.setPlayerY(y);
    }

    public static void setRootPane(Pane pane) {
        rootPane = pane;
    }
    public static Pane getRootPane() {
        return rootPane;
    }

    public static void mostrarNotificacion(String mensaje) {
        if (rootPane == null) return;

        Label label = new Label(mensaje);
        label.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-padding: 8px; -fx-font-size: 16px;");
        label.setLayoutX(400);
        label.setLayoutY(850);

        Platform.runLater(() -> {
            rootPane.getChildren().add(label);
            new Thread(() -> {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> rootPane.getChildren().remove(label));
            }).start();
        });
    }
    

}
