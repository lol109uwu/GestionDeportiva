package LOGICA;

public abstract class Persona {
    protected String nombre; // Encapsulamiento 

    public Persona(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Polimorfismo
    public abstract String obtenerTipo();
}