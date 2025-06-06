package view;
import javax.swing.*;

public class VentanaJuego extends JFrame {

    public VentanaJuego() {
        setTitle("Stardam Valley");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        panel.iniciarJuego();
    }

    public static void main(String[] args) {
        new VentanaJuego();
    }
}
