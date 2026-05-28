package models;

public class Cliente extends Usuario {
    //constructor
    public Cliente(String idUsuario, String nombre, String password) {
        //llamamos al constructor del padre y fijamos su rol como cliente
        super(idUsuario, nombre, password, "Cliente");
    }

    //muestra en terminal sus permisos
    @Override
    public String obtenerNivelAcceso() {
        return "Acceso de Cliente: Solo puede ver su historial de compras y su cuenta personal.";
    }
}
