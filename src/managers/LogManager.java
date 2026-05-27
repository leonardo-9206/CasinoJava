package managers;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {

    // Nombres de los archivos de bitácora
    private static final String LOG_CAMBIOS = "cambiosproductos.log";
    private static final String LOG_RESTOCK = "restock.log";
    private static final String LOG_ACCESOS = "accesos.log";

    // Método auxiliar para obtener la fecha y hora actual automáticamente
    // Esto reemplaza tu antigua clase "fecha.h" de C++ de una manera mucho más limpia.
    private static String obtenerFechaHora() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ahora.format(formato);
    }

    // Registra cuando se edita un producto
    public static void registrarCambioProducto(String nombreEmpleado, String idProducto, String campoModificado, String antes, String despues) {
        String mensaje = obtenerFechaHora() + " | Empleado: " + nombreEmpleado + 
                         " | ProdID: " + idProducto + 
                         " | Modificó [" + campoModificado + "] de '" + antes + "' a '" + despues + "'";
        escribirLinea(LOG_CAMBIOS, mensaje);
    }

    // Registra cuando se surte más mercancía
    public static void registrarRestock(String nombreEmpleado, String idProducto, int cantidadAgregada, int nuevoStockTotal) {
        String mensaje = obtenerFechaHora() + " | Empleado: " + nombreEmpleado + 
                         " | ProdID: " + idProducto + 
                         " | Agregó +" + cantidadAgregada + " unidades (Stock final: " + nuevoStockTotal + ")";
        escribirLinea(LOG_RESTOCK, mensaje);
    }
    
    // Opcional: Registra quién y a qué hora inició sesión
    public static void registrarLogin(String nombreUsuario, String rol) {
        String mensaje = obtenerFechaHora() + " | Login Exitoso | Usuario: " + nombreUsuario + " | Rol: " + rol;
        escribirLinea(LOG_ACCESOS, mensaje);
    }

    // El motor central que escribe en los archivos
    private static void escribirLinea(String nombreArchivo, String lineaTexto) {
        try {
            // El parámetro 'true' en FileWriter es la magia:
            // Le dice a Java que no sobrescriba el archivo desde cero, sino que "añada" la línea al final.
            FileWriter archivo = new FileWriter(nombreArchivo, true);
            PrintWriter escritor = new PrintWriter(archivo);
            
            escritor.println(lineaTexto);
            
            escritor.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error al guardar en la bitácora (" + nombreArchivo + "): " + e.getMessage());
        }
    }
}
