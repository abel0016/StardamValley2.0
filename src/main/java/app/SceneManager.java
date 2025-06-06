package app;

import javafx.scene.Scene;
import javafx.stage.Stage;
import view.*;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void showMainMenu() {
        MainMenuView menu = new MainMenuView(this);
        stage.setScene(new Scene(menu.getRoot(), 800, 600));
        stage.setTitle("Stardam Valley - Menú Principal");
        stage.show();
    }

    public void showGameView() {
        GameView game = new GameView(this);
        stage.setScene(new Scene(game, 1280, 960));
        stage.setTitle("Stardam Valley - Juego");
        stage.show();
    }

    public void showPuebloView() {
        PuebloView pueblo = new PuebloView(this);
        Scene scene = new Scene(pueblo, 1280, 960);
        stage.setScene(scene);
        stage.setTitle("Stardam Valley - Pueblo");
        stage.show();
    }
    public void showPlayaView() {
        PlayaView playa = new PlayaView(this);
        stage.setScene(new Scene(playa, 1280, 960));
        stage.setTitle("Stardam Valley - Playa");
        stage.show();
    }
    public void showCentroCivicoView() {
        CentroCivicoView view = new CentroCivicoView(this);
        Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.show();
    }


}
