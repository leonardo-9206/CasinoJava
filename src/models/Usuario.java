package models;

public abstract class Usuario {
    //protected para que se puedan heredar los atributos
    protected String idUsuario;
    protected String nombre;
    protected String password;
    protected String rol;

    //constructor
    public Usuario(String idUsuario, String nombre, String password, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    //setters y getters
    public String getIdUsuario(){
        return idUsuario; }

    public void setIdUsuario(String idUsuario){
        this.idUsuario = idUsuario; }
    
    public String getNombre(){
        return nombre;}

    public void setNombre(String nombre){
        this.nombre = nombre;}
    
    public String getPassword(){
        return password;}

    public void setPassword(String password){
        this.password = password;}
    
    public String getRol(){
        return rol; }

    public void setRol(String rol){
        this.rol = rol; }

    //para imprimir permisos en terminal
    public abstract String obtenerNivelAcceso();
}
