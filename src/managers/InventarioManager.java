package managers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import models.Producto;

public class InventarioManager {

    private ArrayList<Producto> listaProductos;
    
    // Rutas de archivos (Cumpliendo el requisito mixto de txt y dat)
    private final String RUTA_BINARIO = "inventario.dat";
    private final String RUTA_TEXTO_DEFECTO = "inventario_default.txt";

    public InventarioManager() {
        listaProductos = new ArrayList<>();
        cargarDatos();
    }

    public void agregarProducto(Producto nuevoProducto) {
        listaProductos.add(nuevoProducto);
        guardarDatos();
    }

    public Producto buscarProducto(String idBuscar) {
        for (Producto p : listaProductos) {
            if (p.getId().equals(idBuscar)) {
                return p;
            }
        }
        return null; 
    }

    public Producto buscarProductoPorNombre(String nombreBuscar) {
        for (Producto p : listaProductos) {
            if (p.getNombre().equalsIgnoreCase(nombreBuscar)) {
                return p;
            }
        }
        return null;
    }

    public boolean eliminarProducto(String idEliminar) {
        Producto p = buscarProducto(idEliminar);
        if (p != null) {
            // Baja Lógica: En lugar de borrarlo de memoria, lo desactivamos
            p.setActivo(false);
            guardarDatos(); 
            return true; 
        }
        return false; 
    }

    public ArrayList<Producto> getTodosLosProductos() {
        return listaProductos;
    }

    public boolean reducirStock(String id, int cantidadARestar) {
        Producto p = buscarProducto(id);
        if (p != null && p.getCantidad() >= cantidadARestar) {
            p.setCantidad(p.getCantidad() - cantidadARestar);
            guardarDatos();
            return true; 
        }
        return false; 
    }

    // --- NUEVAS FUNCIONALIDADES PARA LA INTERFAZ GRÁFICA ---
    
    // Método para hacer restock validando el máximo
    public String hacerRestock(String id, int cantidadSumar) {
        Producto p = buscarProducto(id);
        if (p == null) return "Error: Producto no encontrado.";
        if (!p.isActivo()) return "Error: El producto está dado de baja.";
        
        int nuevoStock = p.getCantidad() + cantidadSumar;
        if (nuevoStock > p.getStockMax()) {
            return "Error: Excede el stock máximo permitido (" + p.getStockMax() + ").";
        }
        
        p.setCantidad(nuevoStock);
        guardarDatos();
        return "Éxito: Restock realizado correctamente.";
    }

    // Método para modificar atributos
    public boolean modificarProducto(String id, int opcionCampo, String nuevoValor) {
        Producto p = buscarProducto(id);
        if (p == null) return false;

        try {
            switch (opcionCampo) {
                case 1: p.setNombre(nuevoValor); break;
                case 2: p.setPrecio(Double.parseDouble(nuevoValor)); break;
                case 3: p.setStockMin(Integer.parseInt(nuevoValor)); break;
                case 4: p.setStockMax(Integer.parseInt(nuevoValor)); break;
                case 5: p.setTipo(nuevoValor); break;
                default: return false;
            }
            guardarDatos();
            return true;
        } catch (Exception e) {
            return false; // Error de conversión (ej. meter letras en precio)
        }
    }

    // Método que formatea todo el inventario en texto plano para el JTextArea
    public String obtenerInventarioTexto() {
        if (listaProductos.isEmpty()) return "El inventario está vacío.";

        StringBuilder sb = new StringBuilder();
        // Encabezado
        sb.append(String.format("%-10s %-20s %-15s %-10s %-15s %-15s %-10s\n", 
            "ID", "NOMBRE", "TIPO", "PRECIO", "STOCK", "MIN / MAX", "ESTADO"));
        sb.append("-----------------------------------------------------------------------------------------------------\n");

        for (Producto p : listaProductos) {
            String estado = p.isActivo() ? "Activo" : "INACTIVO";
            String minMax = p.getStockMin() + " / " + p.getStockMax();
            
            sb.append(String.format("%-10s %-20s %-15s $%-9.2f %-15d %-15s %-10s\n", 
                p.getId(), 
                p.getNombre(), 
                p.getTipo(), 
                p.getPrecio(), 
                p.getCantidad(), 
                minMax, 
                estado));
        }
        return sb.toString();
    }

    // ==========================================
    // REQUISITO CUMPLIDO: ARCHIVOS BINARIOS
    // ==========================================
    private void guardarDatos() {
        try {
            // Escribimos TODO el ArrayList directamente al archivo .dat usando serialización binaria
            FileOutputStream archivo = new FileOutputStream(RUTA_BINARIO);
            ObjectOutputStream escritor = new ObjectOutputStream(archivo);

            escritor.writeObject(listaProductos);

            escritor.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error al guardar el archivo binario: " + e.getMessage());
        }
    }

    // Cargar los datos priorizando el archivo binario, y usando el txt como respaldo inicial
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try {
            // Intento 1: Cargar el archivo Binario (El que se estará modificando siempre)
            FileInputStream archivo = new FileInputStream(RUTA_BINARIO);
            ObjectInputStream lector = new ObjectInputStream(archivo);
            
            // Leemos el ArrayList completo de un solo golpe
            listaProductos = (ArrayList<Producto>) lector.readObject();
            
            lector.close();
            archivo.close();
            
        } catch (Exception e) {
            System.out.println("No se encontró " + RUTA_BINARIO + ". Buscando inventario_default.txt...");
            try {
                // Intento 2: Si es la primera vez que se corre, cargar el TXT por defecto
                FileReader archivoDefecto = new FileReader(RUTA_TEXTO_DEFECTO);
                leerArchivoYCargarLista(archivoDefecto);
                System.out.println("¡Inventario TXT por defecto cargado! Convirtiendo y guardando copia en BINARIO (.dat)...");
                guardarDatos(); // Guardamos el .dat para futuras ejecuciones
            } catch (Exception ex) {
                System.out.println("Tampoco se encontró " + RUTA_TEXTO_DEFECTO + ". Empezando con inventario en blanco.");
            }
        }
    }

    // Método auxiliar clásico para leer texto plano delimitado por comas
    private void leerArchivoYCargarLista(FileReader archivo) throws Exception {
        BufferedReader lector = new BufferedReader(archivo);
        String linea;

        while ((linea = lector.readLine()) != null) {
            String[] datos = linea.split(",");
            String id = datos[0];
            String nombre = datos[1];
            double precio = Double.parseDouble(datos[2]);
            int cantidad = Integer.parseInt(datos[3]);
            
            int stockMin = 0;
            int stockMax = 100;
            String tipo = "Fichas";
            boolean activo = true;

            if (datos.length >= 8) {
                stockMin = Integer.parseInt(datos[4]);
                stockMax = Integer.parseInt(datos[5]);
                tipo = datos[6];
                activo = Boolean.parseBoolean(datos[7]);
            }

            Producto p = new Producto(id, nombre, precio, cantidad, stockMin, stockMax, tipo, activo);
            listaProductos.add(p);
        }

        lector.close();
        archivo.close();
    }
}
