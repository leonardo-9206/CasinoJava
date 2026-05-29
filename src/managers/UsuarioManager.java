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

//se encarga de funciones relacionadas a manejar usuarios
public class UsuarioManager {
    private ArrayList<Usuario> listaUsuarios;
    private final String RUTA_ARCHIVO = "usuarios.txt"; //nombre del archivo

    //constructor del manager
    public UsuarioManager() {
        listaUsuarios = new ArrayList<>();
        cargarDatos();
        //si estuviera vacio por borrar usuarios.txt, se crea un admin por defecto
        if (listaUsuarios.isEmpty()) {
            Usuario adminPorDefecto = new Admin("1", "admin", "1234");
            agregarUsuario(adminPorDefecto);
        }
    }

    public Usuario login(String nombreIngresado, String contrasenaIngresada) {
        for (Usuario u : listaUsuarios) {
            // Verificamos credenciales
            if (u.getNombre().equals(nombreIngresado) && u.getPassword().equals(contrasenaIngresada)) {
                //si concide usuario y contraseña en el arraylist
                System.out.println("Permisos del usuario: " + u.obtenerNivelAcceso()); //polimorfismo, imprime dependiendo de su rol
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
            //para que no haya problemas con mayusculas o minusculas
            if (u.getNombre().equalsIgnoreCase(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeIdUsuario(String idUsuario) {
        for (Usuario u : listaUsuarios) {
            if (u.getIdUsuario().equalsIgnoreCase(idUsuario)) {
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

    //NO PONER ESTE METODO, LO PONGO YO
    //acomoda los usuarios del arraylist para poder presentarlos en GUI
    public String obtenerUsuariosTexto() {
        if (listaUsuarios.isEmpty()){
            return "No hay usuarios registrados.";
        } 
        //usamos stringbuilder para no estar generando aca 1000 strings dentro del ciclo
        StringBuilder sb = new StringBuilder();
        //se le da forma con columnas, y al final del renglon un enter
        sb.append(String.format("%-15s %-20s %-15s %-15s\n", "ID USUARIO", "NOMBRE", "CONTRASEÑA", "ROL"));
        sb.append("----------------------------------------------------------------------\n");
        for (Usuario u : listaUsuarios) {
            sb.append(String.format("%-15s %-20s %-15s %-15s\n", 
                u.getIdUsuario(), u.getNombre(), u.getPassword(), u.getRol()));
        }
        //retorna el string ya hecho con columna para nomas ponerlo en el textarea
        return sb.toString();
    }

    private void guardarDatos() {
        try {
            FileWriter archivo = new FileWriter(RUTA_ARCHIVO);
            PrintWriter escritor = new PrintWriter(archivo);
            for (Usuario u : listaUsuarios) {
                //guarda los datos separados por coma
                String linea = u.getIdUsuario() + "," + u.getNombre() + "," + u.getPassword() + "," + u.getRol();
                escritor.println(linea);
            }
            escritor.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    //carga datos desde usuarios.txt
    private void cargarDatos() {
        try {
            FileReader archivo = new FileReader(RUTA_ARCHIVO);
            BufferedReader lector = new BufferedReader(archivo);
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(","); //lo separa por comas
                //asinga el texto a los atributos del objeto
                String id = datos[0];
                String nombre = datos[1];
                String pass = datos[2];
                String rol = datos[3];
                //crea el usuario
                Usuario u;
                //crea la clase segun el rol que tenga
                if (rol.equalsIgnoreCase("Admin")) {
                    u = new Admin(id, nombre, pass);
                } else if (rol.equalsIgnoreCase("Empleado")) {
                    u = new Empleado(id, nombre, pass);
                } else {
                    u = new Cliente(id, nombre, pass);
                }
                //lo agrega al arraylist
                listaUsuarios.add(u);
            }
            lector.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("No se encontró " + RUTA_ARCHIVO + ". Se creará el usuario Admin por defecto.");
        }
    }
}
