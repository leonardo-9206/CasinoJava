package managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import models.Usuario;
import models.Admin;
import models.Empleado;
import models.Cliente;

public class UsuarioManager {

    private ArrayList<Usuario> listaUsuarios;
    private final String RUTA_ARCHIVO = "usuarios.txt";

    public UsuarioManager() {
        listaUsuarios = new ArrayList<>();
        cargarDatos();

        if (listaUsuarios.isEmpty()) {
            // Utilizamos la clase hija "Admin" en lugar de la clase padre
            Usuario adminPorDefecto = new Admin("U001", "admin", "1234");
            agregarUsuario(adminPorDefecto);
        }
    }

    public Usuario login(String nombreIngresado, String contrasenaIngresada) {
        for (Usuario u : listaUsuarios) {
            // Verificamos credenciales
            if (u.getNombre().equals(nombreIngresado) && u.getPassword().equals(contrasenaIngresada)) {
                // AQUÍ PODEMOS VER EL POLIMORFISMO EN ACCIÓN EN LA CONSOLA:
                System.out.println("Permisos del usuario: " + u.obtenerNivelAcceso());
                return u;
            }
        }
        return null;
    }

    public void agregarUsuario(Usuario nuevoUsuario) {
        listaUsuarios.add(nuevoUsuario);
        guardarDatos();
    }

    public boolean existeUsuario(String nombreUsuario) {
        for (Usuario u : listaUsuarios) {
            if (u.getNombre().equals(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        for (Usuario u : listaUsuarios) {
            if (u.getNombre().equals(nombreUsuario)) {
                listaUsuarios.remove(u);
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    private void guardarDatos() {
        try {
            FileWriter archivo = new FileWriter(RUTA_ARCHIVO);
            PrintWriter escritor = new PrintWriter(archivo);

            for (Usuario u : listaUsuarios) {
                // Formato txt: idUsuario,nombre,password,rol
                String linea = u.getIdUsuario() + "," + u.getNombre() + "," + u.getPassword() + "," + u.getRol();
                escritor.println(linea);
            }

            escritor.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            FileReader archivo = new FileReader(RUTA_ARCHIVO);
            BufferedReader lector = new BufferedReader(archivo);
            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(",");
                
                // Compatibilidad con la versión vieja que solo tenía 3 datos
                String id = datos.length >= 4 ? datos[0] : "U" + System.currentTimeMillis();
                String nombre = datos.length >= 4 ? datos[1] : datos[0];
                String pass = datos.length >= 4 ? datos[2] : datos[1];
                String rol = datos.length >= 4 ? datos[3] : datos[2];

                Usuario u;
                // ==========================================
                // REQUISITO CUMPLIDO: HERENCIA (Instanciando hijas)
                // ==========================================
                if (rol.equalsIgnoreCase("Admin")) {
                    u = new Admin(id, nombre, pass);
                } else if (rol.equalsIgnoreCase("Empleado")) {
                    u = new Empleado(id, nombre, pass);
                } else {
                    u = new Cliente(id, nombre, pass);
                }
                
                listaUsuarios.add(u);
            }

            lector.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("No se encontró " + RUTA_ARCHIVO + ". Se creará el usuario Admin por defecto.");
        }
    }
}
