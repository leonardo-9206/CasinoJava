package models;

// Esta clase representa un "renglón" en el ticket de venta.
// Reemplaza a los "arreglos paralelos" y es la forma más profesional 
// pero totalmente escolar de hacerlo.
public class DetalleVenta {
    
    private Producto producto;
    private int cantidad;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // El detalle mismo sabe calcular su subtotal
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}
