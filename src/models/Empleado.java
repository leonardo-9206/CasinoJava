package models;

public class Empleado extends Usuario {
    //constructor
    public Empleado(String idUsuario, String nombre, String password) {
        //llamamos al constructor del padre y fijamos su rol como empleado
        super(idUsuario, nombre, password, "Empleado");
    }

    //mostramos sus permisos en terminal
    @Override
    public String obtenerNivelAcceso() {
        return "Acceso Parcial: Puede realizar ventas y hacer restock de inventario.";
    }
}
