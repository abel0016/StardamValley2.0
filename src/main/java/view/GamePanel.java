package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private final int FPS = 60;

    private int x = 400;
    private int y = 300;
    private final int velocidad = 4;
    private final int tamañoJugador = 32;

    private boolean arriba, abajo, izquierda, derecha;

    private String zonaActual = "granja";
    private final List<Rectangle> obstaculos = new ArrayList<>();

    private Image tileHierba, tileAgua, tileArena, tileArbol;
    private Image jugadorUp1, jugadorUp2, jugadorDown1, jugadorDown2;
    private Image jugadorLeft1, jugadorLeft2, jugadorRight1, jugadorRight2;
    private Image jugadorActual;
    private int animFrame = 0;
    private int animContador = 0;
    private String direccion = "down";

    private final Map<Integer, Image> tiles = new HashMap<>();
    private int[][] mapaZona;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setPreferredSize(new Dimension(1280, 720));

        cargarSprites();
        cargarMapa("granja");

        obstaculos.add(new Rectangle(200, 200, 48, 64));
        obstaculos.add(new Rectangle(600, 150, 48, 64));
    }

    private void cargarSprites() {
        try {
            tileHierba = ImageIO.read(getClass().getResource("/imagenes/suelos/hierba.png"));
            tileAgua = ImageIO.read(getClass().getResource("/imagenes/suelos/agua.png"));
            tileArena = ImageIO.read(getClass().getResource("/imagenes/suelos/arena.png"));
            tileArbol = ImageIO.read(getClass().getResource("/imagenes/arbol.png"));

            jugadorUp1 = ImageIO.read(getClass().getResource("/imagenes/player/walk_up1.png"));
            jugadorUp2 = ImageIO.read(getClass().getResource("/imagenes/player/walk_up2.png"));
            jugadorDown1 = ImageIO.read(getClass().getResource("/imagenes/player/walk_down1.png"));
            jugadorDown2 = ImageIO.read(getClass().getResource("/imagenes/player/walk_down2.png"));
            jugadorLeft1 = ImageIO.read(getClass().getResource("/imagenes/player/walk_left1.png"));
            jugadorLeft2 = ImageIO.read(getClass().getResource("/imagenes/player/walk_left2.png"));
            jugadorRight1 = ImageIO.read(getClass().getResource("/imagenes/player/walk_right1.png"));
            jugadorRight2 = ImageIO.read(getClass().getResource("/imagenes/player/walk_right2.png"));

            jugadorActual = jugadorDown1;

            tiles.put(1, tileHierba);
            tiles.put(2, tileAgua);
            tiles.put(3, tileArena);
            tiles.put(4, tileArbol);

        } catch (IOException e) {
            System.err.println("Error al cargar imágenes: " + e.getMessage());
        }
    }

    private void cargarMapa(String nombreZona) {
        try {
            InputStream is = getClass().getResourceAsStream("/mapas/" + nombreZona + ".txt");
            if (is == null) {
                System.err.println("No se encontró el mapa de la zona: " + nombreZona);
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<int[]> filas = new ArrayList<>();
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.trim().split("\\s+");
                int[] fila = new int[partes.length];
                for (int i = 0; i < partes.length; i++) {
                    fila[i] = Integer.parseInt(partes[i]);
                }
                filas.add(fila);
            }
            mapaZona = filas.toArray(new int[0][]);
            reader.close();
        } catch (IOException e) {
            System.err.println("Error al cargar el mapa: " + e.getMessage());
        }
    }

    public void iniciarJuego() {
        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moverJugador();
        cambiarZonaSiCorresponde();
        repaint();
    }

    private void moverJugador() {
        boolean seMovio = false;
        int nuevoX = x, nuevoY = y;

        if (arriba) {
            nuevoY -= velocidad;
            direccion = "up";
            seMovio = true;
        }
        if (abajo) {
            nuevoY += velocidad;
            direccion = "down";
            seMovio = true;
        }
        if (izquierda) {
            nuevoX -= velocidad;
            direccion = "left";
            seMovio = true;
        }
        if (derecha) {
            nuevoX += velocidad;
            direccion = "right";
            seMovio = true;
        }

        Rectangle futuro = new Rectangle(nuevoX, nuevoY, tamañoJugador, tamañoJugador);
        boolean colision = obstaculos.stream().anyMatch(futuro::intersects);

        if (!colision) {
            x = nuevoX;
            y = nuevoY;
        }

        if (seMovio) {
            animContador++;
            if (animContador >= 10) {
                animFrame = (animFrame + 1) % 2;
                animContador = 0;
            }
        } else {
            animFrame = 0;
        }

        switch (direccion) {
            case "up" -> jugadorActual = (animFrame == 0) ? jugadorUp1 : jugadorUp2;
            case "down" -> jugadorActual = (animFrame == 0) ? jugadorDown1 : jugadorDown2;
            case "left" -> jugadorActual = (animFrame == 0) ? jugadorLeft1 : jugadorLeft2;
            case "right" -> jugadorActual = (animFrame == 0) ? jugadorRight1 : jugadorRight2;
        }
    }

    private void cambiarZonaSiCorresponde() {
        int ancho = getWidth();
        int alto = getHeight();

        if (zonaActual.equals("granja")) {
            if (x <= 0) {
                zonaActual = "lago";
                cargarMapa("lago");
                x = ancho - tamañoJugador;
            } else if (x + tamañoJugador >= ancho) {
                zonaActual = "pueblo";
                cargarMapa("pueblo");
                x = 0;
            } else if (y <= 0) {
                zonaActual = "civico";
                cargarMapa("centro_civico");
                y = alto - tamañoJugador;
            } else if (y + tamañoJugador >= alto) {
                zonaActual = "playa";
                cargarMapa("playa");
                y = 0;
            }
        } else {
            if (zonaActual.equals("lago") && x + tamañoJugador >= ancho) {
                zonaActual = "granja";
                cargarMapa("granja");
                x = 0;
            } else if (zonaActual.equals("pueblo") && x <= 0) {
                zonaActual = "granja";
                cargarMapa("granja");
                x = ancho - tamañoJugador;
            } else if (zonaActual.equals("centro_civico") && y + tamañoJugador >= alto) {
                zonaActual = "granja";
                cargarMapa("granja");
                y = 0;
            } else if (zonaActual.equals("playa") && y <= 0) {
                zonaActual = "granja";
                cargarMapa("granja");
                y = alto - tamañoJugador;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapaZona != null) {
            for (int fila = 0; fila < mapaZona.length; fila++) {
                for (int col = 0; col < mapaZona[fila].length; col++) {
                    int tileID = mapaZona[fila][col];
                    Image tile = tiles.get(tileID);
                    if (tile != null) {
                        g.drawImage(tile, col * 32, fila * 32, null);
                    }
                }
            }
        }

        g.drawImage(jugadorActual, x, y, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Zona actual: " + zonaActual.toUpperCase(), 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> arriba = true;
            case KeyEvent.VK_S -> abajo = true;
            case KeyEvent.VK_A -> izquierda = true;
            case KeyEvent.VK_D -> derecha = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> arriba = false;
            case KeyEvent.VK_S -> abajo = false;
            case KeyEvent.VK_A -> izquierda = false;
            case KeyEvent.VK_D -> derecha = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
