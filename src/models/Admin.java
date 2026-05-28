package models;

public class Admin extends Usuario {
    //constructor
    public Admin(String idUsuario, String nombre, String password) {
        //llamamos al constructor de usuario pero fijamos su rol como admin
        super(idUsuario, nombre, password, "Admin");
    }

    //metodo abstracto, solo es para mostrar en terminal sus permisos
    @Override
    public String obtenerNivelAcceso() {
        return "Acceso Total: Puede registrar, modificar, eliminar, ver reportes y gestionar empleados.";
    }
}
