package view;

import app.GameContext;
import app.SceneManager;
import gestion.GestionFBinario;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modelo.Animal;
import modelo.Granja;
import modelo.Semilla;
import javafx.scene.image.ImageView;
import modelo.TiendaGanadera;

import java.util.*;

public class PuebloView extends Pane {

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
    private final Image camino = new Image(getClass().getResource("/imagenes/suelos/camino_pueblo.png").toExternalForm());
    private final Image piedra = new Image(getClass().getResource("/imagenes/decoracion/rock2.png").toExternalForm());
    private final Image valla = new Image(getClass().getResource("/imagenes/decoracion/valla_horizontal.png").toExternalForm());
    private final Image mercadoSemillas = new Image(getClass().getResource("/imagenes/edificios/market_semillas.png").toExternalForm());
    private final Image mercadoAnimales = new Image(getClass().getResource("/imagenes/edificios/market_animales.png").toExternalForm());

    private final List<int[]> posicionesArboles = new ArrayList<>();
    private final List<int[]> posicionesPiedras = new ArrayList<>();
    private final Random random = new Random();

    private VBox menuEsc;
    private boolean menuEscVisible = false;

    private final Map<String, int[]> mercados = new HashMap<>();
    private final JugadorView jugadorView;

    public PuebloView(SceneManager sceneManager) {
        this.setPrefSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.canvas = new Canvas(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        // Posiciones de los mercados
        mercados.put("Semillas", new int[]{5, 4});
        mercados.put("Animales", new int[]{8, 4});


        // Inicializar jugador con mapa actual "pueblo"
        this.jugadorView = new JugadorView(
                TILE_SIZE, MAP_WIDTH, MAP_HEIGHT, "pueblo",
                new HashMap<>(), new HashSet<>(),
                (accion) -> {
                    if ("cambiar_granja".equals(accion)) {
                        cambioSolicitado = true;
                    }
                }
        );

        configurarControles();
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) this.requestFocus();
        });

        this.setFocusTraversable(true);
        this.requestFocus();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (cambioSolicitado) {
                    stop();
                    Platform.runLater(() -> {
                        jugadorView.limpiarTeclas();
                        GameContext.moverJugadorA(18, 7);
                        sceneManager.showGameView();
                    });
                    return;
                }

                String accion = jugadorView.actualizar(now);
                renderizar();

                if ("MercadoSemillas".equals(accion)) {
                    mostrarMenuSemillas();
                }else if ("MercadoGanadero".equals(accion)) {
                    mostrarMenuGanadero();
                }
            }
        };
        inicializarDecoraciones();
        timer.start();
    }

    private boolean cambioSolicitado = false;
    private Image obtenerImagenSuelo() {
        switch (GameContext.getGranja().getEstacion()) {
            case PRIMAVERA: return hierbaPrimavera;
            case VERANO: return hierbaVerano;
            case OTONIO: return hierbaOtonio;
            case INVIERNO: return hierbaInvierno;
            default: return hierbaPrimavera;
        }
    }
    private Image obtenerImagenArbol() {
        switch (GameContext.getGranja().getEstacion()) {
            case PRIMAVERA:
                return new Image(getClass().getResource("/imagenes/decoracion/arbol_primavera.png").toExternalForm());
            case VERANO:
                return new Image(getClass().getResource("/imagenes/decoracion/arbol_verano.png").toExternalForm());
            case OTONIO:
                return new Image(getClass().getResource("/imagenes/decoracion/arbol_otonio.png").toExternalForm());
            case INVIERNO:
                return new Image(getClass().getResource("/imagenes/decoracion/arbol_invierno.png").toExternalForm());
            default:
                return new Image(getClass().getResource("/imagenes/decoracion/arbol_primavera.png").toExternalForm());
        }
    }

    private void configurarControles() {
        this.setOnKeyPressed((KeyEvent e) -> {
            jugadorView.getKeysPressed().add(e.getCode());

            if (e.getCode() == KeyCode.ESCAPE) {
                if (!menuEscVisible) {
                    MenuEscUtils.mostrarMenuEsc(this, () -> mostrarInventario());
                } else {
                    this.getChildren().remove(menuEsc);
                    menuEscVisible = false;
                    this.requestFocus();
                }
            }
        });

        this.setOnKeyReleased((KeyEvent e) -> jugadorView.getKeysPressed().remove(e.getCode()));
    }


    private void mostrarMenuSemillas() {
        final MenuTiendaSemillas[] menuRef = new MenuTiendaSemillas[1];

        menuRef[0] = new MenuTiendaSemillas(() -> {
            this.getChildren().remove(menuRef[0]);
            this.requestFocus();
        });

        menuRef[0].setLayoutX(400);
        menuRef[0].setLayoutY(200);
        this.getChildren().add(menuRef[0]);
        this.requestFocus();
    }
    private void mostrarMenuGanadero() {
        Platform.runLater(() -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Mercado Ganadero");

            MenuTiendaGanadera contenido = new MenuTiendaGanadera(dialog::close);

            dialog.getDialogPane().setContent(contenido);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        });
    }




    private void renderizar() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gc.drawImage(obtenerImagenSuelo(), x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        for (int y = 6; y <= 7; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gc.drawImage(camino, x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        Image arbolActual = obtenerImagenArbol();
        for (int[] pos : posicionesArboles) {
            gc.drawImage(arbolActual, pos[0] * TILE_SIZE, pos[1] * TILE_SIZE);
        }


        for (int[] pos : posicionesPiedras) {
            gc.drawImage(piedra, pos[0] * TILE_SIZE, pos[1] * TILE_SIZE);
        }
        for (int x = 0; x < MAP_WIDTH; x++) {
            gc.drawImage(valla, x * TILE_SIZE, 5 * TILE_SIZE);
            gc.drawImage(valla, x * TILE_SIZE, 8 * TILE_SIZE);
        }

        for (Map.Entry<String, int[]> entry : mercados.entrySet()) {
            Image img = entry.getKey().equals("Semillas") ? mercadoSemillas : mercadoAnimales;
            int[] pos = entry.getValue();
            gc.drawImage(img, pos[0] * TILE_SIZE, pos[1] * TILE_SIZE, TILE_SIZE * 3, TILE_SIZE * 3);
        }

        jugadorView.renderizar(gc);
    }
    private void inicializarDecoraciones() {
        posicionesArboles.clear();
        posicionesPiedras.clear();

        int numArboles = 15 + random.nextInt(6);
        int numPiedras = 6 + random.nextInt(5);

        Set<String> ocupadas = new HashSet<>();

        // Árboles arriba
        while (posicionesArboles.size() < numArboles / 2) {
            int x = random.nextInt(MAP_WIDTH);
            int y = random.nextInt(4);
            // para evitar que los arboles salgan del mapa
            String clave1 = x + "-" + y;
            String clave2 = x + "-" + (y + 1);
            if (!ocupadas.contains(clave1) && !ocupadas.contains(clave2)) {
                posicionesArboles.add(new int[]{x, y});
                ocupadas.add(clave1);
                ocupadas.add(clave2);
            }
        }

        // Árboles abajo
        while (posicionesArboles.size() < numArboles) {
            int x = random.nextInt(MAP_WIDTH);
            int y = 10 + random.nextInt(6);
            String clave1 = x + "-" + y;
            String clave2 = x + "-" + (y + 1);
            if (!ocupadas.contains(clave1) && !ocupadas.contains(clave2)) {
                posicionesArboles.add(new int[]{x, y});
                ocupadas.add(clave1);
                ocupadas.add(clave2);
            }
        }

        // Piedras abajo
        while (posicionesPiedras.size() < numPiedras) {
            int x = random.nextInt(MAP_WIDTH);
            int y = 10 + random.nextInt(6);
            String clave = x + "-" + y;
            if (!ocupadas.contains(clave)) {
                posicionesPiedras.add(new int[]{x, y});
                ocupadas.add(clave);
            }
        }
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
