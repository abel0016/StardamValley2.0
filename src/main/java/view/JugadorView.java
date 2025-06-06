package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import app.GameContext;
import modelo.Granja;

import java.util.*;
import java.util.function.Consumer;

public class JugadorView {

    private static final long TIEMPO_ENTRE_MOVIMIENTOS = 150_000_000;

    private final int tileSize;
    private final int mapWidth;
    private final int mapHeight;
    private String nombreMapaActual;
    private final Consumer<String> accionCambioMapa;

    private final Map<String, Image[]> animacionesJugador = new HashMap<>();
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private String direccionActual = "down";
    private int frameActual = 0;
    private long ultimoMovimiento = 0;
    private boolean eProcesada = false;
    private long cooldownCambioMapa = 0;

    private final Map<String, int[]> edificios;
    private final Set<String> edificiosConstruidos;
    private final Set<String> colisionesPersonalizadas = new HashSet<>();

    public JugadorView(int tileSize, int mapWidth, int mapHeight,
                       String nombreMapaActual,
                       Map<String, int[]> edificios, Set<String> construidos,
                       Consumer<String> accionCambioMapa) {
        this.tileSize = tileSize;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.nombreMapaActual = nombreMapaActual;
        this.edificios = edificios;
        this.edificiosConstruidos = construidos;
        this.accionCambioMapa = accionCambioMapa;

        cargarAnimaciones();
        inicializarColisiones();
    }

    private void cargarAnimaciones() {
        animacionesJugador.put("down", new Image[]{
                new Image(getClass().getResource("/imagenes/player/walk_down1.png").toExternalForm()),
                new Image(getClass().getResource("/imagenes/player/walk_down2.png").toExternalForm())
        });
        animacionesJugador.put("up", new Image[]{
                new Image(getClass().getResource("/imagenes/player/walk_up1.png").toExternalForm()),
                new Image(getClass().getResource("/imagenes/player/walk_up2.png").toExternalForm())
        });
        animacionesJugador.put("left", new Image[]{
                new Image(getClass().getResource("/imagenes/player/walk_left1.png").toExternalForm()),
                new Image(getClass().getResource("/imagenes/player/walk_left2.png").toExternalForm())
        });
        animacionesJugador.put("right", new Image[]{
                new Image(getClass().getResource("/imagenes/player/walk_right1.png").toExternalForm()),
                new Image(getClass().getResource("/imagenes/player/walk_right2.png").toExternalForm())
        });
    }

    private void inicializarColisiones() {
        colisionesPersonalizadas.clear();

        if ("pueblo".equals(nombreMapaActual)) {
            for (int x = 0; x < mapWidth; x++) {
                colisionesPersonalizadas.add(x + "," + 5);
                colisionesPersonalizadas.add(x + "," + 8);
            }

            for (int x = 5; x < 8; x++) {
                for (int y = 4; y < 7; y++) {
                    colisionesPersonalizadas.add(x + "," + y);
                }
            }

            for (int x = 8; x < 11; x++) {
                for (int y = 4; y < 7; y++) {
                    colisionesPersonalizadas.add(x + "," + y);
                }
            }

            for (int y = 0; y < mapHeight; y++) {
                colisionesPersonalizadas.add((mapWidth - 1) + "," + y);
            }
        }

        if ("playa".equals(nombreMapaActual)) {
            // Bloquear bordes izquierdo y derecho
            for (int y = 0; y < mapHeight; y++) {
                colisionesPersonalizadas.add("0," + y);
                colisionesPersonalizadas.add((mapWidth - 1) + "," + y);
            }

            // Bloquear todas las celdas de agua clara, excepto el muelle
            for (int y = 7; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {
                    if (!(x == 10 && y >= 7 && y <= 10)) {
                        colisionesPersonalizadas.add(x + "," + y);
                    }
                }
            }

            // Aguas profundas alrededor de la última celda del muelle (10,10)
            int[][] aguasProfundas = {
                    {9, 10}, {11, 10},
                    {9, 11}, {10, 11}, {11, 11}
            };
            for (int[] pos : aguasProfundas) {
                colisionesPersonalizadas.add(pos[0] + "," + pos[1]);
            }
        }

        if ("centro_civico".equals(nombreMapaActual)) {
            // Bordes del mapa
            for (int y = 0; y < mapHeight; y++) {
                colisionesPersonalizadas.add("0," + y);
                colisionesPersonalizadas.add((mapWidth - 1) + "," + y);
            }
            for (int x = 0; x < mapWidth; x++) {
                colisionesPersonalizadas.add(x + ",0");
            }

            // Edificio centro cívico (bloquea del 9,1 al 12,2)
            for (int x = 9; x <= 12; x++) {
                for (int y = 1; y <= 2; y++) {
                    colisionesPersonalizadas.add(x + "," + y);
                }
            }
        }

    }

