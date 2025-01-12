package Inventario_pck;


import Producto_pck.Producto;
import modelo.HistorialCambio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private Connection conexion;

    public Inventario(Connection conexion) {
        this.conexion = conexion;
    }

    public Producto obtenerProductoPorCodigo(String codigo) {
        String query = "SELECT * FROM productos WHERE codigo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Crear y devolver el producto si se encuentra
                return new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el producto: " + e.getMessage());
        }
        return null; // Retorna null si no se encuentra el producto
    }
    public void agregarProductos(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, codigo, precio, cantidad) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getCodigo());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());
            stmt.executeUpdate();
            System.out.println("Producto agregado exitosamente.");

        } catch (SQLException e) {
            System.out.println("Error al agregar el producto: " + e.getMessage());
        }
    }

    public void mostrarInventario() {
        String sql = "SELECT * FROM productos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Producto> listaProductos = new ArrayList<>();
            while (rs.next()) {
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
        } catch (SQLException e) {
            System.out.println("Error al mostrar inventario: " + e.getMessage());
        }
    }

    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, codigo = ?, precio = ?, cantidad = ? WHERE id =?";

        try (PreparedStatement stmt = conexion.prepareStatement((sql))) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getCodigo());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());
            stmt.setInt(5, producto.getId());

            int filasActualizadas = stmt.executeUpdate();
            System.out.println("Producto actulizado exitosamente");
            return filasActualizadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualziar el producto por esto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> obtenerProductos() {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT id, nombre, codigo, precio, cantidad FROM productos";

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                );
                listaProductos.add(producto);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener productos: " + e.getMessage());
        }

        return listaProductos;
    }

    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar producto, razón: " + e.getMessage());
            return false;
        }
    }

    public boolean restarCantidad(String codigo, int cantidadARestar) {
        /*String sql = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, cantidadARestar);
            stmt.setString(2, codigo);
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al restar cantidad: " + e.getMessage());
            return false;
        }*/


            Producto producto = obtenerProductoPorCodigo(codigo);
            if (producto != null) {
                int cantidadAnterior = producto.getCantidad();
                int nuevaCantidad = cantidadAnterior - cantidadARestar;

                // Actualiza la cantidad del producto
                String queryActualizar = "UPDATE Productos SET cantidad = ? WHERE codigo = ?";
                try (PreparedStatement ps = conexion.prepareStatement(queryActualizar)) {
                    ps.setInt(1, nuevaCantidad);
                    ps.setString(2, codigo);
                    ps.executeUpdate();
                } catch (SQLException e){
                    e.printStackTrace();
                }

                // Registra el cambio en el historial
                String queryHistorial = "INSERT INTO HistorialCambios (producto_id, cantidad_anterior, cantidad_nueva) VALUES (?, ?, ?)";
                try (PreparedStatement psHistorial = conexion.prepareStatement(queryHistorial)) {
                    psHistorial.setInt(1, producto.getId());
                    psHistorial.setInt(2, cantidadAnterior);
                    psHistorial.setInt(3, nuevaCantidad);
                    psHistorial.executeUpdate();
                } catch (SQLException e){
                    e.printStackTrace();
                }

                return true;
            }

        return false;
    }


    public boolean eliminarProductoCod(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar producto, razón: " + e.getMessage());
            return false;
        }
    }


    public List<HistorialCambio> obtenerHistorial(int productoId) {
        List<HistorialCambio> historial = new ArrayList<>();
        String query = "SELECT fecha, cantidad_anterior, cantidad_nueva FROM HistorialCambios WHERE producto_id = ? ORDER BY fecha DESC";
        try (
                PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, productoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                historial.add(new HistorialCambio(
                        rs.getString("fecha"),
                        rs.getInt("cantidad_anterior"),
                        rs.getInt("cantidad_nueva")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historial;
    }


}
