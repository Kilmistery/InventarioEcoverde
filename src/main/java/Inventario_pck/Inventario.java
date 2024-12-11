package Inventario_pck;


import Producto_pck.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Inventario {

private Connection conexion;

public Inventario(Connection conexion){
    this.conexion = conexion;
}
public void agregarProductos(Producto producto) throws SQLException{
    String sql = "INSERT INTO productos (nombre, codigo, precio, cantidad) VALUES (?,?,?,?)";
    try (PreparedStatement stmt = conexion.prepareStatement(sql)){
        stmt.setString(1, producto.getNombre());
        stmt.setString(2, producto.getCodigo());
        stmt.setDouble(3, producto.getPrecio());
        stmt.setInt(4, producto.getCantidad());
        stmt.executeUpdate();
        System.out.println("Producto agregado exitosamente.");

    } catch (SQLException e){
        System.out.println("Error al agregar el producto: " + e.getMessage());
    }
}

public void mostrarInvetario(){
    String sql = "SELECT * FROM productos";
    try (Statement stmt = conexion.createStatement();
         ResultSet rs = stmt.executeQuery(sql))
    {
        List<Producto> listaProductos = new ArrayList<>();
        while (rs.next()){
            Producto producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad")

            );
            listaProductos.add(producto);
        }
        listaProductos.forEach(System.out::println);
    } catch (SQLException e){
        System.out.println("Error al mostrar inventario: " + e.getMessage());
    }
}

public boolean actualizarProducto(Producto producto){
    String sql = "UPDATE productos SET nombre = ?, codigo = ?, precio = ?, cantidad = ? WHERE id =?";

    try(PreparedStatement stmt = conexion.prepareStatement((sql))){
    stmt.setString(1, producto.getNombre());
    stmt.setString(2, producto.getCodigo());
    stmt.setDouble(3, producto.getPrecio());
    stmt.setInt(4, producto.getCantidad());
    stmt.setInt(5, producto.getId());

    int filasActualizadas = stmt.executeUpdate();
        System.out.println("Producto actulizado exitosamente");
    return filasActualizadas > 0;

    } catch (SQLException e){
        System.out.println("Error al actualziar el producto por esto: " + e.getMessage());
        return false;
    }
}

}
