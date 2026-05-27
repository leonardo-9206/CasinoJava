package models;

// Aplicando Herencia: Cliente "es un" Usuario
public class Cliente extends Usuario {

    public Cliente(String idUsuario, String nombre, String password) {
        // Llama al constructor de la clase padre (Usuario) y fuerza el rol "Cliente"
        super(idUsuario, nombre, password, "Cliente");
    }

    // Aplicando Polimorfismo: Sobrescribe el método abstracto
    @Override
    public String obtenerNivelAcceso() {
        return "[NIVEL 3] Acceso de Cliente: Solo puede ver su historial de compras y su cuenta personal.";
    }
}
