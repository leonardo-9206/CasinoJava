package managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import models.CuentaCliente;
import models.DetalleVenta;
import models.Producto;
import models.Venta;

public class VentasManager {
    //guarda todas las cuentas de los clientes
    private ArrayList<CuentaCliente> listaCuentas;
    //guardamos por separado clientes, las ventas, y el detalle de las ventas
    private final String ARCHIVO_CLIENTES = "clientes.txt";
    private final String ARCHIVO_VENTAS = "ventas.txt";
    private final String ARCHIVO_DETALLES = "detalles_ventas.txt";
    //para que pueda acceder al inventario y sus metodos
    private InventarioManager inventarioRef;

    //constructor
    public VentasManager(InventarioManager inventario) {
        this.listaCuentas = new ArrayList<>();
        this.inventarioRef = inventario;
        cargarDatos(); //carga datos desde los 3 archivos, clientes, ventas y sus detalles 
    }

    //retorna la lista de todas las cuentas
    public ArrayList<CuentaCliente> getListaCuentas() {
        return listaCuentas;
    }

    public void agregarCliente(CuentaCliente nuevoCliente) {
        listaCuentas.add(nuevoCliente);
        guardarDatos(); //se guardan los cambios
    }

    //retorna una cuenta
    public CuentaCliente buscarCliente(String idCuenta) {
        for (CuentaCliente c : listaCuentas) {
            if (c.getIdCuenta().equals(idCuenta)) {
                return c;
            }
        }
        return null;
    }

    public boolean procesarVenta(Venta nuevaVenta) {
        CuentaCliente cliente = buscarCliente(nuevaVenta.getIdCliente());
        if (cliente == null) {
            System.out.println("Error: El cliente no existe.");
            return false;
        }
        //primero verifica con ayuda del manager de inventario que si exista el stock a vender 
        for (DetalleVenta detalle : nuevaVenta.getDetalles()) {
            Producto pInv = inventarioRef.buscarProducto(detalle.getProducto().getId());
            if (pInv == null || pInv.getCantidad() < detalle.getCantidad()) {
                System.out.println("Error: No hay suficiente stock de " + detalle.getProducto().getNombre());
                return false; 
            }
        }

        //si si existe, entonces reduce el stock que se esta vendiendo
        for (DetalleVenta detalle : nuevaVenta.getDetalles()) {
            inventarioRef.reducirStock(detalle.getProducto().getId(), detalle.getCantidad());
        }

        //se guarda en el arraylist de ventas del cliente
        cliente.agregarVenta(nuevaVenta);
        guardarDatos(); //se actualizan los archivos
        System.out.println("Venta procesada con éxito Total: $" + nuevaVenta.getTotal());
        return true;
    }

    //ESTE METODO NO LO PONGAN, LA PONGO YO
    //devuelve todas las cuentas formateadas para el textarea
    public String obtenerCuentasText() {
        if (listaCuentas.isEmpty()) {
            return "Aún no hay clientes registrados en el sistema."; //imprime eso en el textarea
        } 
        StringBuilder sb = new StringBuilder();
        //formato para que se imprima aca chido en el textarea
        sb.append(String.format("%-15s %-30s\n", "ID CLIENTE", "NOMBRE DEL CLIENTE"));
        sb.append("--------------------------------------------------\n");
        for (CuentaCliente c : listaCuentas) {
            sb.append(String.format("%-15s %-30s\n", c.getIdCuenta(), c.getNombreCliente()));
        }
        return sb.toString();
    }

    //ESTA TAMPOCO LA PONGAN, LA PONGO YO
    //devuelve todas las ventas de todos los clientes para el textarea
    public String obtenerTodasLasVentasTexto() {
        StringBuilder sb = new StringBuilder();
        boolean hayVentas = false;
        //recorre las cuentas
        for (CuentaCliente c : listaCuentas) {
            //dentro de cada cuenta recorre sus ventas
            for (Venta v : c.getHistorialVentas()) {
                hayVentas = true;
                //agregamos los datos de la venta
                sb.append("Fecha: ").append(v.getFecha())
                  .append(" | Cliente: ").append(c.getNombreCliente())
                  .append(" (ID: ").append(c.getIdCuenta()).append(")\n");
                  //nos vamos a los detalles de la venta, o los productos que se compraron en ella
                for (DetalleVenta d : v.getDetalles()) {
                    //formato para los productos
                    sb.append(String.format("   - %-20s x%-5d  $%.2f\n",
                        d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal()));
                }
                //se pone el total de la venta
                sb.append(String.format("   TOTAL VENTA: $%.2f\n", v.getTotal()));
                sb.append("----------------------------------------------\n");
            }
        }
        if (!hayVentas){
            return "No se han registrado ventas en el sistema.";
        }
        return sb.toString();
    }

