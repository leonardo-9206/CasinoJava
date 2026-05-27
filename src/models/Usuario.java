package models;

// Hacemos la clase abstracta para cumplir con el requisito de Herencia y Polimorfismo
public abstract class Usuario {
    
    // Protected permite que las clases hijas (Admin, Empleado) hereden estas variables
    protected String idUsuario;
    protected String nombre;
    protected String password;
    protected String rol;

    public Usuario(String idUsuario, String nombre, String password, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    // ==========================================
    // REQUISITO CUMPLIDO: POLIMORFISMO
    // ==========================================
    // Este método obliga a todas las clases hijas a tener su propia versión de los permisos
    public abstract String obtenerNivelAcceso();
}
