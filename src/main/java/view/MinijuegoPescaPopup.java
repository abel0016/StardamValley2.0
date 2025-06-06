package view;

import app.GameContext;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.function.Consumer;

public class MinijuegoPescaPopup extends StackPane {

    private final Canvas canvas;
    private final GraphicsContext gc;

    private double barraX = 0;
    private double velocidad = 3;
    private boolean derecha = true;

    private final double ancho = 300;
    private final double alto = 100;
    private final double zonaOkInicio = 120;
    private final double zonaOkFin = 180;

    private int posicionXInicial;
    private int posicionYInicial;
    private AnimationTimer watcher; // nuevo
    private Timeline animacion;
    private Consumer<Boolean> resultadoCallback;

    public MinijuegoPescaPopup(Consumer<Boolean> callback) {
        this.resultadoCallback = callback;
        canvas = new Canvas(ancho, alto);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        this.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px;");

        // Guardar posición inicial del jugador
        this.posicionXInicial = GameContext.getGranja().getPlayerX();
        this.posicionYInicial = GameContext.getGranja().getPlayerY();

        iniciarAnimacion();
        iniciarWatcher();

        setFocusTraversable(true);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                comprobarResultado();
            }
        });
    }

    private void iniciarWatcher() {
        watcher = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int x = GameContext.getGranja().getPlayerX();
                int y = GameContext.getGranja().getPlayerY();
                if (x != posicionXInicial || y != posicionYInicial) {
                    cerrarPopup();
                }
            }
        };
        watcher.start();
    }


    private void cerrarPopup() {
        if (watcher != null) watcher.stop();
        if (animacion != null) animacion.stop();
        GameContext.getRootPane().getChildren().remove(this);

        // Devolver foco a la vista principal
        Platform.runLater(() -> {
            GameContext.getRootPane().requestFocus();
        });
    }


    private void iniciarAnimacion() {
        animacion = new Timeline(new KeyFrame(Duration.millis(30), e -> moverBarra()));
        animacion.setCycleCount(Timeline.INDEFINITE);
        animacion.play();
    }

    private void moverBarra() {
        if (derecha) {
            barraX += velocidad;
            if (barraX > ancho - 20) derecha = false;
        } else {
            barraX -= velocidad;
            if (barraX < 0) derecha = true;
        }

        dibujar();
    }

    private void dibujar() {
        gc.clearRect(0, 0, ancho, alto);

        // Zona verde
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(zonaOkInicio, alto / 2 - 10, zonaOkFin - zonaOkInicio, 20);

        // Barra en movimiento
        gc.setFill(Color.DARKBLUE);
        gc.fillRect(barraX, alto / 2 - 10, 20, 20);

        // Texto
        gc.setFill(Color.WHITE);
        gc.fillText("Pulsa ENTER para pescar", 90, 20);
    }

    private void comprobarResultado() {
        animacion.stop();
        watcher.stop();  // importante detener el watcher
        boolean exito = barraX >= zonaOkInicio && barraX <= zonaOkFin;
        resultadoCallback.accept(exito);
        cerrarPopup();
    }
}
