package GUI_pck;

import Inventario_pck.Inventario;
import Producto_pck.Producto;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.ConexionBD;
import modelo.HistorialCambio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class MainApp extends Application {

    private Inventario inventario;

    public MainApp() throws SQLException {
        // Aquí estamos inicializando la conexión a la base de datos y el inventario
        Connection conexion = ConexionBD.getConnection(); // Obtiene la conexión a la base de datos
        inventario = new Inventario(conexion); // Inicializa el inventario con la conexión
    }

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Crear la tabla de productos
        TableView<Producto> tablaProductos = crearTablaProductos();

        // Campo de búsqueda y botón
        HBox barraBusqueda = new HBox(10);
        barraBusqueda.setPadding(new Insets(10));
        TextField txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Buscar por nombre o código...");
        Button btnBuscar = new Button("Buscar");
        Button btnReset = new Button("Reset");

        btnBuscar.setOnAction(e -> {
            String criterio = txtBusqueda.getText().toLowerCase();
            if (!criterio.isEmpty()) {
                ObservableList<Producto> productosFiltrados = FXCollections.observableArrayList();
                for (Producto producto : inventario.obtenerProductos()) {
                    if (producto.getNombre().toLowerCase().contains(criterio) ||
                            producto.getCodigo().toLowerCase().contains(criterio)) {
                        productosFiltrados.add(producto);
                    }
                }
                tablaProductos.setItems(productosFiltrados);
            } else {
                mostrarAlerta("Error", "Por favor ingrese un criterio de búsqueda.");
            }
        });

        btnReset.setOnAction(e -> {
            txtBusqueda.clear();
            actualizarTabla(tablaProductos);
        });

        barraBusqueda.getChildren().addAll(txtBusqueda, btnBuscar, btnReset);
        root.setTop(barraBusqueda);

        // Agregar la tabla al centro
        root.setCenter(tablaProductos);

        // Crear el formulario para agregar productos
        VBox formularioAgregar = crearFormulario(tablaProductos);
        root.setRight(formularioAgregar);

        // Crear la escena
        Scene scene = new Scene(root, 1000, 700);
        System.out.println(getClass().getResource("/styles.css"));
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventario de Productos");
        primaryStage.show();

        // Cargar los datos iniciales en la tabla
        tablaProductos.getItems().setAll(inventario.obtenerProductos());

    }

    private TableView<Producto> crearTablaProductos() {
        TableView<Producto> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Producto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNombre()));

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Codigo");
        colCodigo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCodigo()));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecio()));

        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCantidad()));

        TableColumn<Producto, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnModificar.setOnAction(e->{
                    Producto producto = getTableView().getItems().get(getIndex());
                    mostrarVentanaModificar(producto,getTableView());

                });

                btnEliminar.setOnAction(e->{
                    Producto producto = getTableView().getItems().get(getIndex());
                    boolean exito = inventario.eliminarProductoCod(producto.getCodigo());
                    if (exito) {
                        actualizarTabla(getTableView());
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el producto.");
                    }
                });
            }

            protected void  updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10, btnModificar, btnEliminar);
                    setGraphic(hBox);
                }
            }


        });

        TableColumn<Producto, Void> colRestar = new TableColumn<>("Restar");
        colRestar.setCellFactory(col -> new TableCell<>() {
            private final TextField txtRestarCantidad = new TextField();
            private final Button btnRestar = new Button("Restar");

            {
                btnRestar.setOnAction(e -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    String cantidadTexto = txtRestarCantidad.getText();

                    // Verifica si el campo está vacío
                    if (cantidadTexto.isEmpty()) {
                        mostrarAlerta("Error", "Debe ingresar una cantidad para restar.");
                        return;
                    }

                    try {
                        int cantidadARestar = Integer.parseInt(cantidadTexto);
                        if (cantidadARestar > 0 && cantidadARestar <= producto.getCantidad()) {
                            boolean exito = inventario.restarCantidad(producto.getCodigo(), cantidadARestar);
                            if (exito) {
                                actualizarTabla(getTableView());
                                txtRestarCantidad.clear();
                            } else {
                                mostrarAlerta("Error", "No se pudo actualizar la cantidad.");
                            }
                        } else {
                            mostrarAlerta("Error", "Cantidad inválida.");
                        }
                    } catch (NumberFormatException ex) {
                        mostrarAlerta("Error", "Ingrese un número válido.");
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, txtRestarCantidad, btnRestar);
                    setGraphic(hbox);
                }
            }
                });

        TableColumn<Producto, Void> colHistorial = new TableColumn<>("Historial");
        colHistorial.setCellFactory(col -> new TableCell<>() {
            private final Button btnHistorial = new Button("Historial");

            {
                btnHistorial.setOnAction(e -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    mostrarVentanaHistorial(producto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnHistorial);
                }
            }
        });


        tabla.getColumns().
                addAll(colId, colNombre, colCodigo, colPrecio, colCantidad, colAcciones, colRestar,colHistorial);
        return tabla;

        }


    private VBox crearFormulario(TableView<Producto> tablaProductos) {
        VBox formulario = new VBox(10);
        formulario.setPadding(new Insets(10));

        Label lblNombre = new Label("Nombre");
        TextField txtNombre = new TextField();

        Label lblCodigo = new Label("Codigo");
        TextField txtCodigo = new TextField();

        Label lblPrecio = new Label("Precio");
        TextField txtPrecio = new TextField();

        Label lblCantidad = new Label("Cantidad");
        TextField txtCantidad = new TextField();

        Button btnAgregar = new Button("Agregar Producto");
        btnAgregar.setOnAction(e -> {
            String nombre = txtNombre.getText();
            String codigo = txtCodigo.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());

            Producto nuevoProducto = new Producto(0, nombre, codigo, precio, cantidad);

            try {
                inventario.agregarProductos(nuevoProducto);
                tablaProductos.getItems().setAll(inventario.obtenerProductos());

                txtNombre.clear();
                txtCodigo.clear();
                txtPrecio.clear();
                txtCantidad.clear();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        formulario.getChildren().addAll(lblNombre, txtNombre, lblCodigo, txtCodigo, lblPrecio, txtPrecio, lblCantidad, txtCantidad, btnAgregar);
        return formulario;
    }

    private void mostrarVentanaModificar(Producto producto, TableView<Producto> tablaProductos) {
        Stage ventanaModificar = new Stage();
        ventanaModificar.setTitle("Modificar Producto");

        TextField txtNombre = new TextField(producto.getNombre());
        TextField txtCodigo = new TextField(producto.getCodigo());
        TextField txtPrecio = new TextField(String.valueOf(producto.getPrecio()));
        TextField txtCantidad = new TextField(String.valueOf(producto.getCantidad()));

        Button btnGuardar = new Button("Guardar");

        btnGuardar.setOnAction(e -> {
            try {
                producto.setNombre(txtNombre.getText());
                producto.setCodigo(txtCodigo.getText());
                producto.setPrecio(Double.parseDouble(txtPrecio.getText()));
                producto.setCantidad(Integer.parseInt(txtCantidad.getText()));

                // Actualiza el producto en el inventario y en la tabla
                boolean actualizado = inventario.actualizarProducto(producto);
                if (actualizado) {
                    // Refresca los datos de la tabla
                    tablaProductos.getItems().setAll(inventario.obtenerProductos());
                    ventanaModificar.close();
                } else {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el producto.");
                    alerta.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Por favor, ingrese valores válidos.");
                alerta.showAndWait();
            }
        });

        VBox vBox = new VBox(10,
                new Label("Nombre"), txtNombre,
                new Label("Código"), txtCodigo,
                new Label("Precio"), txtPrecio,
                new Label("Cantidad"), txtCantidad,
                btnGuardar
        );
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox, 300, 300);
        ventanaModificar.setScene(scene);
        ventanaModificar.show();
    }
    // Método para obtener los datos desde la base de datos
    private ObservableList<Producto> obtenerDatosDeBaseDeDatos() {
        ObservableList<Producto> productos = FXCollections.observableArrayList();
        // Aquí llamaremos al método de Inventario para obtener los productos
        try {
            Connection conexion = ConexionBD.getConnection();
            Inventario inventario = new Inventario(conexion); // Asegúrate de que esta clase ya está configurada
            productos.addAll(inventario.obtenerProductos());
        } catch (Exception e) {
            System.out.println("Error al obtener los datos: " + e.getMessage());
        }
        return productos;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void actualizarTabla(TableView<Producto> tablaProductos) {
        tablaProductos.getItems().setAll(inventario.obtenerProductos());
    }

    public static void main(String[] args) {
        launch(); //Inicia la aplicación
    }

    private void mostrarVentanaHistorial(Producto producto) {
        Stage ventanaHistorial = new Stage();
        ventanaHistorial.setTitle("Historial de Cambios - " + producto.getNombre());

        TableView<HistorialCambio> tablaHistorial = new TableView<>();
        tablaHistorial.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HistorialCambio, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<HistorialCambio, Integer> colCantidadAnterior = new TableColumn<>("Cantidad Anterior");
        colCantidadAnterior.setCellValueFactory(new PropertyValueFactory<>("cantidadAnterior"));

        TableColumn<HistorialCambio, Integer> colCantidadNueva = new TableColumn<>("Cantidad Nueva");
        colCantidadNueva.setCellValueFactory(new PropertyValueFactory<>("cantidadNueva"));

        tablaHistorial.getColumns().addAll(colFecha, colCantidadAnterior, colCantidadNueva);

        // Obtener el historial del producto desde la base de datos
        tablaHistorial.setItems(FXCollections.observableArrayList(inventario.obtenerHistorial(producto.getId())));

        VBox vbox = new VBox(tablaHistorial);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 500, 400);
        ventanaHistorial.setScene(scene);
        ventanaHistorial.show();
    }
}
