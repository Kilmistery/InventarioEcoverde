package modelo;

public class HistorialCambio {
    private String fecha;
    private int cantidadAnterior;
    private int cantidadNueva;

    public HistorialCambio(String fecha, int cantidadAnterior, int cantidadNueva) {
        this.fecha = fecha;
        this.cantidadAnterior = cantidadAnterior;
        this.cantidadNueva = cantidadNueva;
    }

    // Getters y setters
    public String getFecha() {
        return fecha;
    }

    public int getCantidadAnterior() {
        return cantidadAnterior;
    }

    public int getCantidadNueva() {
        return cantidadNueva;
    }
}
