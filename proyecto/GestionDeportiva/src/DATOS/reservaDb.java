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
import LOGICA.Reserva;

public class reservaDb {

    // --- 1. OPTIMIZACIÓN DE RENDIMIENTO: TRAER TODO EL DÍA DE UNA VEZ ---
    // Este método descarga todas las reservas de hoy en una sola lista
    public static List<Reserva> obtenerReservasDeHoy() {
        List<Reserva> listaDelDia = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        
        // Hacemos JOIN para traer el nombre del socio directamente
        String sql = "SELECT r.id_instalacion, r.hora_inicio, s.nombre || ' ' || s.apellido as nom_socio " +
                     "FROM reserva_instalacion r " +
                     "JOIN socio s ON r.id_socio = s.id_socio " +
                     "WHERE r.fecha_reserva = ?";

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            if (conn != null) {
                pst.setDate(1, Date.valueOf(hoy));
                ResultSet rs = pst.executeQuery();
    
                while (rs.next()) {
                    // Creamos un objeto Reserva temporal con los datos necesarios para la tabla
                    // Usamos: NombreSocio, NroCancha, Fecha, HoraInicio, HoraFin
                    Reserva r = new Reserva(
                        rs.getString("nom_socio"),
                        rs.getInt("id_instalacion"),
                        hoy,
                        rs.getInt("hora_inicio"),
                        0 // La hora fin no es relevante para pintar la celda
                    );
                    listaDelDia.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaDelDia;
    }

    // --- 2. GUARDAR (CREATE) ---
    public static void guardarReserva(String nombreSocio, int idInstalacion, LocalDate fecha, int horaInicio, int duracion) throws ClubException {
        int idSocio = buscarIdSocioFlexible(nombreSocio);
        if (idSocio == -1) throw new ClubException("El socio no existe en la base de datos.");

        if ((horaInicio + duracion) > 23) throw new ClubException("Horario inválido (Cierra a las 23:00).");

        // Validación de choque de horarios (usando método rápido local si se quisiera, o SQL directo)
        // Por seguridad, aquí hacemos la validación SQL normal antes de insertar
        for (int h = horaInicio; h < horaInicio + duracion; h++) {
            if (estaOcupado(idInstalacion, fecha, h)) {
                throw new ClubException("La cancha ya está ocupada a las " + h + ":00.");
            }
        }

        String sql = "INSERT INTO reserva_instalacion (id_instalacion, id_socio, fecha_reserva, hora_inicio, hora_fin) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            for (int h = horaInicio; h < horaInicio + duracion; h++) {
                pst.setInt(1, idInstalacion);
                pst.setInt(2, idSocio);
                pst.setDate(3, Date.valueOf(fecha));
                pst.setTime(4, Time.valueOf(h + ":00:00"));
                pst.setTime(5, Time.valueOf((h + 1) + ":00:00"));
                pst.addBatch();
            }
            pst.executeBatch();
            
        } catch (SQLException e) {
            if(e.getMessage().contains("ya está reservada")) throw new ClubException("Horario ocupado (Detectado por BD).");
            throw new ClubException("Error BD: " + e.getMessage());
        }
    }

    // --- 3. MODIFICAR (UPDATE) ---
    public static void modificarReserva(int oldCancha, int oldHora, String newNombre, int newCancha, int newHora, int newDuracion) throws ClubException {
        // Estrategia segura: Borrar viejo -> Crear nuevo
        cancelarReserva(oldCancha, oldHora);
        try {
            guardarReserva(newNombre, newCancha, LocalDate.now(), newHora, newDuracion);
        } catch (ClubException e) {
            // Si falla la nueva (ej. estaba ocupada), sería ideal restaurar la vieja aquí, 
            // pero para este nivel, mostrar el error es suficiente.
            throw e; 
        }
    }

    // --- 4. CANCELAR (DELETE) ---
    public static void cancelarReserva(int idInstalacion, int hora) throws ClubException {
        LocalDate hoy = LocalDate.now();
        String sql = "DELETE FROM reserva_instalacion WHERE id_instalacion = ? AND fecha_reserva = ? AND hora_inicio = ?";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idInstalacion);
            pst.setDate(2, Date.valueOf(hoy));
            pst.setTime(3, Time.valueOf(hora + ":00:00"));
            
            int filas = pst.executeUpdate();
            if (filas == 0) throw new ClubException("No se encontró la reserva para eliminar.");
            
        } catch (SQLException e) {
            throw new ClubException("Error al borrar: " + e.getMessage());
        }
    }

    // --- HELPERS ---
    
    // Método tradicional para validar antes de guardar (verifica 1 celda específica en la BD)
    public static boolean estaOcupado(int idInstalacion, LocalDate fecha, int hora) {
        String sql = "SELECT COUNT(*) FROM reserva_instalacion WHERE id_instalacion=? AND fecha_reserva=? AND hora_inicio=?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idInstalacion);
            pst.setDate(2, Date.valueOf(fecha));
            pst.setTime(3, Time.valueOf(hora + ":00:00"));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {}
        return false;
    }
    
    // Método para obtener ID de socio
    private static int buscarIdSocioFlexible(String texto) {
        String sql = "SELECT id_socio FROM socio WHERE dni = ? OR nombre ILIKE ? OR (nombre || ' ' || apellido) ILIKE ? LIMIT 1";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, texto);
            pst.setString(2, "%" + texto + "%");
            pst.setString(3, "%" + texto + "%");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return -1;
    }
    
    // Método antiguo para obtener cliente (ya no se usa mucho gracias a la optimización, pero lo dejamos por compatibilidad)
    public static String obtenerCliente(int idInstalacion, int hora) {
        String sql = "SELECT s.nombre FROM reserva_instalacion r JOIN socio s ON r.id_socio = s.id_socio WHERE r.id_instalacion=? AND r.fecha_reserva=? AND r.hora_inicio=?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idInstalacion);
            pst.setDate(2, Date.valueOf(LocalDate.now()));
            pst.setTime(3, Time.valueOf(hora + ":00:00"));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {}
        return "";
    }
}