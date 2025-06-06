package view;

import app.SceneManager;
import gestion.GestionFBinario;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import app.GameContext;
import javafx.scene.paint.Color;
import modelo.Granja;
import modelo.Jugador;
import modelo.Semilla;
import modelo.TiendaGanadera;

import java.util.*;
import java.util.function.Consumer;

public class GameView extends Pane {

    private static final int TILE_SIZE = 64;
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 15;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private boolean interaccionProcesada = false;

    private VBox menuEsc;
    private boolean menuEscVisible = false;

    private final Image hierbaPrimavera = new Image(getClass().getResource("/imagenes/suelos/hierba_primavera.png").toExternalForm());
    private final Image hierbaVerano = new Image(getClass().getResource("/imagenes/suelos/hierba_verano.png").toExternalForm());
    private final Image hierbaOtonio = new Image(getClass().getResource("/imagenes/suelos/hierba_otonio.png").toExternalForm());
    private final Image hierbaInvierno = new Image(getClass().getResource("/imagenes/suelos/hierba_invierno.png").toExternalForm());
    private final Image flor1 = new Image(getClass().getResource("/imagenes/decoracion/flor_blanca.png").toExternalForm());
    private final Image flor2 = new Image(getClass().getResource("/imagenes/decoracion/flor_roja.png").toExternalForm());
    private final Image edificioSinConstruir = new Image(getClass().getResource("/imagenes/edificios/edificio_sin_construir.png").toExternalForm());
    private final Image edificioGallinas = new Image(getClass().getResource("/imagenes/edificios/edificio_gallinas.png").toExternalForm());
    private final Image edificioOvejas = new Image(getClass().getResource("/imagenes/edificios/edicifio_ovejas.png").toExternalForm());
    private final Image edificioVacas = new Image(getClass().getResource("/imagenes/edificios/edicifio_vacas.png").toExternalForm());
    private final Image edificioCerdos = new Image(getClass().getResource("/imagenes/edificios/edicifio_cerdos.png").toExternalForm());
    private final Image imagenAlmacen = new Image(getClass().getResource("/imagenes/edificios/almacen.png").toExternalForm());


    private AnimationTimer timer;

    private final HuertoView huertoView;
    private final JugadorView jugadorView;

    private final Map<String, int[]> edificios = new HashMap<>();
    private final Set<String> edificiosConstruidos = new HashSet<>();

