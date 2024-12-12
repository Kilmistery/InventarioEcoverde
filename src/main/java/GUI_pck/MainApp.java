package GUI_pck;

import Producto_pck.Producto;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.ConexionBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainApp extends  Application {

    public void start(Stage primaryStage){
        // Crear el TableView
        TableView<Producto> tableView = new TableView<>();

        // Definir las columnas
        TableColumn<Producto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Agregar las columnas al TableView
        tableView.getColumns().addAll(colId, colNombre, colCodigo, colPrecio, colCantidad);

        // Cargar los datos desde la base de datos
        tableView.setItems(obtenerProductos());

        // Crear el contenedor principal
        VBox root = new VBox(tableView);
        root.setStyle("-fx-padding: 20;");

        // Configurar y mostrar la escena
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventario de Productos");
        primaryStage.show();
    }

    private ObservableList<Producto> obtenerProductos() {
        ObservableList<Producto> productos = FXCollections.observableArrayList();

        try (Connection conexion = ConexionBD.getConnection();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM productos")) {

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                ));
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

        return productos;
    }

    public static void main(String[] args) {
        launch(); //Inicia la aplicación
    }
}
