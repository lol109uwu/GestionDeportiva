package LOGICA;

import java.time.LocalDate;
public class Reserva {
    private String nombreSocio;
    private int numeroCancha;
    private LocalDate fecha; 
    private int horaInicio;
    private int horaFin;

    public Reserva(String nombreSocio, int numeroCancha, LocalDate fecha, int horaInicio, int horaFin) {
        this.nombreSocio = nombreSocio;
        this.numeroCancha = numeroCancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getNombreSocio() { return nombreSocio; }
    public int getNumeroCancha() { return numeroCancha; }
    public LocalDate getFecha() { return fecha; }
    public int getHoraInicio() { return horaInicio; }
    public int getHoraFin() { return horaFin; }
}