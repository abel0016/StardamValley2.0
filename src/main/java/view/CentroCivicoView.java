package view;

import app.GameContext;
import app.SceneManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import modelo.Estacion;

public class CentroCivicoView extends Pane {

    private static final int TILE_SIZE = 64;
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 15;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private AnimationTimer timer;

    private final Image hierbaPrimavera = new Image(getClass().getResource("/imagenes/suelos/hierba_primavera.png").toExternalForm());
    private final Image hierbaVerano = new Image(getClass().getResource("/imagenes/suelos/hierba_verano.png").toExternalForm());
    private final Image hierbaOtonio = new Image(getClass().getResource("/imagenes/suelos/hierba_otonio.png").toExternalForm());
    private final Image hierbaInvierno = new Image(getClass().getResource("/imagenes/suelos/hierba_invierno.png").toExternalForm());

    private final Image baldosa = new Image(getClass().getResource("/imagenes/suelos/camino_pueblo.png").toExternalForm());
    private final Image edificio = new Image(getClass().getResource("/imagenes/edificios/centro_civico.png").toExternalForm());

    private final JugadorView jugadorView;

    private boolean cambioSolicitado = false;

    public CentroCivicoView(SceneManager sceneManager) {
        this.setPrefSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.canvas = new Canvas(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        this.jugadorView = new JugadorView(
                TILE_SIZE, MAP_WIDTH, MAP_HEIGHT, "centro_civico",
                new java.util.HashMap<>(), new java.util.HashSet<>(),
                (accion) -> {
                    if ("cambiar_granja".equals(accion)) {
                        cambioSolicitado = true;
                    }
                }
        );

        this.setFocusTraversable(true);
        this.requestFocus();
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) this.requestFocus();
        });

        configurarControles();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (cambioSolicitado) {
                    stop();
                    Platform.runLater(() -> {
                        jugadorView.limpiarTeclas();
                        GameContext.moverJugadorA(10, 1);
                        sceneManager.showGameView();
                    });
                    return;
                }

                jugadorView.actualizar(now);
                renderizar();
            }
        };
        timer.start();
    }

    private void configurarControles() {
        this.setOnKeyPressed((KeyEvent e) -> {
            jugadorView.getKeysPressed().add(e.getCode());

            if (e.getCode() == KeyCode.E) {
                int x = GameContext.getGranja().getPlayerX();
                int y = GameContext.getGranja().getPlayerY();

                if (y == 3 && x >= 9 && x <= 12) {
                    boolean yaAbierto = this.getChildren().stream().anyMatch(n -> n instanceof CentroCivicoPopup);
                    if (!yaAbierto) {
                        CentroCivicoPopup popup = new CentroCivicoPopup();
                        popup.setLayoutX(200);
                        popup.setLayoutY(100);
                        this.getChildren().add(popup);
                        popup.requestFocus();
                    }
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                MenuEscUtils.mostrarMenuEsc(this, () -> mostrarInventario());
            }
        });


        this.setOnKeyReleased((KeyEvent e) -> jugadorView.getKeysPressed().remove(e.getCode()));
    }

    private void renderizar() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Fondo por estación
        Estacion estacion = GameContext.getGranja().getEstacion();
        Image fondo;
        switch (estacion) {
            case PRIMAVERA: fondo = hierbaPrimavera; break;
            case VERANO: fondo = hierbaVerano; break;
            case OTONIO: fondo = hierbaOtonio; break;
            case INVIERNO: fondo = hierbaInvierno; break;
            default: fondo = hierbaPrimavera;
        }

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gc.drawImage(fondo, x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        // Camino vertical hacia el edificio
        for (int y = 3; y <= 14; y++) {
            gc.drawImage(baldosa, 9 * TILE_SIZE, y * TILE_SIZE);
            gc.drawImage(baldosa, 10 * TILE_SIZE, y * TILE_SIZE);
            gc.drawImage(baldosa, 11 * TILE_SIZE, y * TILE_SIZE);
        }

        // Baldosas delante del edificio
        for (int x = 8; x <= 12; x++) {
            for (int y = 2; y <= 3; y++) {
                gc.drawImage(baldosa, x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        // Edificio centrado
        gc.drawImage(edificio, 9 * TILE_SIZE, TILE_SIZE);

        // Jugador
        jugadorView.renderizar(gc);
    }
    private void mostrarInventario() {
        final MenuInventarioView[] menuInv = new MenuInventarioView[1];
        menuInv[0] = new MenuInventarioView(() -> {
            this.getChildren().remove(menuInv[0]);
            this.requestFocus();
        });
        this.getChildren().add(menuInv[0]);
    }
}
