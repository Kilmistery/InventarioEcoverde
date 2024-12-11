package main;

import Inventario_pck.Inventario;
import Producto_pck.Producto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/inventario_db";
        String usuario = "root";
        String contrasena = "23Ljjh12-.";//Kil
        //String contrasena = "1234"; //Kel

        try (Connection conexion = DriverManager.getConnection(url, usuario, contrasena)){
            Inventario inventario = new Inventario(conexion);
            /*
            Producto producto1 = new Producto("Cobre", "A126", 20000.0, 3);
            inventario.agregarProductos(producto1);
            inventario.mostrarInvetario();
            producto1 = new Producto(3,"Herbicida", "A043",20800.0, 4);
            inventario.actualizarProducto(producto1);
            */
            if (inventario.eliminarProducto(1) == true){
                System.out.println("Todo bien");
            } else {
                System.out.println("Oh no");
            }

        } catch (SQLException e){
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }

    }

}