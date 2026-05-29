package managers;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    private static final String LOG_CAMBIOS = "cambiosproductos.log";
    private static final String LOG_RESTOCK = "restock.log";
    

    private static String obtenerFechaHora() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ahora.format(formato);
    }

    //registra cuando se cambia un atributo de algun producto
    //registra quien lo cambio, el id del producto, el campo que modifico, lo que habia antes y lo que quedo despues
    public static void registrarCambioProducto(String nombreEmpleado, String idProducto, String campoModificado, String antes, String despues) {
        String mensaje = obtenerFechaHora() + " | Empleado: " + nombreEmpleado + 
                         " | ProdID: " + idProducto + 
                         " | Modificó [" + campoModificado + "] de '" + antes + "' a '" + despues + "'";
        escribirLinea(LOG_CAMBIOS, mensaje);
    }

    //registra cuando se hace un restock
    public static void registrarRestock(String nombreEmpleado, String idProducto, int cantidadAgregada, int nuevoStockTotal) {
        String mensaje = obtenerFechaHora() + " | Empleado: " + nombreEmpleado + 
                         " | ProdID: " + idProducto + 
                         " | Agregó +" + cantidadAgregada + " unidades (Stock final: " + nuevoStockTotal + ")";
        escribirLinea(LOG_RESTOCK, mensaje);
    }

    //reutilizamos esta funcion para no escribirla muchas veces
    private static void escribirLinea(String nombreArchivo, String lineaTexto) {
        try {
            //true para que añada la linea al final, que no sobrescriba
            FileWriter archivo = new FileWriter(nombreArchivo, true);
            PrintWriter escritor = new PrintWriter(archivo);
            //escribe la info
            escritor.println(lineaTexto);
            escritor.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error al guardar en la bitácora (" + nombreArchivo + "): " + e.getMessage());
        }
    }
}
