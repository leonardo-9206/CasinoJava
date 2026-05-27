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

    private ArrayList<CuentaCliente> listaCuentas;
    
    // Para no enredarnos con formatos complejos en un solo archivo, 
    // usaremos el estilo de "Base de Datos Relacional" usando 3 archivos simples.
    private final String ARCHIVO_CLIENTES = "clientes.txt";
    private final String ARCHIVO_VENTAS = "ventas.txt";
    private final String ARCHIVO_DETALLES = "detalles_ventas.txt";
    
    // Necesitamos que este manager conozca al inventario para poder reconstruir
    // los productos cuando lea el archivo de detalles.
    private InventarioManager inventarioRef;

    public VentasManager(InventarioManager inventario) {
        this.listaCuentas = new ArrayList<>();
        this.inventarioRef = inventario;
        cargarDatos(); // Cargamos automáticamente al iniciar
    }

    public ArrayList<CuentaCliente> getListaCuentas() {
        return listaCuentas;
    }

    public void agregarCliente(CuentaCliente nuevoCliente) {
        listaCuentas.add(nuevoCliente);
        guardarDatos(); // Guardamos cambios
    }

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

        // 1. Validar stock
        for (DetalleVenta detalle : nuevaVenta.getDetalles()) {
            Producto pInv = inventarioRef.buscarProducto(detalle.getProducto().getId());
            if (pInv == null || pInv.getCantidad() < detalle.getCantidad()) {
                System.out.println("Error: No hay suficiente stock de " + detalle.getProducto().getNombre());
                return false; 
            }
        }

        // 2. Reducir stock
        for (DetalleVenta detalle : nuevaVenta.getDetalles()) {
            inventarioRef.reducirStock(detalle.getProducto().getId(), detalle.getCantidad());
        }

        // 3. Guardar en historial y archivos
        cliente.agregarVenta(nuevaVenta);
        guardarDatos(); // Guardamos todos los archivos tras una venta
        
        System.out.println("¡Venta procesada con éxito! Total: $" + nuevaVenta.getTotal());
        return true;
    }

    // --- NUEVAS FUNCIONES PARA LA INTERFAZ ---

    public String obtenerCuentasText() {
        if (listaCuentas.isEmpty()) return "Aún no hay clientes registrados en el sistema.";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-30s\n", "ID CLIENTE", "NOMBRE DEL CLIENTE"));
        sb.append("--------------------------------------------------\n");

        for (CuentaCliente c : listaCuentas) {
            sb.append(String.format("%-15s %-30s\n", c.getIdCuenta(), c.getNombreCliente()));
        }
        return sb.toString();
    }

    // Devuelve TODAS las ventas de TODOS los clientes formateadas para el JTextArea
    public String obtenerTodasLasVentasTexto() {
        StringBuilder sb = new StringBuilder();
        boolean hayVentas = false;

        for (CuentaCliente c : listaCuentas) {
            for (Venta v : c.getHistorialVentas()) {
                hayVentas = true;
                sb.append("Fecha: ").append(v.getFecha())
                  .append(" | Cliente: ").append(c.getNombreCliente())
                  .append(" (ID: ").append(c.getIdCuenta()).append(")\n");
                for (DetalleVenta d : v.getDetalles()) {
                    sb.append(String.format("   - %-20s x%-5d  $%.2f\n",
                        d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal()));
                }
                sb.append(String.format("   TOTAL VENTA: $%.2f\n", v.getTotal()));
                sb.append("----------------------------------------------\n");
            }
        }

        if (!hayVentas) return "No se han registrado ventas en el sistema.";
        return sb.toString();
    }

    public String generarCorteCaja(String fechaOperativa) {
        double totalDia = 0.0;
        int ventasTotales = 0;
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("=== CORTE DE CAJA ===\n");
        reporte.append("Fecha de Operación: ").append(fechaOperativa).append("\n");
        reporte.append("------------------------------------------------------------------\n");
        
        for (CuentaCliente c : listaCuentas) {
            for (Venta v : c.getHistorialVentas()) {
                if (v.getFecha().equals(fechaOperativa)) {
                    reporte.append(String.format("Venta ID: %-10s | Cliente: %-15s | Total: $%.2f\n", 
                            v.getIdVenta(), c.getNombreCliente(), v.getTotal()));
                    totalDia += v.getTotal();
                    ventasTotales++;
                }
            }
        }
        
        reporte.append("------------------------------------------------------------------\n");
        reporte.append("Ventas Totales Realizadas: ").append(ventasTotales).append("\n");
        reporte.append("INGRESOS TOTALES DEL DÍA: $").append(totalDia).append("\n");
        
        try {
            // Se usa .replace para no crear carpetas accidentales con los slashes de la fecha
            String nombreArchivo = "Corte_Dia_" + fechaOperativa.replace("/", "-") + ".txt";
            PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo));
            pw.print(reporte.toString());
            pw.close();
            return "Corte generado con éxito en el archivo: " + nombreArchivo + "\n\nRecaudación Total: $" + totalDia;
        } catch (Exception e) {
            return "Error al generar archivo de corte de caja.";
        }
    }

    // --- MAGIA DE ARCHIVOS (ESTILO BASE DE DATOS) ---

    private void guardarDatos() {
        try {
            PrintWriter escClientes = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES));
            PrintWriter escVentas = new PrintWriter(new FileWriter(ARCHIVO_VENTAS));
            PrintWriter escDetalles = new PrintWriter(new FileWriter(ARCHIVO_DETALLES));

            for (CuentaCliente cliente : listaCuentas) {
                // Guardamos al cliente
                escClientes.println(cliente.getIdCuenta() + "," + cliente.getNombreCliente());

                for (Venta venta : cliente.getHistorialVentas()) {
                    // Guardamos la venta (le atamos el ID del cliente para saber de quién es)
                    escVentas.println(venta.getIdVenta() + "," + cliente.getIdCuenta() + "," + venta.getFecha());

                    for (DetalleVenta detalle : venta.getDetalles()) {
                        // Guardamos cada renglón atado al ID de la venta
                        escDetalles.println(venta.getIdVenta() + "," + detalle.getProducto().getId() + "," + detalle.getCantidad());
                    }
                }
            }

            escClientes.close();
            escVentas.close();
            escDetalles.close();
        } catch (Exception e) {
            System.out.println("Error al guardar clientes y ventas: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            // 1. Cargamos a los Clientes
            BufferedReader lecClientes = new BufferedReader(new FileReader(ARCHIVO_CLIENTES));
            String linea;
            while ((linea = lecClientes.readLine()) != null) {
                String[] datos = linea.split(",");
                listaCuentas.add(new CuentaCliente(datos[0], datos[1]));
            }
            lecClientes.close();

            // 2. Cargamos las Ventas vacías y se las asignamos a sus dueños
            BufferedReader lecVentas = new BufferedReader(new FileReader(ARCHIVO_VENTAS));
            while ((linea = lecVentas.readLine()) != null) {
                String[] datos = linea.split(",");
                String idVenta = datos[0];
                String idCuenta = datos[1];
                String fecha = datos[2];
                
                CuentaCliente c = buscarCliente(idCuenta);
                if (c != null) {
                    c.agregarVenta(new Venta(idVenta, idCuenta, fecha));
                }
            }
            lecVentas.close();

            // 3. Cargamos los detalles, buscamos el Producto real, y lo metemos a su Venta
            BufferedReader lecDetalles = new BufferedReader(new FileReader(ARCHIVO_DETALLES));
            while ((linea = lecDetalles.readLine()) != null) {
                String[] datos = linea.split(",");
                String idVenta = datos[0];
                String idProducto = datos[1];
                int cantidad = Integer.parseInt(datos[2]);
                
                Producto p = inventarioRef.buscarProducto(idProducto); // Magia: aquí usamos la referencia al inventario
                if (p != null) {
                    Venta v = buscarVentaGlobal(idVenta);
                    if (v != null) {
                        v.agregarDetalle(p, cantidad); // Esto también suma al total automáticamente
                    }
                }
            }
            lecDetalles.close();

        } catch (Exception e) {
            System.out.println("No se encontraron archivos de ventas previos. Empezando en limpio.");
        }
    }

    // Método auxiliar interno que busca una venta en todos los historiales de todos los clientes
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
