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
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace(); // para consola
            try (java.io.PrintWriter out = new java.io.PrintWriter("error.log")) {
                e.printStackTrace(out); // para archivo
            } catch (Exception ignored) {}
        }
    }
}
