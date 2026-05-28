package models;

import java.util.ArrayList;
public class Venta {
    private String idVenta;
    private String idCliente;
    private String fecha;
    //dentro de cada venta se guarda el producto y su cantidad, los detalles de la venta
    private ArrayList<DetalleVenta> detalles; //digamos que es el arreglo de productos (junto con su cantidad) dentro de esa venta
    private double total;

    //constructor, este sirve para cargar todo desde el ventas.txt
    public Venta(String idVenta, String idCliente, String fecha) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    //ESTE CONSTRUCTOR NO LO PONGAN, LO PONGO YO
    //constructor para que no explote al realizar ventas en la interfaz grafica.
    public Venta(String idCliente, String fecha) {
        this.idVenta = "V-" + System.currentTimeMillis(); //se le agrega una ID unica
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    //para añadir un producto junto con su detalle a la venta
    public void agregarDetalle(Producto producto, int cantidad) {
        DetalleVenta nuevoDetalle = new DetalleVenta(producto, cantidad);
        detalles.add(nuevoDetalle); //se agrega el detalle al arraylist
        //al total se le suma el subtotal del detalle
        total += nuevoDetalle.getSubtotal();
    }

    //setters y getters
    public String getIdVenta(){
        return idVenta;
    }

    public void setIdVenta(String idVenta){
        this.idVenta = idVenta;
    }

    public String getIdCliente(){
        return idCliente;
    }

    public void setIdCliente(String idCliente){
        this.idCliente = idCliente;
    }

    public String getFecha(){
        return fecha;
    }

    public void setFecha(String fecha){
        this.fecha = fecha;
    }

    public double getTotal(){
        return total;
    }

    //retorna todo el arraylist de detalle de esa venta
    public ArrayList<DetalleVenta> getDetalles(){
        return detalles;
    }

    //imprimir detalles de la venta
    public void mostrarVenta() {
        System.out.println("Venta ID: " + idVenta + " | Fecha: " + fecha);
        for (DetalleVenta d : detalles) {
            System.out.println("- " + d.getProducto().getNombre() + " x" + d.getCantidad() + " = $" + d.getSubtotal());
        }
        System.out.println("Total: $" + total);
    }
}
