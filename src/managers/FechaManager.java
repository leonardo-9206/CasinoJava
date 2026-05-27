package managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FechaManager {

    private static final String ARCHIVO_FECHA = "fecha_sistema.txt";
    private static String fechaActual = null;

    public static String getFechaActual() {
        if (fechaActual == null) {
            cargarFecha();
        }
        return fechaActual;
    }

    private static void cargarFecha() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_FECHA));
            fechaActual = br.readLine();
            br.close();
            // Validar que no esté vacío
            if(fechaActual == null || fechaActual.isEmpty()) {
                throw new Exception("Archivo vacío");
            }
        } catch (Exception e) {
            // Si no existe el archivo, empezamos en esta fecha por defecto
            fechaActual = "01/04/2026";
            guardarFecha();
        }
    }

    public static void avanzarDia() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fecha = LocalDate.parse(getFechaActual(), formatter);
            fecha = fecha.plusDays(1);
            fechaActual = fecha.format(formatter);
            guardarFecha();
        } catch (Exception e) {
            System.out.println("Error al avanzar la fecha: " + e.getMessage());
        }
    }

    private static void guardarFecha() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_FECHA));
            pw.println(fechaActual);
            pw.close();
        } catch (Exception e) {
            System.out.println("Error guardando fecha de sistema.");
        }
    }
}
