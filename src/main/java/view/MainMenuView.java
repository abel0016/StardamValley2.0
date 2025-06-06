package view;

import app.ConfiguracionInicialView;
import app.GameContext;
import app.SceneManager;
import gestion.GestionFBinario;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modelo.Granja;

import java.io.File;

public class MainMenuView {

    private final VBox root;

    public MainMenuView(SceneManager sceneManager) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40; -fx-background-color: #C8E6C9;");

        Label titulo = new Label("\uD83C\uDF3E Stardam Valley \uD83C\uDF3E");
        titulo.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Button btnNueva = new Button("Nueva Partida");
        Button btnCargar = new Button("Cargar Partida");
        Button btnSalir = new Button("Salir");

        btnNueva.setOnAction(e -> {
            ConfiguracionInicialView.mostrar(root.getScene().getWindow(), sceneManager);
        });

        btnCargar.setDisable(!new File(GestionFBinario.RUTA_FICHERO_BINARIO).exists());
        btnCargar.setOnAction(e -> {
            Granja granja = GestionFBinario.getInstancia().cargarPartidaGuardada();
            if (granja != null) {
                GameContext.setGranja(granja);
                sceneManager.showGameView();
            } else {
                System.out.println("No se pudo cargar la partida");
            }
        });

        btnSalir.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(titulo, btnNueva, btnCargar, btnSalir);
    }

    public VBox getRoot() {
        return root;
    }
}
