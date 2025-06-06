package view;

import app.GameContext;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import modelo.Jugador;

import java.util.HashMap;
import java.util.Map;

public class MenuHabilidadesView {

    public static void mostrar(Pane parent) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 20; -fx-border-color: black;");
        layout.setLayoutX(200);
        layout.setLayoutY(100);

        Label titulo = new Label("Habilidades del Jugador");
        titulo.setFont(new Font(18));
        layout.getChildren().add(titulo);

        Jugador jugador = GameContext.getGranja().getJugador();
        Map<String, Integer> niveles = jugador.getHabilidades();
        Map<String, Integer> experiencia = jugador.getExperienciaHabilidades();

        Map<String, String> descripciones = new HashMap<>();
        descripciones.put("Regar", "Cada nivel aumenta un 5% la probabilidad de no gastar energía al regar");
        descripciones.put("Pesca", "Cada nivel aumenta un 5% la probabilidad de conseguir un pez trofeo");
        descripciones.put("Cosechar", "Cada nivel aumenta un 5% la probabilidad de que se replante una semilla automáticamente");
        descripciones.put("Ganadería", "Cada nivel aumenta un 5% la probabilidad de obtener un producto de calidad de tus animales");

        for (String habilidad : niveles.keySet()) {
            int nivel = niveles.get(habilidad);
            int exp = experiencia.getOrDefault(habilidad, 0);

            Label label = new Label(habilidad + " - Nivel " + nivel + " (" + exp + "/100)");
            ProgressBar barra = new ProgressBar(exp / 100.0);
            barra.setPrefWidth(200);

            Tooltip tooltip = new Tooltip(descripciones.getOrDefault(habilidad, "Sin descripción"));
            Tooltip.install(label, tooltip);
            Tooltip.install(barra, tooltip);

            HBox fila = new HBox(10, label, barra);
            fila.setPadding(new Insets(5));
            layout.getChildren().add(fila);
        }

        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> {
            parent.getChildren().remove(layout);
            parent.requestFocus();
        });

        layout.getChildren().add(cerrar);
        parent.getChildren().add(layout);
    }
}
