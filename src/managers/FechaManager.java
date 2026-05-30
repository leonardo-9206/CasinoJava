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
            //validamos que no este vacio
            if(fechaActual == null || fechaActual.isEmpty()) {
                throw new Exception("Archivo vacío");
            }
        } catch (Exception e) {
            //si no existe el archivo, primera ejecucion e iniciamos con esta fecha
            fechaActual = "01/04/2026";
            guardarFecha();
        }
    }

    public static void avanzarDia() {
        try {
            //para el formato
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            //convertimos de texto a objeto
            LocalDate fecha = LocalDate.parse(getFechaActual(), formatter);
            //avanzamos un dia
            fecha = fecha.plusDays(1);
            //lo regresamos a texto
            fechaActual = fecha.format(formatter);
            //lo guardamos en el txt
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
