package managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import models.CuentaCliente;
import models.DetalleVenta;
import models.Venta;

public class ReportesManager {

    private static String getFechaHora() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    // Reporte global de todas las ventas del casino (genera .txt y devuelve confirmación)
    public static String generarReporteVentasGlobal(VentasManager vManager) {
        String nombreArchivo = "Reporte_Global_" + getFechaHora() + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nombreArchivo))) {
            out.println("=========================================");
            out.println("        REPORTE DE VENTAS GLOBAL         ");
            out.println("=========================================");
            
            double granTotal = 0;
            
            for (CuentaCliente c : vManager.getListaCuentas()) {
                if(c.getHistorialVentas().isEmpty()) continue;
                
                out.println("\n--- CLIENTE: " + c.getNombreCliente() + " (ID: " + c.getIdCuenta() + ") ---");
                for (Venta v : c.getHistorialVentas()) {
                    out.println("  Venta ID: " + v.getIdVenta() + " | Fecha: " + v.getFecha());
                    for (DetalleVenta d : v.getDetalles()) {
                        out.println("    - " + d.getProducto().getNombre() + " x" + d.getCantidad() + " ($" + d.getSubtotal() + ")");
                    }
                    out.println("  TOTAL VENTA: $" + v.getTotal());
                    granTotal += v.getTotal();
                }
                out.println("  Total Histórico del Cliente: $" + c.calcularTotalGastado());
            }
            
            out.println("\n=========================================");
            out.println("GRAN TOTAL INGRESOS DEL CASINO: $" + granTotal);
            out.println("=========================================");
            return "Reporte global generado con éxito en: " + nombreArchivo;
            
        } catch (Exception e) {
            return "Error al generar reporte global: " + e.getMessage();
        }
    }

    // Reporte específico de un solo cliente (genera .txt y devuelve confirmación)
    public static String generarReporteCliente(CuentaCliente cliente) {
        if(cliente == null) return "Error: Cliente no encontrado.";
        
        String nombreArchivo = "Reporte_Cliente_" + cliente.getIdCuenta() + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nombreArchivo))) {
            out.println("=== ESTADO DE CUENTA ===");
            out.println("Cliente: " + cliente.getNombreCliente());
            out.println("ID Cuenta: " + cliente.getIdCuenta());
            out.println("------------------------");
            
            for (Venta v : cliente.getHistorialVentas()) {
                out.println("Fecha: " + v.getFecha() + " | Ticket: " + v.getIdVenta() + " | Monto: $" + v.getTotal());
                for (DetalleVenta d : v.getDetalles()) {
                    out.println("  - " + d.getProducto().getNombre() + " x" + d.getCantidad() + " ($" + d.getSubtotal() + ")");
                }
            }
            
            out.println("------------------------");
            out.println("TOTAL GASTADO: $" + cliente.calcularTotalGastado());
            return "Reporte de cliente generado con éxito en: " + nombreArchivo;
            
        } catch (Exception e) {
            return "Error al generar reporte de cliente: " + e.getMessage();
        }
    }

    // NUEVO: Reporte filtrado por fecha (genera .txt)
    public static String generarReportePorFecha(VentasManager vManager, String fecha) {
        String nombreArchivo = "Reporte_Fecha_" + fecha.replace("/", "-") + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nombreArchivo))) {
            out.println("=========================================");
            out.println("    REPORTE DE VENTAS POR FECHA          ");
            out.println("    Fecha: " + fecha);
            out.println("=========================================");
            
            double totalFecha = 0;
            int ventasEncontradas = 0;
            
            for (CuentaCliente c : vManager.getListaCuentas()) {
                for (Venta v : c.getHistorialVentas()) {
                    if (v.getFecha().equals(fecha)) {
                        ventasEncontradas++;
                        out.println("\nCliente: " + c.getNombreCliente() + " (ID: " + c.getIdCuenta() + ")");
                        for (DetalleVenta d : v.getDetalles()) {
                            out.println("  - " + d.getProducto().getNombre() + " x" + d.getCantidad() + " ($" + d.getSubtotal() + ")");
                        }
                        out.println("  TOTAL VENTA: $" + v.getTotal());
                        totalFecha += v.getTotal();
                    }
                }
            }
            
            out.println("\n=========================================");
            out.println("Ventas encontradas: " + ventasEncontradas);
            out.println("TOTAL RECAUDADO EN FECHA: $" + totalFecha);
            out.println("=========================================");
            
            if (ventasEncontradas == 0) {
                return "No se encontraron ventas en la fecha: " + fecha + "\n(El archivo fue generado vacío)";
            }
            return "Reporte por fecha generado con éxito en: " + nombreArchivo + "\nVentas: " + ventasEncontradas + " | Total: $" + totalFecha;
            
        } catch (Exception e) {
            return "Error al generar reporte por fecha: " + e.getMessage();
        }
    }

    // NUEVO: Reporte filtrado por producto (genera .txt)
    public static String generarReportePorProducto(VentasManager vManager, String idProducto) {
        String nombreArchivo = "Reporte_Producto_" + idProducto + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nombreArchivo))) {
            out.println("=========================================");
            out.println("    REPORTE DE VENTAS POR PRODUCTO       ");
            out.println("    Producto ID: " + idProducto);
            out.println("=========================================");
            
            double totalProducto = 0;
            int unidadesVendidas = 0;
            int ventasEncontradas = 0;
            
            for (CuentaCliente c : vManager.getListaCuentas()) {
                for (Venta v : c.getHistorialVentas()) {
                    for (DetalleVenta d : v.getDetalles()) {
                        if (d.getProducto().getId().equals(idProducto)) {
                            ventasEncontradas++;
                            out.println("\nFecha: " + v.getFecha() + " | Cliente: " + c.getNombreCliente());
                            out.println("  Cantidad: " + d.getCantidad() + " | Subtotal: $" + d.getSubtotal());
                            totalProducto += d.getSubtotal();
                            unidadesVendidas += d.getCantidad();
                        }
                    }
                }
            }
            
            out.println("\n=========================================");
            out.println("Apariciones en ventas: " + ventasEncontradas);
            out.println("Unidades vendidas en total: " + unidadesVendidas);
            out.println("INGRESOS TOTALES DEL PRODUCTO: $" + totalProducto);
            out.println("=========================================");
            
            if (ventasEncontradas == 0) {
                return "No se encontraron ventas del producto: " + idProducto + "\n(El archivo fue generado vacío)";
            }
            return "Reporte por producto generado con éxito en: " + nombreArchivo + "\nUnidades vendidas: " + unidadesVendidas + " | Total: $" + totalProducto;
            
        } catch (Exception e) {
            return "Error al generar reporte por producto: " + e.getMessage();
        }
    }

    // NUEVO: Lee el archivo restock.log y devuelve su contenido para el JTextArea
    public static String leerLogRestock() {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader("restock.log"));
            String linea;
            sb.append("=== HISTORIAL DE RESTOCKS ===\n");
            sb.append("----------------------------------------------\n");
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
            br.close();
            if (sb.toString().split("\n").length <= 2) {
                return "No se han registrado restocks en el sistema.";
            }
            return sb.toString();
        } catch (Exception e) {
            return "No se encontró el archivo de log de restocks.\n(Aún no se han realizado restocks en el sistema)";
        }
    }
}