    public String actualizar(long now) {
        if (now - ultimoMovimiento < TIEMPO_ENTRE_MOVIMIENTOS) return null;

        Granja granja = GameContext.getGranja();
        int playerX = granja.getPlayerX();
        int playerY = granja.getPlayerY();

        int nuevaX = playerX;
        int nuevaY = playerY;

        if (keysPressed.contains(KeyCode.W)) { nuevaY--; direccionActual = "up"; }
        else if (keysPressed.contains(KeyCode.S)) { nuevaY++; direccionActual = "down"; }
        else if (keysPressed.contains(KeyCode.A)) { nuevaX--; direccionActual = "left"; }
        else if (keysPressed.contains(KeyCode.D)) { nuevaX++; direccionActual = "right"; }

        boolean bloqueado = false;

        for (Map.Entry<String, int[]> entry : edificios.entrySet()) {
            String nombre = entry.getKey();
            int[] pos = entry.getValue();
            int tamaño = nombre.equalsIgnoreCase("almacen") ? 1 : 2;

            for (int dx = 0; dx < tamaño; dx++) {
                for (int dy = 0; dy < tamaño; dy++) {
                    if (nuevaX == pos[0] + dx && nuevaY == pos[1] + dy) bloqueado = true;
                }
            }
        }

        if (colisionesPersonalizadas.contains(nuevaX + "," + nuevaY)) bloqueado = true;

        if (!bloqueado && nuevaX >= 0 && nuevaX < mapWidth && nuevaY >= 0 && nuevaY < mapHeight) {
            granja.setPlayerX(nuevaX);
            granja.setPlayerY(nuevaY);
        }

        frameActual = (frameActual + 1) % animacionesJugador.get(direccionActual).length;
        ultimoMovimiento = now;

        if (now > cooldownCambioMapa) {
            if ("granja".equals(nombreMapaActual)) {
                if (granja.getPlayerX() == mapWidth - 1) {
                    cooldownCambioMapa = now + 1_500_000_000;
                    accionCambioMapa.accept("cambiar_pueblo");
                } else if (granja.getPlayerY() == mapHeight - 1) {
                    cooldownCambioMapa = now + 1_500_000_000;
                    accionCambioMapa.accept("cambiar_playa");
                } else if (granja.getPlayerY() == 0) {
                    cooldownCambioMapa = now + 1_500_000_000;
                    accionCambioMapa.accept("cambiar_centro_civico");
                }
            } else if ("pueblo".equals(nombreMapaActual) && granja.getPlayerX() == 0) {
                cooldownCambioMapa = now + 1_500_000_000;
                accionCambioMapa.accept("cambiar_granja");
            } else if ("playa".equals(nombreMapaActual) && granja.getPlayerY() == 0) {
                cooldownCambioMapa = now + 1_500_000_000;
                accionCambioMapa.accept("cambiar_granja");
            } else if ("centro_civico".equals(nombreMapaActual) && granja.getPlayerY() == mapHeight - 1) {
                cooldownCambioMapa = now + 1_500_000_000;
                accionCambioMapa.accept("cambiar_granja");
            }

        }

        if (keysPressed.contains(KeyCode.E)) {
            if (!eProcesada) {
                eProcesada = true;

                if (estaEnHuerto(granja.getPlayerX(), granja.getPlayerY())) return "huerto";

                if ("pueblo".equals(nombreMapaActual)) {
                    int x = granja.getPlayerX();
                    int y = granja.getPlayerY();
                    if (y == 7 && x >= 5 && x <= 7) return "MercadoSemillas";
                    if (y == 7 && x >= 8 && x <= 10) return "MercadoGanadero";
                }

                for (Map.Entry<String, int[]> entry : edificios.entrySet()) {
                    String nombreEdificio = entry.getKey();
                    int[] pos = entry.getValue();
                    int tamaño = nombreEdificio.equalsIgnoreCase("almacen") ? 1 : 2;

                    for (int dx = 0; dx < tamaño; dx++) {
                        for (int dy = 0; dy < tamaño; dy++) {
                            int ex = pos[0] + dx;
                            int ey = pos[1] + dy;
                            boolean estaCerca = (Math.abs(granja.getPlayerX() - ex) == 1 && granja.getPlayerY() == ey) ||
                                    (Math.abs(granja.getPlayerY() - ey) == 1 && granja.getPlayerX() == ex);
                            if (estaCerca) {
                                return nombreEdificio;
                            }
                        }
                    }
                }
            }
        } else {
            eProcesada = false;
        }

        return null;
    }

    public void renderizar(GraphicsContext gc) {
        Granja granja = GameContext.getGranja();
        int playerX = granja.getPlayerX();
        int playerY = granja.getPlayerY();
        Image frame = animacionesJugador.get(direccionActual)[frameActual];
        gc.drawImage(frame, playerX * tileSize, playerY * tileSize);
    }

    public Set<KeyCode> getKeysPressed() {
        return keysPressed;
    }

    public void setMapaActual(String nombreMapa) {
        this.nombreMapaActual = nombreMapa;
    }

    private boolean estaEnHuerto(int x, int y) {
        return x >= 0 && x < 6 && y >= 0 && y < 6;
    }

    public void limpiarTeclas() {
        keysPressed.clear();
    }
}
