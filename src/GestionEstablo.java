import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase gestiona el establo de la granja. Se encarga de alimentar a los animales, producir productos,
 * vender productos, rellenar el comedero, cargar los animales, alimentos y productos desde la base de datos,
 * y registrar las transacciones realizadas.
 */
public class GestionEstablo implements Serializable {
    private static GestionEstablo instancia;
    private Granja granja;  // Referencia a la clase Granja
    private List<Animal> animales;
    private List<Producto> productos;
    private List<Alimento> alimentos;

    /**
     * Constructor privado de la clase GestionEstablo. Inicializa las listas de animales, productos y alimentos,
     * y carga los datos desde la base de datos.
     *
     * @param granja La instancia de la granja a la que pertenece este establo.
     */
    private GestionEstablo(Granja granja) {
        this.granja = granja;
        this.animales = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.alimentos = new ArrayList<>();
        cargarAnimales();
        cargarAlimentos();
        cargarProductos();
    }

    /**
     * Devuelve la instancia única de la clase GestionEstablo, siguiendo el patrón Singleton.
     *
     * @param granja La instancia de la granja a la que pertenece este establo.
     * @return La instancia única de GestionEstablo.
     */
    public static GestionEstablo getInstance(Granja granja) {
        if (instancia == null) {
            instancia = new GestionEstablo(granja);
        }
        return instancia;
    }

    /**
     * Alimenta a todos los animales del establo. Verifica si ya han comido y si hay suficiente alimento
     * disponible en la base de datos. Actualiza el estado de los animales y los alimentos después de alimentarlos.
     */
    public void alimentarAnimales() {
        for (Animal animal : animales) {
            if (animal.isHaComido()) {
                System.out.println(animal.getNombre() + " ya ha comido hoy!");
            } else {
                Alimento alimento = buscarAlimentoPorId(animal.getIdAlimento());

                if (alimento != null) {
                    // Verifica la cantidad disponible del alimento
                    int cantidadNecesaria = animal.alimentar();

                    // Verificar si hay suficiente alimento disponible en la base de datos
                    if (alimento.getCantidadDisponible() >= cantidadNecesaria) {
                        // Actualizar la cantidad disponible del alimento
                        alimento.setCantidadDisponible(alimento.getCantidadDisponible() - cantidadNecesaria);
                        actualizarCantidadAlimento(alimento);
                        animal.setHaComido(true);
                        System.out.println(animal.getNombre() + " ha sido alimentado con " + cantidadNecesaria + " unidades de alimento");
                        registrarConsumo(animal.getId(), cantidadNecesaria);
                    } else {
                        System.out.println("No hay suficiente alimento para " + animal.getNombre());
                    }
                } else {
                    System.out.println("No se encontró alimento con ID: " + animal.getIdAlimento());
                }
            }
        }
    }

    /**
     * Llama al metodo producir de cada animal para que produzcan productos según su tipo y la estación actual.
     * Actualiza la cantidad disponible de los productos y registra la producción en la base de datos.
     */
    public void producir() {
        for (Animal animal : animales) {
            int cantidadProducida = animal.producir(granja.getEstacion(), granja.getDia_juego());
            if (cantidadProducida > 0) {
                Producto producto = buscarProductoPorId(animal.getIdProducto());
                if (producto != null) {
                    producto.setCantidadDisponible(producto.getCantidadDisponible() + cantidadProducida);
                    actualizarCantidadProducto(producto);
                    registrarProduccion(animal.getId(), cantidadProducida);
                }
            }
        }
    }

