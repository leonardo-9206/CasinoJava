package models;

// Aplicando Herencia: Admin "es un" Usuario
public class Admin extends Usuario {

    public Admin(String idUsuario, String nombre, String password) {
        // Llama al constructor de la clase padre (Usuario) y fuerza el rol "Admin"
        super(idUsuario, nombre, password, "Admin");
    }

    // Aplicando Polimorfismo: Sobrescribe el método abstracto
    @Override
    public String obtenerNivelAcceso() {
        return "[NIVEL 1] Acceso Total: Puede registrar, modificar, eliminar, ver reportes y gestionar empleados.";
    }
}
