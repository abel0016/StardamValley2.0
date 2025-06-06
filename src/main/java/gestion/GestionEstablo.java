package gestion;

import modelo.*;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class GestionEstablo implements Serializable {

    private transient Connection conexion;
    private Granja granja;

    public GestionEstablo(Granja granja) {
        this.granja = granja;
        this.conexion = ConexionBD.getInstance().getConnection();
    }

    public Granja getGranja() {
        return granja;
    }

    public void insertarAnimal(Animal animal) {
        String sql = "INSERT INTO animales (tipo, nombre, dias_vida, haComido, peso, id_alimento, fecha_adquisicion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, animal.getTipo());
            stmt.setString(2, animal.getNombre());
            stmt.setInt(3, animal.getEdad());
            stmt.setBoolean(4, animal.isHaComido());

            if (animal instanceof Vaca vaca) {
                stmt.setDouble(5, vaca.getPeso());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            stmt.setInt(6, animal.getIdAlimento());
            stmt.setDate(7, java.sql.Date.valueOf(java.time.LocalDate.now()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAnimal(String nombre) {
        String sql = "DELETE FROM animales WHERE nombre = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Animal> obtenerAnimalesPorTipo(String tipo) {
        List<Animal> lista = new ArrayList<>();
        String sql = "SELECT * FROM animales WHERE tipo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Animal animal = crearAnimalDesdeBD(rs);
                lista.add(animal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void darDeComer(String tipo, int cantidad) {
        List<Animal> animales = obtenerAnimalesPorTipo(tipo);
        int alimentados = 0;

        for (Animal animal : animales) {
            if (alimentados < cantidad) {
                animal.setHaComido(true);
                animal.incrementarEdad();
                actualizarEdadYComida(animal);
                alimentados++;
            } else {
                animal.setHaComido(false);
                actualizarEdadYComida(animal);
            }
        }
    }

    public List<Alimento> obtenerAlimentosParaTipo(String tipo) {
        List<Alimento> lista = new ArrayList<>();
        String sql = "SELECT * FROM alimentos WHERE tipo_animal = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                lista.add(new Alimento(nombre, precio));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void actualizarEdadYComida(Animal animal) {
        String sql = "UPDATE animales SET dias_vida = ?, haComido = ? WHERE nombre = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, animal.getEdad());
            stmt.setInt(2, animal.isHaComido() ? 1 : 0);
            stmt.setString(3, animal.getNombre());
            int filas = stmt.executeUpdate();
            System.out.println("Nombre: " + animal.getNombre() + ", haComido: " + animal.isHaComido());
            System.out.println("Actualizadas: " + filas + " filas para " + animal.getNombre());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Animal crearAnimalDesdeBD(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        String nombre = rs.getString("nombre");
        int edad = rs.getInt("dias_vida");
        double peso = rs.getDouble("peso");
        boolean haComido = rs.getBoolean("haComido");

        Animal animal = switch (tipo.toLowerCase()) {
            case "vaca" -> new Vaca(nombre, peso);
            case "oveja" -> new Oveja(nombre);
            case "gallina" -> new Gallina(nombre);
            case "cerdo" -> new Cerdo(nombre);
            default -> null;
        };

        if (animal != null) {
            animal.setEdad(edad);
            animal.setHaComido(haComido);
        }

        return animal;
    }

    public void producirGanado(String tipo, Jugador jugador) {
        List<Animal> animales = obtenerAnimalesPorTipo(tipo);
        Random rand = new Random();
        double probabilidad = jugador.getProbabilidadProductoCalidad();

        for (Animal animal : animales) {
            if (animal.isHaComido()) {
                int cantidad = animal.producir();
                if (cantidad > 0) {
                    String nombreProducto = switch (animal.getTipo()) {
                        case "gallina" -> "Huevo";
                        case "vaca" -> "Leche";
                        case "oveja" -> "Lana";
                        case "cerdo" -> "Trufa";
                        default -> "Producto";
                    };

                    jugador.agregarProductoGanadero(nombreProducto, cantidad);

                    if (rand.nextDouble() < probabilidad) {
                        System.out.println("¡Producto de calidad obtenido de " + animal.getNombre() + "!");
                    }
                }
            }
        }
    }

    public List<Animal> getAnimales() {
        List<Animal> todos = new ArrayList<>();
        todos.addAll(obtenerAnimalesPorTipo("vaca"));
        todos.addAll(obtenerAnimalesPorTipo("oveja"));
        todos.addAll(obtenerAnimalesPorTipo("gallina"));
        todos.addAll(obtenerAnimalesPorTipo("cerdo"));
        return todos;
    }
}