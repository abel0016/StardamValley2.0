package view;

import app.GameContext;
import gestion.GestionFicheroHuerto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modelo.Granja;
import modelo.Semilla;

import java.io.RandomAccessFile;
import java.util.*;

public class HuertoView {

    private static final int TILE_SIZE = 64;
    private static final int HUERTO_ANCHO = 6;
    private static final int HUERTO_ALTO = 6;
    private static final int HUERTO_X_INICIO = 0;
    private static final int HUERTO_Y_INICIO = 0;

    private final Image imagenTierra;
    private final Image imagenSemilla;
    private final Map<Integer, Image> imagenFrutos;
    private final Image imagenTierraMojada;

    public HuertoView(Image imagenTierra, Image imagenSemilla, Map<Integer, Image> imagenFrutos, Image imagenTierraMojada) {
        this.imagenTierra = imagenTierra;
        this.imagenSemilla = imagenSemilla;
        this.imagenFrutos = imagenFrutos;
        this.imagenTierraMojada = imagenTierraMojada;
    }

    public void renderizar(GraphicsContext gc) {
        Granja granja = GameContext.getGranja();
        GestionFicheroHuerto.CeldaHuerto[][] estadoHuerto = granja.getHuerto().obtenerEstadoHuerto();
        Map<Integer, Semilla> semillas = granja.getSemillasPorClave();

        for (int fila = 0; fila < HUERTO_ALTO; fila++) {
            for (int col = 0; col < HUERTO_ANCHO; col++) {
                int x = HUERTO_X_INICIO + col;
                int y = HUERTO_Y_INICIO + fila;

                GestionFicheroHuerto.CeldaHuerto celda = estadoHuerto[fila][col];
                int id = celda.idSemilla;

                if (celda.regado) {
                    gc.drawImage(imagenTierraMojada, x * TILE_SIZE, y * TILE_SIZE);
                } else {
                    gc.drawImage(imagenTierra, x * TILE_SIZE, y * TILE_SIZE);
                }

                if (id != -1 && semillas.containsKey(id)) {
                    Semilla s = semillas.get(id);
                    if (celda.diasPlantado < s.getDiasCrecimiento()) {
                        gc.drawImage(imagenSemilla, x * TILE_SIZE, y * TILE_SIZE);
                    } else {
                        Image imagenFruto = imagenFrutos.get(id);
                        if (imagenFruto != null) {
                            gc.drawImage(imagenFruto, x * TILE_SIZE, y * TILE_SIZE);
                        }
                    }
                }
            }
        }
    }

    public void mostrarMenu(Pane parent) {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 20; -fx-border-color: black;");
        menu.setLayoutX(200);
        menu.setLayoutY(200);

        Button btnRegar = new Button("Regar");
        Button btnPlantar = new Button("Plantar");
        Button btnCosechar = new Button("Cosechar");
        Button btnCerrar = new Button("Cerrar");

        btnRegar.setOnAction(e -> {
            var jugador = GameContext.getGranja().getJugador();
            var huerto = GameContext.getGranja().getHuerto();
            int regadas = huerto.regarTodo();
            boolean pudo = jugador.intentarRegar(regadas);
            if (pudo) {
                ((GameView) parent).redibujar();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Se han regado " + regadas + " casillas");
                alert.show();
            }
        });


        btnPlantar.setOnAction(e -> mostrarMenuPlantado(parent));

        btnCosechar.setOnAction(e -> {
            GameContext.getGranja().atenderCultivos();
            ((GameView) parent).redibujar();
        });

        btnCerrar.setOnAction(e -> {
            parent.getChildren().remove(menu);
            parent.requestFocus();
        });

        menu.getChildren().addAll(btnRegar, btnPlantar, btnCosechar, btnCerrar);
        parent.getChildren().add(menu);
    }

    private void mostrarMenuPlantado(Pane parent) {
        Granja granja = GameContext.getGranja();
        Map<Semilla, Integer> semillasDisponibles = new HashMap<>(granja.getJugador().getSemillasInventario());
        if (semillasDisponibles.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Inventario vacío");
            alert.setContentText("No tienes semillas disponibles para plantar");
            alert.show();
            return;
        }

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));
        Map<Semilla, Spinner<Integer>> spinners = new HashMap<>();

        for (Semilla semilla : semillasDisponibles.keySet()) {
            HBox fila = new HBox(10);
            Label label = new Label(semilla.getNombre());
            Spinner<Integer> spinner = new Spinner<>(0, semillasDisponibles.get(semilla), 0);
            spinners.put(semilla, spinner);
            fila.getChildren().addAll(label, spinner);
            layout.getChildren().add(fila);
        }

        Button btnAceptar = new Button("Plantar");
        btnAceptar.setOnAction(e -> {
            int filas = Integer.parseInt(granja.getConf().getPropiedad("filasHuerto"));
            int columnas = Integer.parseInt(granja.getConf().getPropiedad("columnasHuerto"));
            int totalPlantadas = 0;

            try (RandomAccessFile raf = new RandomAccessFile(new java.io.File(System.getProperty("user.home") + "/.stardamvalley/huerto.dat"), "rw")) {
                for (int fila = 0; fila < filas; fila++) {
                    for (int col = 0; col < columnas; col++) {
                        long offset = (fila * columnas + col) * (Integer.BYTES + 1 + Integer.BYTES);
                        raf.seek(offset);
                        int idActual = raf.readInt();
                        if (idActual != -1) continue;

                        for (Map.Entry<Semilla, Spinner<Integer>> entrada : spinners.entrySet()) {
                            Semilla semilla = entrada.getKey();
                            Spinner<Integer> spinner = entrada.getValue();
                            int cantidadActual = spinner.getValue();

                            if (cantidadActual > 0 && granja.getJugador().usarSemilla(semilla)) {
                                raf.seek(offset);
                                raf.writeInt(semilla.getId());
                                raf.writeBoolean(false);
                                raf.writeInt(0);
                                spinner.getValueFactory().setValue(cantidadActual - 1);
                                totalPlantadas++;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error al plantar semillas", ex);
            }

            Alert resumen = new Alert(Alert.AlertType.INFORMATION);
            resumen.setHeaderText("Plantado completado");
            resumen.setContentText("Se han plantado " + totalPlantadas + " semillas en total");
            resumen.show();

            ((GameView) parent).redibujar();
            parent.requestFocus();
        });

        layout.getChildren().add(btnAceptar);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setPrefHeight(300);
        Alert ventana = new Alert(Alert.AlertType.NONE);
        ventana.setTitle("Plantar semillas");
        ventana.getDialogPane().setContent(scroll);
        ventana.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        ventana.show();
    }
}
