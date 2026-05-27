import managers.*;
import models.*;

public class TestLogica {
    public static void main(String[] args) {
        System.out.println("========== INICIANDO PRUEBAS DE ESTRÉS (BACKEND CASINO) ==========");
        
        try {
            // 1. Pruebas de Usuarios
            System.out.println("\n[1] Probando UsuarioManager...");
            UsuarioManager uMgr = new UsuarioManager();
            if (uMgr.login("admin", "1234") != null) {
                System.out.println("  [OK] Admin por defecto existe y login funciona.");
            } else {
                System.out.println("  [FAIL] Login falló.");
            }
            
            // 2. Pruebas de Inventario
            System.out.println("\n[2] Probando InventarioManager...");
            InventarioManager iMgr = new InventarioManager();
            // Evitamos duplicados en las pruebas si ya existen
            if(iMgr.buscarProducto("P-TEST") == null) {
                iMgr.agregarProducto(new Producto("P-TEST", "Fichas de Prueba", 10.0, 100, 5, 200, "Fichas", true));
            }
            Producto pTest = iMgr.buscarProducto("P-TEST");
            if (pTest != null && pTest.getCantidad() == 100) {
                System.out.println("  [OK] Producto de prueba agregado (Stock inicial: 100).");
            } else {
                System.out.println("  [FAIL] Error al crear/leer producto de prueba.");
            }
            
            // 3. Pruebas de Ventas e Integración
            System.out.println("\n[3] Probando VentasManager e Integración con Inventario...");
            VentasManager vMgr = new VentasManager(iMgr);
            if(vMgr.buscarCliente("C-TEST") == null) {
                vMgr.agregarCliente(new CuentaCliente("C-TEST", "Cliente Prueba"));
            }
            
            Venta nuevaVenta = new Venta("V-TEST", "C-TEST", "2026-05-01");
            nuevaVenta.agregarDetalle(pTest, 20); // Vendemos 20 fichas
            
            boolean exitoVenta = vMgr.procesarVenta(nuevaVenta);
            if (exitoVenta) {
                System.out.println("  [OK] Venta procesada sin crashear.");
            } else {
                System.out.println("  [FAIL] La venta fue rechazada por error de lógica.");
            }
            
            // Verificamos si realmente se redujo el stock (100 - 20 = 80)
            Producto pTestDespues = iMgr.buscarProducto("P-TEST");
            if (pTestDespues != null && pTestDespues.getCantidad() == 80) {
                System.out.println("  [OK] Matemáticas de Stock correctas (Quedan 80).");
            } else {
                System.out.println("  [FAIL] Matemáticas de Stock incorrectas. Hay: " + pTestDespues.getCantidad());
            }
            
            // 4. Pruebas de Bitácoras (Logs)
            System.out.println("\n[4] Probando LogManager...");
            LogManager.registrarCambioProducto("admin", "P-TEST", "Precio", "10", "15");
            LogManager.registrarLogin("admin", "Admin");
            System.out.println("  [OK] Logs escritos silenciosamente en archivos .log.");
            
            // 5. Pruebas de Reportes
            System.out.println("\n[5] Probando ReportesManager...");
            ReportesManager.generarReporteVentasGlobal(vMgr);
            System.out.println("  [OK] Archivo de reporte generado.");
            
            System.out.println("\n========== TODAS LAS PRUEBAS FINALIZADAS ==========");
            
        } catch (Exception e) {
            System.out.println("\n[CRITICAL ERROR] El sistema crasheó durante las pruebas:");
            e.printStackTrace();
        }
    }
}
