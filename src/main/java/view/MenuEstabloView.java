package view;

import app.GameContext;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import modelo.*;

import java.util.*;

public class MenuEstabloView extends VBox {

    private Label lblEstado;
    private Label lblCrias;
    private Label lblAdultos;
    private Label lblAncianos;
    private Label lblNoAlimentados;
    private Button btnAlimentar;

    public MenuEstabloView(Runnable onClose, String tipoAnimal) {
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #cfa76e; -fx-border-color: brown; -fx-border-width: 2px;");

        Granja granja = GameContext.getGranja();
        List<Animal> animales = granja.getEstablo().getAnimales()
                .stream()
                .filter(a -> a.getTipo().equalsIgnoreCase(tipoAnimal))
                .toList();


        // Icono
        ImageView icono = new ImageView(obtenerIconoAnimal(tipoAnimal.toLowerCase()));
        icono.setFitWidth(48);
        icono.setFitHeight(48);
        getChildren().add(icono);

        // Título
        Label titulo = new Label("Establo de " + tipoAnimal.toLowerCase());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        getChildren().add(titulo);

        if (animales.isEmpty()) {
            getChildren().add(new Label("No hay animales todavía"));
            Button cerrar = new Button("Cerrar");
            cerrar.setOnAction(e -> onClose.run());
            getChildren().add(cerrar);
            return;
        }

        // Labels de conteo
        lblCrias = new Label();
        lblAdultos = new Label();
        lblAncianos = new Label();
        lblNoAlimentados = new Label();
        lblEstado = new Label();

        getChildren().addAll(lblCrias, lblAdultos, lblAncianos, lblNoAlimentados, lblEstado);
        actualizarContadores(animales);

        // Alimentar
        Map<Integer, String> nombresAlimentos = TiendaGanadera.getInstancia().getNombreAlimentos();
        Map<Integer, Integer> invAlimentos = granja.getJugador().getAlimentos();

        ComboBox<String> combo = new ComboBox<>();
        Map<String, Integer> nombreAId = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : invAlimentos.entrySet()) {
            int id = entry.getKey();
            int cant = entry.getValue();
            if (cant > 0) {
                String nombre = nombresAlimentos.get(id);
                String item = nombre + " (x" + cant + ")";
                combo.getItems().add(item);
                nombreAId.put(item, id);
            }
        }

        TextField campoCantidad = new TextField("1");
        btnAlimentar = new Button("Dar de comer");
        btnAlimentar.setDisable(animales.stream().noneMatch(a -> !a.isHaComido()));

        btnAlimentar.setOnAction(e -> {
            String seleccion = combo.getValue();
            if (seleccion == null) {
                GameContext.mostrarNotificacion("Selecciona un alimento");
                return;
            }

            int idAlimento = nombreAId.get(seleccion);
            int cantidad;

            try {
                cantidad = Integer.parseInt(campoCantidad.getText());
                if (cantidad <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                GameContext.mostrarNotificacion("Cantidad inválida");
                return;
            }

            List<Animal> compatibles = granja.getEstablo().obtenerAnimalesPorTipo(tipoAnimal.toLowerCase());
            if (compatibles.isEmpty()) {
                GameContext.mostrarNotificacion("No tienes animales de este tipo");
                return;
            }

            if (compatibles.get(0).getIdAlimento() != idAlimento) {
                GameContext.mostrarNotificacion("Ese alimento no es compatible con los " + tipoAnimal);
                return;
            }

            if (!granja.consumirAlimento(idAlimento, cantidad)) {
                GameContext.mostrarNotificacion("No tienes suficiente cantidad");
                return;
            }

            Jugador jugador = granja.getJugador();
            if (!jugador.alimentarAnimalesManual(cantidad)) {
                GameContext.mostrarNotificacion("No tienes suficiente energía");
                return;
            }

            int alimentadosAhora = 0;
            for (Animal animal : compatibles) {
                if (!animal.isHaComido() && alimentadosAhora < cantidad) {
                    animal.setHaComido(true);
                    granja.getEstablo().actualizarEdadYComida(animal);
                    alimentadosAhora++;

                    int cantidadProducida = animal.producir();

                    if (cantidadProducida > 0) {
                        String tipo = animal.getTipo();
                        String nombreProducto = switch (tipo) {
                            case "vaca" -> "leche";
                            case "gallina" -> "huevo";
                            case "oveja" -> "lana";
                            case "cerdo" -> "trufa";
                            default -> "producto";
                        };
                        jugador.agregarProductoGanadero(nombreProducto, cantidadProducida);
                        System.out.println(animal.getNombre() + " ha producido " + cantidadProducida + " de " + nombreProducto);
                    }
                }
            }

            GameContext.mostrarNotificacion("Has alimentado a " + alimentadosAhora + " " + tipoAnimal.toLowerCase() + "(s)");

            actualizarContadores(compatibles);
            btnAlimentar.setDisable(compatibles.stream().noneMatch(a -> !a.isHaComido()));
        });

        getChildren().addAll(combo, campoCantidad, btnAlimentar);

        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> onClose.run());
        getChildren().add(cerrar);
    }

    private void actualizarContadores(List<Animal> animales) {
        long crias = animales.stream().filter(a -> a.getEstadoEdad() == Animal.EstadoEdad.CRIA).count();
        long adultos = animales.stream().filter(a -> a.getEstadoEdad() == Animal.EstadoEdad.ADULTO).count();
        long ancianos = animales.stream().filter(a -> a.getEstadoEdad() == Animal.EstadoEdad.ANCIANO).count();
        long noAlimentados = animales.stream().filter(a -> !a.isHaComido()).count();

        lblCrias.setText("Crías: " + crias);
        lblAdultos.setText("Adultos: " + adultos);
        lblAncianos.setText("Ancianos: " + ancianos);
        lblNoAlimentados.setText("No alimentados: " + noAlimentados);

        if (noAlimentados == 0) {
            lblEstado.setText("Todos los animales están alimentados");
        } else {
            lblEstado.setText("Necesitas " + noAlimentados + " raciones para alimentarlos");
        }
    }

    private Image obtenerIconoAnimal(String tipo) {
        try {
            return switch (tipo) {
                case "vaca" -> new Image(getClass().getResource("/imagenes/animales/icono_vaca.png").toExternalForm());
                case "gallina" -> new Image(getClass().getResource("/imagenes/animales/icono_gallina.png").toExternalForm());
                case "cerdo" -> new Image(getClass().getResource("/imagenes/animales/icono_cerdo.png").toExternalForm());
                case "oveja" -> new Image(getClass().getResource("/imagenes/animales/icono_oveja.png").toExternalForm());
                default -> null;
            };
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono para: " + tipo);
            return null;
        }
    }
}
