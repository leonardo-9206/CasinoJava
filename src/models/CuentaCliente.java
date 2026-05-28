package models;
import java.util.ArrayList;

public class CuentaCliente {
    private String idCuenta;
    private String nombreCliente;
    private ArrayList<Venta> historialVentas; //se guardan las ventas para el cliente

    //constructor
    public CuentaCliente(String idCuenta, String nombreCliente) {
        this.idCuenta = idCuenta;
        this.nombreCliente = nombreCliente;
        this.historialVentas = new ArrayList<>();
    }

    //agregar una venta al historial
    public void agregarVenta(Venta nuevaVenta) {
        historialVentas.add(nuevaVenta);
    }

    public double calcularTotalGastado() {
        double totalGlobal = 0;
        for (Venta v : historialVentas) {
            totalGlobal += v.getTotal();
        }
        return totalGlobal;
    }

    //setters y getters
    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta){
        this.idCuenta = idCuenta;
    }

    public String getNombreCliente(){
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente){
        this.nombreCliente = nombreCliente;
    }

    public ArrayList<Venta> getHistorialVentas(){
        return historialVentas;
    }
}
