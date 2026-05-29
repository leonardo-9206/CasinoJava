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
    //el inventario guarda todos los productos en un arraylist
    private ArrayList<Producto> listaProductos;
    private final String RUTA_BINARIO = "inventario.dat";
    private final String RUTA_TEXTO_DEFECTO = "inventario_default.txt";

    //constructor
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

    public boolean eliminarProducto(String idEliminar) {
        Producto p = buscarProducto(idEliminar);
        //si no retorna null, lo encontro
        if (p != null) {
            //le hacemos baja logica
            p.setActivo(false);
            //se actualiza el binario
            guardarDatos(); 
            return true; 
        }
        return false; 
    }

    //retorna todos los productos
    public ArrayList<Producto> getTodosLosProductos() {
        return listaProductos;
    }

    //reducir stock para actualizarlo despues de una venta
    public boolean reducirStock(String id, int cantidadARestar) {
        Producto p = buscarProducto(id);
        //si si lo encontro, y la cantidad actual es mayor o igual a la cantidad a restar
        if (p != null && p.getCantidad() >= cantidadARestar) {
            p.setCantidad(p.getCantidad() - cantidadARestar);
            guardarDatos();
            return true; 
        }
        return false; 
    }

    //hacer restock de producto
    public String hacerRestock(String id, int cantidadSumar) {
        Producto p = buscarProducto(id);
        if (p == null){
            return "Error: Producto no encontrado.";
        }
        if (!p.isActivo()) {
            return "Error: El producto está dado de baja.";
        } 
        int nuevoStock = p.getCantidad() + cantidadSumar;
        //que no lo deje reestockear mas de lo permitido
        if (nuevoStock > p.getStockMax()) {
            return "Error: Excede el stock máximo permitido (" + p.getStockMax() + ").";
        }
        //cambia el atributo
        p.setCantidad(nuevoStock);
        //actualiza el binario
        guardarDatos();
        return "Éxito: Restock realizado correctamente.";
    }

    //modificar atributos de producto
    public boolean modificarProducto(String id, int opcionCampo, String nuevoValor) {
        Producto p = buscarProducto(id);
        if (p == null){
            return false;
        }
        try {
            //le preguntamos que campo quiere cambiar (esto va para el optionpane)
            switch (opcionCampo) {
                case 1: p.setNombre(nuevoValor);{
                    break;
                }
                //convertimos a enteros
                case 2: p.setPrecio(Double.parseDouble(nuevoValor));{
                    break;
                }
                case 3: p.setStockMin(Integer.parseInt(nuevoValor)); {
                    break;
                }
                case 4: p.setStockMax(Integer.parseInt(nuevoValor)); {
                    break;
                }
                case 5: p.setTipo(nuevoValor); {
                    break;
                }
                default: 
                    return false;
            }
            //actualizamos el binario
            guardarDatos();
            //se pudo modificar. esto es pensando en el optionpane
            return true;
        } catch (Exception e) {
            return false; //si metio una letra en vez de numero
        }
    }

    //ESTE METODO NO LO PONGAN, LO PONGO YO
    //convierte todo el inventario en texto para el textarea
    public String obtenerInventarioTexto() {
        if (listaProductos.isEmpty()) {
            return "El inventario está vacío.";
        }
        //para el formato y no estar creando muchas strings
        StringBuilder sb = new StringBuilder();
        ////las columnas
        sb.append(String.format("%-10s %-20s %-15s %-10s %-15s %-15s %-10s\n", 
            "ID", "NOMBRE", "TIPO", "PRECIO", "STOCK", "MIN / MAX", "ESTADO"));
        sb.append("-----------------------------------------------------------------------------------------------------\n");
        for (Producto p : listaProductos) {
            String estado;
                if (p.isActivo()) {
                    estado = "Activo";
                } else {
                    estado = "INACTIVO";
                }
            //para que se muestren juntos minimo y maximo
            String minMax = p.getStockMin() + " / " + p.getStockMax();
            //lo formateamos
            sb.append(String.format("%-10s %-20s %-15s $%-9.2f %-15d %-15s %-10s\n", 
                p.getId(), 
                p.getNombre(), 
                p.getTipo(), 
                p.getPrecio(), 
                p.getCantidad(), 
                minMax, 
                estado));
        }
        //lo retornamos como string
        return sb.toString();
    }

    //para actualizar el binario
    private void guardarDatos() {
        try {
            //podemos poner todo el arraylist directamente con la serializacion
            FileOutputStream archivo = new FileOutputStream(RUTA_BINARIO);
            ObjectOutputStream escritor = new ObjectOutputStream(archivo);
            escritor.writeObject(listaProductos);
            escritor.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error al guardar el archivo binario: " + e.getMessage());
        }
    }

    //cargar datos poniendo como prioridad el binario
    private void cargarDatos() {
        try {
            FileInputStream archivo = new FileInputStream(RUTA_BINARIO);
            ObjectInputStream lector = new ObjectInputStream(archivo);
            //lee todo el inventario
            listaProductos = (ArrayList<Producto>) lector.readObject();
            lector.close();
            archivo.close();
        } catch (Exception e) {
            //por si es primera ejecucion, busca el inventario por defecto
            System.out.println("No se encontró " + RUTA_BINARIO + ". Buscando inventario_default.txt");
            try {
                //carga el txt por defecto
                FileReader archivoDefecto = new FileReader(RUTA_TEXTO_DEFECTO);
                leerDesdeTxt(archivoDefecto);
                System.out.println("Inventario txt cargado, convirtiendo y guardando copia en binario");
                guardarDatos(); //se guarda para las demas ejecuciones
            } catch (Exception ex) {
                //por si se borrar el txt con el inventario por defecto
                System.out.println("Tampoco se encontró " + RUTA_TEXTO_DEFECTO + ". Empezando con inventario en blanco.");
            }
        }
    }

    //leer texto separado por comas, desde el txt y cargandolo al arraylist
    private void leerDesdeTxt(FileReader archivo) throws Exception {
        BufferedReader lector = new BufferedReader(archivo);
        String linea;
        while ((linea = lector.readLine()) != null) {
            String[] datos = linea.split(",");
            String id = datos[0];
            String nombre = datos[1];
            double precio = Double.parseDouble(datos[2]);
            int cantidad = Integer.parseInt(datos[3]);
            int stockMin = Integer.parseInt(datos[4]);
            int stockMax = Integer.parseInt(datos[5]);
            String tipo = datos[6];
            boolean activo = Boolean.parseBoolean(datos[7]);
            Producto p = new Producto(id, nombre, precio, cantidad, stockMin, stockMax, tipo, activo);
            listaProductos.add(p);
        }
        lector.close();
        archivo.close();
    }
}
