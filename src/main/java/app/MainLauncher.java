package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
