package models;

// Aplicando Herencia: Empleado "es un" Usuario
public class Empleado extends Usuario {

    public Empleado(String idUsuario, String nombre, String password) {
        // Llama al constructor de la clase padre (Usuario) y fuerza el rol "Empleado"
        super(idUsuario, nombre, password, "Empleado");
    }

    // Aplicando Polimorfismo: Sobrescribe el método abstracto
    @Override
    public String obtenerNivelAcceso() {
        return "[NIVEL 2] Acceso Parcial: Puede realizar ventas y hacer restock de inventario.";
    }
}
