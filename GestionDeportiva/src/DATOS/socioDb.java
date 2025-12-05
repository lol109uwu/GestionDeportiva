package DATOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import LOGICA.ClubException;
import LOGICA.Socio;

public class socioDb {

    // --- LEER TODOS (Para la tabla del Admin) ---
    public static List<Socio> obtenerTodos() {
        List<Socio> lista = new ArrayList<>();
        String sql = "SELECT nombre, apellido, dni FROM socio ORDER BY id_socio DESC";

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lista.add(new Socio(
                    rs.getString("nombre"), 
                    rs.getString("apellido"), 
                    rs.getString("dni")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- VALIDAR EXISTENCIA ---
    // Busca si el nombre, apellido o DNI existe (Búsqueda flexible)
    public static boolean verificarExiste(String datoIngresado) {
        if (datoIngresado == null) return false;
        String busqueda = datoIngresado.trim();

        String sql = "SELECT COUNT(*) FROM socio WHERE dni = ? OR nombre ILIKE ? OR (nombre || ' ' || apellido) ILIKE ?";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, busqueda);
            pst.setString(2, busqueda);
            pst.setString(3, "%" + busqueda + "%"); 

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // --- AGREGAR (INSERT) ---
    public static void agregarSocio(String nombre, String apellido, String dni) throws ClubException {
        // Validación previa
        if (verificarExiste(dni)) {
            throw new ClubException("El DNI " + dni + " ya está registrado en la base de datos.");
        }

        String sql = "INSERT INTO socio (nombre, apellido, dni, fecha_alta) VALUES (?, ?, ?, CURRENT_DATE)";

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, dni);
            
            pst.executeUpdate();
            System.out.println("✅ Socio guardado en PostgreSQL: " + nombre);

        } catch (SQLException e) {
            throw new ClubException("Error al guardar en BD: " + e.getMessage());
        }
    }

    // --- ELIMINAR (DELETE) CORREGIDO ---
    public static void eliminarSocio(String dni) throws ClubException {
        
        // 1. PRIMERO: Borramos el usuario de login asociado para evitar error de FK
        usuarioDb.eliminarUsuario(dni);

        // 2. SEGUNDO: Intentamos borrar al socio
        String sql = "DELETE FROM socio WHERE dni = ?";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, dni);
            int filas = pst.executeUpdate();
            
            if (filas == 0) {
                throw new ClubException("No se encontró ese DNI en la base de datos.");
            }

        } catch (SQLException e) {
            // Capturamos si hay otro error, por ejemplo, si tiene RESERVAS
            // El mensaje de error de postgres suele contener el nombre de la tabla que causa el conflicto
            if (e.getMessage().contains("reserva_instalacion")) {
                throw new ClubException("❌ No se puede eliminar: El socio tiene RESERVAS o PAGOS registrados.\n" +
                                        "Debe eliminar primero sus registros asociados.");
            }
            // Cualquier otro error SQL
            throw new ClubException("Error al eliminar socio: " + e.getMessage());
        }
    }
}