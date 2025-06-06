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
import modelo.Jugador;
import modelo.Pez;

public class PlayaView extends Pane {

    private static final int TILE_SIZE = 64;
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 15;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private AnimationTimer timer;

    private final Image arena = new Image(getClass().getResource("/imagenes/suelos/arena.png").toExternalForm());
    private final Image agua = new Image(getClass().getResource("/imagenes/suelos/agua.png").toExternalForm());
    private final Image aguaProfunda = new Image(getClass().getResource("/imagenes/suelos/agua_profunda.png").toExternalForm());
    private final Image madera = new Image(getClass().getResource("/imagenes/suelos/suelo_madera.png").toExternalForm());

    private final JugadorView jugadorView;
    private boolean cambioSolicitado = false;

    public PlayaView(SceneManager sceneManager) {
        this.setPrefSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.canvas = new Canvas(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        GameContext.setRootPane(this);

        this.jugadorView = new JugadorView(
                TILE_SIZE, MAP_WIDTH, MAP_HEIGHT, "playa",
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
                        GameContext.moverJugadorA(10, 13);
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

            // Activar minijuego de pesca si está en la posición correcta y pulsa E
            if (e.getCode() == KeyCode.E) {
                int x = GameContext.getGranja().getPlayerX();
                int y = GameContext.getGranja().getPlayerY();

                // Solo permitir pescar si estás en el puente (x = 10, y entre 7 y 11)
                if (x == 10 && y >= 7 && y <= 11) {
                    iniciarMinijuegoPesca();
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                MenuEscUtils.mostrarMenuEsc(this, () -> mostrarInventario());
            }

        });

        this.setOnKeyReleased((KeyEvent e) -> jugadorView.getKeysPressed().remove(e.getCode()));
    }

    private void iniciarMinijuegoPesca() {
        int x = GameContext.getGranja().getPlayerX();
        int y = GameContext.getGranja().getPlayerY();

        String tipoDeAguas = "superficiales";

        boolean estaFrenteAAguaProfunda =
                (x == 9 && y == 10) || (x == 11 && y == 10) ||
                        (x == 9 && y == 11) || (x == 10 && y == 11) || (x == 11 && y == 11);

        if (estaFrenteAAguaProfunda) {
            if (!GameContext.getGranja().getJugador().tieneCañaLarga()) {
                GameContext.mostrarNotificacion("Necesitas una caña larga para pescar aquí.");
                return;
            }
            tipoDeAguas = "profundas";
        }

        final String tipoFinal = tipoDeAguas;

        MinijuegoPescaPopup popup = new MinijuegoPescaPopup(exito -> {
            if (exito) {
                Jugador jugador = GameContext.getGranja().getJugador();
                Estacion estacionActual = GameContext.getGranja().getEstacion();
                Pez pez = jugador.pescar(estacionActual, tipoFinal);

                if (pez != null) {
                    GameContext.mostrarNotificacion("¡Pescaste un " + pez.getNombre() + " de " + pez.getTamanioReal() + " cm!");
                    jugador.registrarPez(pez);
                } else {
                    GameContext.mostrarNotificacion("No has podido pescar nada.");
                }
            } else {
                GameContext.mostrarNotificacion("¡El pez se escapó!");
            }
        });

        this.getChildren().add(popup);
        popup.requestFocus();
    }

    private void renderizar() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                boolean esAguaProfunda =
                        (x == 9 && y == 10) || (x == 11 && y == 10) ||
                                (x == 9 && y == 11) || (x == 10 && y == 11) || (x == 11 && y == 11);

                if (esAguaProfunda) {
                    gc.drawImage(aguaProfunda, x * TILE_SIZE, y * TILE_SIZE);
                } else if (y < 7) {
                    gc.drawImage(arena, x * TILE_SIZE, y * TILE_SIZE);
                } else {
                    gc.drawImage(agua, x * TILE_SIZE, y * TILE_SIZE);
                }

                if (x == 10 && y >= 7 && y <= 10) {
                    gc.drawImage(madera, x * TILE_SIZE, y * TILE_SIZE);
                }
            }
        }

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
