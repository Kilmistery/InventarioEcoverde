package Producto_pck;

public class Producto {
    private String nombre;
    private String codigo;
    private double precio;
    private int id;
    private int cantidad;
    // Constructor
    public Producto(int id, String nombre, String codigo, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Producto(String nombre, String codigo, double precio, int cantidad) {

        this.nombre = nombre;
        this.codigo = codigo;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    // Método toString para mostrar la información del producto
    @Override
    public String toString() {
        return "Producto [nombre=" + nombre + ", codigo=" + codigo + ", precio=" + precio + ", id=" + id + ", cantidad=" + cantidad + "]";
    }
}