    public GameView(SceneManager sceneManager) {
        this.setPrefSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.canvas = new Canvas(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);


        edificios.put("Gallinero", new int[]{7, 2, 2000});
        edificios.put("Ovejas", new int[]{9, 2, 3500});
        edificios.put("Vacas", new int[]{11, 2, 5000});
        edificios.put("Cerdos", new int[]{13, 2, 6500});
        edificios.put("almacen", new int[]{6, 6, 0});



        Granja granja = GameContext.getGranja();
        if (granja != null) {
            edificiosConstruidos.addAll(granja.getEdificiosConstruidos());
        }

        this.huertoView = new HuertoView(
                new Image(getClass().getResource("/imagenes/suelos/tierra.png").toExternalForm()),
                new Image(getClass().getResource("/imagenes/semillas/semilla.png").toExternalForm()),
                cargarImagenesFrutos(),
                new Image(getClass().getResource("/imagenes/suelos/tierramojada.png").toExternalForm())
        );

        JugadorView[] tempJugador = new JugadorView[1];

        Consumer<String> accion = (accionTexto) -> {
            Platform.runLater(() -> {
                tempJugador[0].limpiarTeclas();
                switch (accionTexto) {
                    case "cambiar_pueblo":
                        GameContext.moverJugadorA(1, 7); // izquierda, centro
                        sceneManager.showPuebloView();
                        break;
                    case "cambiar_playa":
                        GameContext.moverJugadorA(10, 1); // centro, arriba
                        sceneManager.showPlayaView();
                        break;
                    case "cambiar_centro_civico":
                        GameContext.moverJugadorA(10, 13); // centro, abajo
                        sceneManager.showCentroCivicoView();
                        break;
                }
            });
        };


        JugadorView jugador = new JugadorView(
                TILE_SIZE, MAP_WIDTH, MAP_HEIGHT, "granja", edificios, edificiosConstruidos,
                accion
        );
        this.jugadorView = jugador;
        tempJugador[0] = jugador;

        jugadorView.setMapaActual("granja");

        configurarControles();

        this.setFocusTraversable(true);
        this.requestFocus();
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                this.requestFocus();
            }
        });

        GameView thisGameView = this;
        this.timer = new AnimationTimer() {
            public void handle(long now) {
                String accion = jugadorView.actualizar(now);
                renderizar();

                if ("huerto".equals(accion)) {
                    huertoView.mostrarMenu(GameView.this);
                } else if ("almacen".equals(accion)) {
                    if (!interaccionProcesada && jugadorView.getKeysPressed().contains(KeyCode.E)) {
                        interaccionProcesada = true;
                        Platform.runLater(() -> {
                            MenuAlmacenView.mostrar(GameView.this);
                            jugadorView.limpiarTeclas();
                        });
                    }
                } else if (accion != null && edificios.containsKey(accion)) {
                    int[] pos = edificios.get(accion);
                    if (!edificiosConstruidos.contains(accion)) {
                        if (!interaccionProcesada && jugadorView.getKeysPressed().contains(KeyCode.E)) {
                            interaccionProcesada = true;
                            manejarInteraccionEdificio(accion, pos[2]);
                            jugadorView.limpiarTeclas();
                        }
                    } else {
                        if (!interaccionProcesada && jugadorView.getKeysPressed().contains(KeyCode.E)) {
                            interaccionProcesada = true;

                            Platform.runLater(() -> {
                                String tipoAnimal = switch (accion) {
                                    case "Vacas" -> "vaca";
                                    case "Ovejas" -> "oveja";
                                    case "Cerdos" -> "cerdo";
                                    case "Gallinero" -> "gallina";
                                    default -> null;
                                };

                                if (tipoAnimal != null) {
                                    MenuEstabloView[] vista = new MenuEstabloView[1];
                                    vista[0] = new MenuEstabloView(() -> {
                                        thisGameView.getChildren().remove(vista[0]);
                                        thisGameView.requestFocus();
                                        interaccionProcesada = false;
                                    }, tipoAnimal);

                                    // Centramos la vista en pantalla
                                    vista[0].setLayoutX((thisGameView.getWidth() - 300) / 2);
                                    vista[0].setLayoutY((thisGameView.getHeight() - 250) / 2);

                                    thisGameView.getChildren().add(vista[0]);
                                    vista[0].requestFocus();
                                }

                                jugadorView.limpiarTeclas();
                            });

                        }
                    }
                } else {
                    interaccionProcesada = false;
                }

                if ("cambiar_pueblo".equals(accion)) {
                    timer.stop();
                }
                if ("cambiar_playa".equals(accion)) {
                    timer.stop();
                }
            }
        };

        GameContext.setRootPane(this);
        this.timer.start();
    }
    private Image obtenerImagenSuelo() {
        switch (GameContext.getGranja().getEstacion()) {
            case PRIMAVERA: return hierbaPrimavera;
            case VERANO: return hierbaVerano;
            case OTONIO: return hierbaOtonio;
            case INVIERNO: return hierbaInvierno;
            default: return hierbaPrimavera;
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



    private void mostrarMenuHuerto() {
        huertoView.mostrarMenu(this);
    }

    private void renderizar() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gc.drawImage(obtenerImagenSuelo(), x * TILE_SIZE, y * TILE_SIZE);
                if (x % 7 == 0 && y % 5 == 0) gc.drawImage(flor1, x * TILE_SIZE, y * TILE_SIZE);
                if (x % 6 == 0 && y % 4 == 0) gc.drawImage(flor2, x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        int energia = GameContext.getGranja().getJugador().getEnergiaActual();
        gc.fillText("", 18.5 * TILE_SIZE, 0.7 * TILE_SIZE);
        gc.setFill(Color.BROWN);
        gc.fillRect(19 * TILE_SIZE, TILE_SIZE / 2.0, TILE_SIZE / 4.0, TILE_SIZE);
        gc.setFill(Color.YELLOW);
        double altoEnergia = TILE_SIZE * (energia / 100.0);
        gc.fillRect(19 * TILE_SIZE, TILE_SIZE / 2.0 + (TILE_SIZE - altoEnergia), TILE_SIZE / 4.0, altoEnergia);

        huertoView.renderizar(gc);

        for (Map.Entry<String, int[]> entry : edificios.entrySet()) {
            int[] pos = entry.getValue();
            String nombre = entry.getKey();
            if (nombre.equals("almacen")) continue;
            Image img = edificioSinConstruir;
            if (edificiosConstruidos.contains(nombre)) {
                switch (nombre) {
                    case "Gallinero": img = edificioGallinas; break;
                    case "Ovejas": img = edificioOvejas; break;
                    case "Vacas": img = edificioVacas; break;
                    case "Cerdos": img = edificioCerdos; break;
                }
            }
            gc.drawImage(img, pos[0] * TILE_SIZE, pos[1] * TILE_SIZE, TILE_SIZE * 2, TILE_SIZE * 2);
        }
        // Dibuja el almacén en la posición (7,7)
        gc.drawImage(imagenAlmacen, 6 * TILE_SIZE, 6 * TILE_SIZE, TILE_SIZE * 2, TILE_SIZE * 2);


        jugadorView.renderizar(gc);
    }

    private void manejarInteraccionEdificio(String nombre, int precio) {
        Platform.runLater(() -> {
            double presupuesto = GameContext.getGranja().getPresupuesto();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Construcción");
            confirm.setHeaderText("¿Construir " + nombre + "?");
            confirm.setContentText("Precio: " + precio + " monedas");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (presupuesto >= precio) {
                    GameContext.getGranja().setPresupuesto(presupuesto - precio);
                    edificiosConstruidos.add(nombre);
                    GameContext.getGranja().setEdificiosConstruidos(new HashSet<>(edificiosConstruidos));
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Construcción completada");
                    alert.setContentText(nombre + " construido por " + precio + " monedas");
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Fondos insuficientes");
                    alert.setContentText("Necesitas " + precio + " monedas para construir " + nombre);
                    alert.show();
                }
            }
        });
    }

    private Map<Integer, Image> cargarImagenesFrutos() {
        Map<Integer, Image> imagenes = new HashMap<>();
        for (int id = 1; id <= 25; id++) {
            try {
                Image img = new Image(getClass().getResource("/imagenes/frutos/" + id + ".png").toExternalForm());
                imagenes.put(id, img);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen del fruto ID " + id);
            }
        }
        return imagenes;
    }

    private void mostrarInventario() {
        final MenuInventarioView[] menuInv = new MenuInventarioView[1];
        menuInv[0] = new MenuInventarioView(() -> {
            this.getChildren().remove(menuInv[0]);
            this.requestFocus();
        });
        this.getChildren().add(menuInv[0]);
    }


    public void redibujar() {
        renderizar();
    }
    private boolean jugadorCercaDe(int x, int y) {
        int px = GameContext.getGranja().getPlayerX();
        int py = GameContext.getGranja().getPlayerY();
        return Math.abs(px - x) <= 1 && Math.abs(py - y) <= 1;
    }
    private void mostrarNotificacionNivel(String mensaje) {
        Label notificacion = new Label(mensaje);
        notificacion.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 16px;");
        notificacion.setLayoutX((MAP_WIDTH * TILE_SIZE) / 2.0 - 150);
        notificacion.setLayoutY(MAP_HEIGHT * TILE_SIZE - 60);

        this.getChildren().add(notificacion);

        new Thread(() -> {
            try {
                Thread.sleep(3000); // 3 segundos
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> this.getChildren().remove(notificacion));
        }).start();
    }
    private boolean esEstabloAnimal(String edificio) {
        return edificio.equalsIgnoreCase("vaca") ||
                edificio.equalsIgnoreCase("oveja") ||
                edificio.equalsIgnoreCase("cerdo") ||
                edificio.equalsIgnoreCase("gallina");
    }


}