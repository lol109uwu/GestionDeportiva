package LOGICA;

public class Socio extends Persona { 

    private String apellido;
    private String dni;
   
    private int idSocio; 

    public Socio(String nombre, String apellido, String dni) {
        super(nombre); // Hereda 
        this.apellido = apellido;
        this.dni = dni;
    }
    
    
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public int getIdSocio() { return idSocio; }
    public void setIdSocio(int id) { this.idSocio = id; }

    @Override
    public String obtenerTipo() {
        return "Socio - DNI: " + dni; 
    }
    
   
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
}