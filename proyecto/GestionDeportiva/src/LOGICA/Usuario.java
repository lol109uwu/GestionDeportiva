package LOGICA;

public class Usuario {
    private String username;
    private String password;
    private String rol; // "ADMIN" o "SOCIO"
    private String nombreReal; // Para llenar el formulario automáticamente

    public Usuario(String username, String password, String rol, String nombreReal) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.nombreReal = nombreReal;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public String getNombreReal() { return nombreReal; }
}