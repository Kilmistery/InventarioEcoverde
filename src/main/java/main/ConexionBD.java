package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/inventario_db";  // Cambia el nombre de la base de datos si es necesario
    private static final String USER = "root";  // Cambia el usuario si es necesario
    private static final String PASSWORD = "23Ljjh12-.";  // Cambia la contraseña si es necesario

    // Método para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}