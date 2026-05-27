package models;

import java.util.ArrayList;

public class Venta {
    
    private String idVenta;
    private String idCliente;
    private String fecha;
    
    // Lista de renglones del ticket (cada uno trae su producto y cantidad)
    private ArrayList<DetalleVenta> detalles;
    
    private double total;

    public Venta(String idVenta, String idCliente, String fecha) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    // Constructor simplificado (autogenera un ID interno para no romper los archivos .txt)
    public Venta(String idCliente, String fecha) {
        this.idVenta = "V-" + System.currentTimeMillis();
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    // Método para agregar un renglón al ticket
    public void agregarDetalle(Producto producto, int cantidad) {
        DetalleVenta nuevoDetalle = new DetalleVenta(producto, cantidad);
        detalles.add(nuevoDetalle);
        
        // Sumamos al total el subtotal de este detalle
        total += nuevoDetalle.getSubtotal();
    }

    // Getters y Setters
    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void mostrarVenta() {
        System.out.println("Venta ID: " + idVenta + " | Fecha: " + fecha);
        for (DetalleVenta d : detalles) {
            System.out.println("- " + d.getProducto().getNombre() + " x" + d.getCantidad() + " = $" + d.getSubtotal());
        }
        System.out.println("Total: $" + total);
    }
}