    //ESTA TAMPOCO LA PONGAN, LA PONGO YO
    //hace el corte de caja
    public String generarCorteCaja(String fechaOperativa) {
        double totalDia = 0.0;
        int ventasTotales = 0;
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== CORTE DE CAJA ===\n");
        reporte.append("Fecha de Operación: ").append(fechaOperativa).append("\n");
        reporte.append("------------------------------------------------------------------\n");
        //recorre todas las cuentas
        for (CuentaCliente c : listaCuentas) {
            //y adentro el arraylist de ventas
            for (Venta v : c.getHistorialVentas()) {
                //si la fecha de la venta coincide con la fecha actual
                if (v.getFecha().equals(fechaOperativa)) {
                    //pone los datos de la venta
                    reporte.append(String.format("Venta ID: %-10s | Cliente: %-15s | Total: $%.2f\n", 
                            v.getIdVenta(), c.getNombreCliente(), v.getTotal()));
                            //suma el total de dinero y de ventas
                    totalDia += v.getTotal();
                    ventasTotales++;
                }
            }
        }
        reporte.append("------------------------------------------------------------------\n");
        reporte.append("Ventas Totales Realizadas: ").append(ventasTotales).append("\n");
        reporte.append("INGRESOS TOTALES DEL DÍA: $").append(totalDia).append("\n");
        
        try {
            String nombreArchivo = "Corte_Dia_" + fechaOperativa.replace("/", "-") + ".txt";
            PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo));
            pw.print(reporte.toString());
            pw.close();
            //crea un archivo con la informacion de las ventas de ese dia
            return "Corte generado con éxito en el archivo: " + nombreArchivo + "\n\nRecaudación Total: $" + totalDia;
        } catch (Exception e) {
            return "Error al generar archivo de corte de caja.";
        }
    }

    private void guardarDatos() {
        try {
            PrintWriter escClientes = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES));
            PrintWriter escVentas = new PrintWriter(new FileWriter(ARCHIVO_VENTAS));
            PrintWriter escDetalles = new PrintWriter(new FileWriter(ARCHIVO_DETALLES));
            for (CuentaCliente cliente : listaCuentas) {
                //guarda el ID y el nombre del cliente
                escClientes.println(cliente.getIdCuenta() + "," + cliente.getNombreCliente());
                for (Venta venta : cliente.getHistorialVentas()) {
                    //dentro de la cuenta, recorremos sus ventas y guardamos el ID de cada una asi como el ID de la cuenta, para q no c pierda
                    escVentas.println(venta.getIdVenta() + "," + cliente.getIdCuenta() + "," + venta.getFecha());
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        //en los detalles guardamos el ID de la venta, asi como el detalle de los productos
                        escDetalles.println(venta.getIdVenta() + "," + detalle.getProducto().getId() + "," + detalle.getCantidad());
                    }
                }
            }//todos quedaron conectados, tienen un ID que los une para cuando carguemos los datos
            escClientes.close();
            escVentas.close();
            escDetalles.close();
        } catch (Exception e) {
            System.out.println("Error al guardar clientes y ventas: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            BufferedReader lecClientes = new BufferedReader(new FileReader(ARCHIVO_CLIENTES));
            String linea;
            while ((linea = lecClientes.readLine()) != null) {
                //como lee del .txt, hay que separarlo por la ",", primero ID y luego esta el nombre
                String[] datos = linea.split(",");
                //metemos el ID y el nombre del cliente al arraylist para ir reconstruyendo las cuentas
                listaCuentas.add(new CuentaCliente(datos[0], datos[1]));
            }
            lecClientes.close();
            //ahora les cargamos sus ventas vacias
            BufferedReader lecVentas = new BufferedReader(new FileReader(ARCHIVO_VENTAS));
            //lee el archivo de ventas, en donde se guardo con todo y sus ids
            while ((linea = lecVentas.readLine()) != null) {
                String[] datos = linea.split(",");
                String idVenta = datos[0];
                String idCuenta = datos[1];
                String fecha = datos[2];
                //llama a buscar la cuenta que coincida con la ID que tenia ahi guardada
                CuentaCliente c = buscarCliente(idCuenta);
                if (c != null) {
                    //si lo encuentra, le agrega la venta a esa cuenta
                    c.agregarVenta(new Venta(idVenta, idCuenta, fecha));
                }
            }
            lecVentas.close();
            //ya que estan cargadas las cuentas con sus ventas vacias, cargamos los detalles de las ventas
            BufferedReader lecDetalles = new BufferedReader(new FileReader(ARCHIVO_DETALLES));
            while ((linea = lecDetalles.readLine()) != null) {
                String[] datos = linea.split(",");
                String idVenta = datos[0];
                String idProducto = datos[1];
                int cantidad = Integer.parseInt(datos[2]);
                //busca que exista el producto
                Producto p = inventarioRef.buscarProducto(idProducto);
                if (p != null) {
                    /*si existe, nos retorna un producto, y llama a buscar la venta dentro de todo el historial de todas las cuentas, y
                    nos va a retornar la venta que tenga ese mismo ID*/
                    Venta v = buscarVentaGlobal(idVenta);
                    if (v != null) {
                        //teniendo esa venta, le agregamos su detalle
                        v.agregarDetalle(p, cantidad); // Esto también suma al total automáticamente
                    }
                }
            }
            lecDetalles.close();
        } catch (Exception e) {
            System.out.println("No se encontraron archivos de ventas previos. Empezando en limpio.");
        }
    }

    //busca una venta global para poder reconstruirla al cargar los datos
    private Venta buscarVentaGlobal(String idVenta) {
        for (CuentaCliente c : listaCuentas) {
            for (Venta v : c.getHistorialVentas()) {
                if (v.getIdVenta().equals(idVenta)) {
                    return v;
                }
            }
        }
        return null;
    }
}
