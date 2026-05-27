package models;

import java.util.ArrayList;

public class CuentaCliente {
    
    private String idCuenta;
    private String nombreCliente;
    
    // En lugar del viejo arreglo de punteros ventas[100], usamos un ArrayList
    private ArrayList<Venta> historialVentas;

    public CuentaCliente(String idCuenta, String nombreCliente) {
        this.idCuenta = idCuenta;
        this.nombreCliente = nombreCliente;
        this.historialVentas = new ArrayList<>();
    }

    // Agregar una venta al historial del cliente
    public void agregarVenta(Venta nuevaVenta) {
        historialVentas.add(nuevaVenta);
    }

    // Calcular el total histórico gastado por este cliente
    public double calcularTotalGastado() {
        double totalGlobal = 0;
        for (Venta v : historialVentas) {
            totalGlobal += v.getTotal();
        }
        return totalGlobal;
    }

    // Getters y Setters
    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public ArrayList<Venta> getHistorialVentas() {
        return historialVentas;
    }
}
