package DATOS;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import LOGICA.ClubException;

public class reservaDb {

   
    public static void guardarReserva(String nombreSocio, int idInstalacion, LocalDate fecha, int horaInicio, int duracion) throws ClubException {
      
        if (nombreSocio == null || nombreSocio.isEmpty()) throw new ClubException("Falta el socio.");

        int idSocio = obtenerIdSocioPorNombre(nombreSocio);
        if (idSocio == -1) throw new ClubException("El socio '" + nombreSocio + "' no existe en la base de datos.");

        int horaFinCalculada = horaInicio + duracion;
        if (horaFinCalculada > 23) throw new ClubException("Horario excede el cierre.");

     
        for (int h = horaInicio; h < horaFinCalculada; h++) {
            if (estaOcupado(idInstalacion, fecha, h)) {
                throw new ClubException("La cancha ya está ocupada a las " + h + ":00.");
            }
        }

        String sql = "INSERT INTO reserva_instalacion (id_instalacion, id_socio, fecha_reserva, hora_inicio, hora_fin) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            for (int h = horaInicio; h < horaFinCalculada; h++) {
                pst.setInt(1, idInstalacion);
                pst.setInt(2, idSocio);
                pst.setDate(3, Date.valueOf(fecha));
                pst.setTime(4, Time.valueOf(h + ":00:00"));
                pst.setTime(5, Time.valueOf((h + 1) + ":00:00"));
                pst.addBatch();
            }
            pst.executeBatch();
            System.out.println("✅ Reserva guardada en PostgreSQL");

        } catch (SQLException e) {
            if(e.getMessage().contains("ya está reservada")) throw new ClubException("Horario ocupado (BD).");
            e.printStackTrace();
            throw new ClubException("Error SQL: " + e.getMessage());
        }
    }


    public static boolean estaOcupado(int idInstalacion, LocalDate fecha, int hora) {
        String sql = "SELECT COUNT(*) FROM reserva_instalacion WHERE id_instalacion = ? AND fecha_reserva = ? AND hora_inicio = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idInstalacion);
            pst.setDate(2, Date.valueOf(fecha));
            pst.setTime(3, Time.valueOf(hora + ":00:00"));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    public static boolean estaOcupado(int nroCancha, int hora) {
        return estaOcupado(nroCancha, LocalDate.now(), hora);
    }

    
    public static String obtenerCliente(int idInstalacion, int hora) {
        LocalDate hoy = LocalDate.now();
        String sql = "SELECT s.nombre || ' ' || s.apellido FROM reserva_instalacion r " +
                     "JOIN socio s ON r.id_socio = s.id_socio " +
                     "WHERE r.id_instalacion = ? AND r.fecha_reserva = ? AND r.hora_inicio = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idInstalacion);
            pst.setDate(2, Date.valueOf(hoy));
            pst.setTime(3, Time.valueOf(hora + ":00:00"));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    public static void cancelarReserva(int idInstalacion, int hora) throws ClubException {
        LocalDate hoy = LocalDate.now();
        String sql = "DELETE FROM reserva_instalacion WHERE id_instalacion = ? AND fecha_reserva = ? AND hora_inicio = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idInstalacion);
            pst.setDate(2, Date.valueOf(hoy));
            pst.setTime(3, Time.valueOf(hora + ":00:00"));
            int filas = pst.executeUpdate();
            if (filas == 0) throw new ClubException("No se encontró reserva para borrar.");
        } catch (SQLException e) { throw new ClubException("Error SQL: " + e.getMessage()); }
    }
    

    public static void modificarReserva(int oldCancha, int oldHora, String newNombre, int newCancha, int newHora, int newDuracion) throws ClubException {
        cancelarReserva(oldCancha, oldHora);
        guardarReserva(newNombre, newCancha, LocalDate.now(), newHora, newDuracion);
    }

   
    private static int obtenerIdSocioPorNombre(String nombreBusqueda) {
        String sql = "SELECT id_socio FROM socio WHERE nombre ILIKE ? OR apellido ILIKE ? OR (nombre || ' ' || apellido) ILIKE ? LIMIT 1";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + nombreBusqueda + "%");
            pst.setString(2, "%" + nombreBusqueda + "%");
            pst.setString(3, "%" + nombreBusqueda + "%");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("id_socio");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
   
    public static Object[][] obtenerReporteGlobal() {
        List<Object[]> filas = new ArrayList<>();
        
      
        String sql = "SELECT s.nombre || ' ' || s.apellido AS socio, " +
                     "i.nombre AS instalacion, " +
                     "r.fecha_reserva, " +
                     "r.hora_inicio, " +
                     "r.hora_fin " +
                     "FROM reserva_instalacion r " +
                     "JOIN socio s ON r.id_socio = s.id_socio " +
                     "JOIN instalacion i ON r.id_instalacion = i.id_instalacion " +
                     "ORDER BY r.fecha_reserva DESC, r.hora_inicio ASC";

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                filas.add(new Object[] {
                    rs.getString("socio"),
                    rs.getString("instalacion"),
                    rs.getDate("fecha_reserva").toString(),
                    rs.getTime("hora_inicio").toString(),
                    rs.getTime("hora_fin").toString()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

       
        Object[][] matriz = new Object[filas.size()][5];
        for (int i = 0; i < filas.size(); i++) {
            matriz[i] = filas.get(i);
        }
        return matriz;
    }
}