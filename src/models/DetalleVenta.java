package models;

//guarda el producto y la cantidad que se agrego a la venta
public class DetalleVenta {
    private Producto producto;
    private int cantidad;

    //constructor
    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    //setters y getters
    public Producto getProducto(){
        return producto;
    }

    public void setProducto(Producto producto){
        this.producto = producto;
    }

    public int getCantidad(){
        return cantidad;
    }

    public void setCantidad(int cantidad){
        this.cantidad = cantidad;
    }


    //calcula el subtotal de el detalle
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}
