package GUI_pck;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends  Application {

    public void start(Stage primaryStage){
        // Crear el contenedor principal
        VBox root = new VBox(10); // Espaciado entre los elementos
        root.setStyle("-fx-padding: 20; -fx-alignment: center;"); // Estilo básico

        // Crear una etiqueta
        Label label = new Label("Prueba 1: La gráfica de aplicación");

        // Crear un botón
        Button btn = new Button("Hacer clic");
        btn.setOnAction(event -> {
            // Acción al presionar el botón
            label.setText("¡Botón presionado!");
        });

        // Agregar elementos al contenedor
        root.getChildren().addAll(label, btn);

        // Crear la escena
        Scene scene = new Scene(root, 400, 300);
        System.out.println("Funciona la grafica");
        // Configurar y mostrar la ventana
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inventario de Productos");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(); //Inicia la aplicación
    }
}