    /**
     * Vende los productos disponibles en la base de datos, actualiza la cantidad disponible de los productos
     * y registra la transacción en la base de datos.
     */
    public void venderProductos() {
        double totalGanancias = 0.0;

        try (Connection connection = ConexionBD.getInstance().getConnection()) {
            String selectQuery = "SELECT id, cantidad_disponible, precio FROM Productos WHERE cantidad_disponible > 0";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = selectStmt.executeQuery()) {

                while (resultSet.next()) {
                    int idProducto = resultSet.getInt("id");
                    int cantidadDisponible = resultSet.getInt("cantidad_disponible");
                    double precioVenta = resultSet.getDouble("precio");

                    double gananciaProducto = cantidadDisponible * precioVenta;
                    totalGanancias += gananciaProducto;

                    String updateQuery = "UPDATE Productos SET cantidad_disponible = 0 WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, idProducto);
                        updateStmt.executeUpdate();
                    }
                }
            }

            if (granja != null && totalGanancias > 0) {
                granja.setPresupuesto(granja.getPresupuesto() + totalGanancias);
                System.out.println("Venta realizada, Ganancia total: " + totalGanancias);

                // Registrar la transacción de venta solo si se han vendido productos
                String insertQuery = "INSERT INTO Transacciones (tipo_transaccion, tipo_elemento, precio, fecha_transaccion) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, "VENTA");
                    insertStmt.setString(2, "PRODUCTO");
                    insertStmt.setDouble(3, totalGanancias);
                    insertStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    insertStmt.executeUpdate();
                }
            } else {
                System.out.println("No se ha vendido ningún producto.");
            }

        } catch (SQLException e) {
            System.out.println("Error al vender los productos: " + e.getMessage());
        }
    }

    /**
     * Rellena el comedero con los alimentos necesarios según la cantidad disponible en la base de datos.
     * Actualiza la cantidad de alimentos en la base de datos y registra la transacción de compra.
     */
    public void rellenarComedero() {
        double totalGasto = 0.0;

        try (Connection connection = ConexionBD.getInstance().getConnection()) {
            String selectQuery = "SELECT id, nombre, cantidad_disponible, precio FROM Alimentos";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = selectStmt.executeQuery()) {

                while (resultSet.next()) {
                    int idAlimento = resultSet.getInt("id");
                    String nombreAlimento = resultSet.getString("nombre");
                    int cantidadDisponible = resultSet.getInt("cantidad_disponible");
                    double precioCompra = resultSet.getDouble("precio");

                    int cantidadNecesaria = 25 - cantidadDisponible;
                    if (cantidadNecesaria > 0) {
                        double costoAlimento = cantidadNecesaria * precioCompra;
                        if (granja.getPresupuesto() >= costoAlimento) {
                            granja.setPresupuesto(granja.getPresupuesto() - costoAlimento);
                            totalGasto += costoAlimento;

                            // Actualizamos tanto la lista de alimentos como la base de datos
                            for (Alimento alimento : alimentos) {
                                if (alimento.getId() == idAlimento) {
                                    alimento.setCantidadDisponible(25);
                                }
                            }

                            String updateQuery = "UPDATE Alimentos SET cantidad_disponible = 25 WHERE id = ?";
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                updateStmt.setInt(1, idAlimento);
                                updateStmt.executeUpdate();
                            }

                            System.out.println("Se han comprado " + cantidadNecesaria + " unidades de alimento: " + nombreAlimento);
                        } else {
                            System.out.println("No hay suficiente presupuesto para comprar " + cantidadNecesaria + " unidades de alimento ID " + idAlimento);
                        }
                    }
                }
            }
            // Registrar la transacción de compra en la tabla Transacciones
            if (totalGasto > 0) {
                String insertQuery = "INSERT INTO Transacciones (tipo_transaccion, tipo_elemento, precio, fecha_transaccion) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, "COMPRA");
                    insertStmt.setString(2, "ALIMENTO");
                    insertStmt.setDouble(3, totalGasto);
                    insertStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    insertStmt.executeUpdate();
                    System.out.println("Transacción de compra registrada.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al rellenar el comedero: " + e.getMessage());
        }
    }

    /**
     * Busca un alimento en la lista de alimentos por su ID.
     *
     * @param idAlimento El ID del alimento que se busca.
     * @return El alimento correspondiente, o null si no se encuentra.
     */
    private Alimento buscarAlimentoPorId(int idAlimento) {
        for (Alimento alimento : alimentos) {
            if (alimento.getId() == idAlimento) {
                return alimento;
            }
        }
        return null;
    }

    /**
     * Actualiza la cantidad disponible de un alimento en la base de datos.
     *
     * @param alimento El alimento cuya cantidad se actualiza.
     */
    private void actualizarCantidadAlimento(Alimento alimento) {
        try (Connection connection = ConexionBD.getInstance().getConnection()) {
            String updateQuery = "UPDATE Alimentos SET cantidad_disponible = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setInt(1, alimento.getCantidadDisponible());
                statement.setInt(2, alimento.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar la cantidad de alimento: " + e.getMessage());
        }
    }

    /**
     * Busca un producto en la lista de productos por su ID.
     *
     * @param idProducto El ID del producto que se busca.
     * @return El producto correspondiente, o null si no se encuentra.
     */
    private Producto buscarProductoPorId(int idProducto) {
        for (Producto producto : productos) {
            if (producto.getId() == idProducto) {
                return producto;
            }
        }
        return null;
    }

    /**
     * Actualiza la cantidad disponible de un producto en la base de datos.
     *
     * @param producto El producto cuya cantidad se actualiza.
     */
    private void actualizarCantidadProducto(Producto producto) {
        try (Connection connection = ConexionBD.getInstance().getConnection()) {
            String updateQuery = "UPDATE Productos SET cantidad_disponible = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setInt(1, producto.getCantidadDisponible());
                statement.setInt(2, producto.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar la cantidad de productos: " + e.getMessage());
        }
    }

    /**
     * Registra la producción de un animal en la base de datos.
     *
     * @param idAnimal       El ID del animal que produjo los productos.
     * @param cantidadProducida La cantidad producida.
     */
    private void registrarProduccion(int idAnimal, int cantidadProducida) {
        try (Connection connection = ConexionBD.getInstance().getConnection()) {
            String insertQuery = "INSERT INTO HistorialProduccion (id_animal, cantidad, fecha_produccion) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, idAnimal);
                statement.setInt(2, cantidadProducida);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar la producción: " + e.getMessage());
        }
    }
    /**
     * Carga los animales desde la base de datos y los agrega a la lista de animales.
     */
    public void cargarAnimales() {
        String query = "SELECT id, tipo, nombre, dia_insercion, peso, id_alimento, id_producto FROM Animales";
        try (Connection connection = ConexionBD.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String tipo = resultSet.getString("tipo").toLowerCase();
                String nombre = resultSet.getString("nombre");
                int diaInsercion = resultSet.getInt("dia_insercion");
                int idAlimento = resultSet.getInt("id_alimento");
                int idProducto = resultSet.getInt("id_producto");

                Animal animal = null;
                switch (tipo) {
                    case "gallina":
                        animal = new Gallina(id, nombre, diaInsercion, idAlimento, idProducto);
                        break;
                    case "vaca":
                        double peso = resultSet.getDouble("peso");
                        animal = new Vaca(id, nombre, diaInsercion, peso, idAlimento, idProducto);
                        break;
                    case "oveja":
                        animal = new Oveja(id, nombre, diaInsercion, idAlimento, idProducto);
                        break;
                    case "cerdo":
                        animal = new Cerdo(id, nombre, diaInsercion, idAlimento, idProducto);
                        break;
                }
                if (animal != null) {
                    animales.add(animal);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar los animales: " + e.getMessage());
        }
    }

    /**
     * Carga los alimentos desde la base de datos y los agrega a la lista de alimentos.
     */
    private void cargarAlimentos() {
        try (Connection connection = ConexionBD.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Alimentos");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                double precioCompra = resultSet.getDouble("precio");
                int cantidadDisponible = resultSet.getInt("cantidad_disponible");

                Alimento alimento = new Alimento(id, nombre, precioCompra, cantidadDisponible);
                alimentos.add(alimento);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar los alimentos: " + e.getMessage());
        }
    }

    /**
     * Registra el consumo de alimento de un animal en la base de datos.
     *
     * @param idAnimal       El ID del animal que consumió el alimento.
     * @param cantidadConsumida La cantidad de alimento consumida.
     */
    private void registrarConsumo(int idAnimal, int cantidadConsumida) {
        try (Connection connection = ConexionBD.getInstance().getConnection()) {
            String insertQuery = "INSERT INTO HistorialConsumo (id_animal, cantidad_consumida, fecha_consumo) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, idAnimal);
                statement.setInt(2, cantidadConsumida);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar el consumo: " + e.getMessage());
        }
    }

    /**
     * Carga los productos desde la base de datos y los agrega a la lista de productos.
     */
    private void cargarProductos() {
        try (Connection connection = ConexionBD.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Productos");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                double precioVenta = resultSet.getDouble("precio");
                int cantidadDisponible = resultSet.getInt("cantidad_disponible");

                Producto producto = new Producto(id, nombre, precioVenta, cantidadDisponible);
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar los productos: " + e.getMessage());
        }
    }

    /**
     * Muestra en consola los animales, los alimentos y los productos disponibles en el establo.
     * Usamos el printf basicamente para los tabulados para que quede mas bonito
     * Me resultó imposible de hacer sin printf por el tamaño de los diferentes Strings.
     * %-4d: Imprime un número entero alineado a la izquierda en un campo de 4 caracteres.
     * %-10s: Imprime una cadena de texto alineada a la izquierda en un campo de 10 caracteres.
     * %-12s: Imprime una cadena de texto alineada a la izquierda en un campo de 12 caracteres.
     * .2f: Imprime solo 2 decimales.
     */
    public void mostrarAnimales() {
        // Mostrar cabecera
        System.out.println("ID   Tipo       Alimentado    Alimento   Producto ");
        System.out.println("--------------------------------------------------------------");

        // Mostrar animales
        for (Animal animal : animales) {
            String alimentado;
            if (animal.isHaComido()) {
                alimentado = "Sí";
            } else {
                alimentado = "No";
            }

            String alimento = getAlimentoNombreById(animal.getIdAlimento());
            String producto = getProductoNombreById(animal.getIdProducto());

            //Mostrar cada animal
            System.out.printf("%-4d %-10s %-12s %-10s %-10s\n",
                    animal.getId(),
                    animal.getClass().getSimpleName(),
                    alimentado,
                    alimento,
                    producto);
        }

        //Mostrar alimentos
        System.out.println("\nAlimentos Disponibles");
        System.out.printf("%-10s %-20s %-20s\n", "Alimento", "Cantidad Disponible", "Precio");
        System.out.println("----------------------------------------");
        for (Alimento alimento : alimentos) {
            System.out.printf("%-10s %-20d %-20.2f\n", alimento.getNombre(), alimento.getCantidadDisponible(), alimento.getPrecioCompra());
        }

        //Mostrar productos
        System.out.println("\nProductos Disponibles");
        System.out.printf("%-10s %-20s %-20s\n", "Producto", "Cantidad Disponible", "Precio");
        System.out.println("----------------------------------------");
        for (Producto producto : productos) {
            System.out.printf("%-10s %-20d %-20.2f\n", producto.getNombre(), producto.getCantidadDisponible(), producto.getPrecioVenta());
        }
    }

    private String getAlimentoNombreById(int idAlimento) {
        for (Alimento alimento : alimentos) {
            if (alimento.getId() == idAlimento) {
                return alimento.getNombre();
            }
        }
        return "Desconocido";
    }

    private String getProductoNombreById(int idProducto) {
        for (Producto producto : productos) {
            if (producto.getId() == idProducto) {
                return producto.getNombre();
            }
        }
        return "Desconocido";
    }

    public List<Animal> getAnimales() {
        return animales;
    }

    public void setGranja(Granja granja) {
        this.granja = granja;
    }
}
