package DATOS;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import LOGICA.Usuario;

public class usuarioDb {

    // --- AUTENTICAR (LOGIN CON SQL) ---
    public static Usuario autenticar(String user, String pass) {
        String passEncriptada = encriptar(pass);
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, user);
            pst.setString(2, passEncriptada);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Recuperar ID de socio para saber el nombre real
                int idSocio = rs.getInt("id_socio");
                String nombreReal = obtenerNombreSocio(idSocio);
                
                // Determinar Rol (Si tiene id_staff es ADMIN, si tiene id_socio es SOCIO)
                String rol = (rs.getObject("id_staff") != null || user.equals("admin")) ? "ADMIN" : "SOCIO";
                
                if (nombreReal == null) nombreReal = user; 
                
                return new Usuario(user, "********", rol, nombreReal);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- AGREGAR USUARIO (INSERT) ---
    public static void agregarUsuario(String user, String pass, String rol, String nombreReal) {
        int idSocio = obtenerIdSocioPorDNI(user); 
        
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena, email, id_socio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, user);
            pst.setString(2, encriptar(pass));
            pst.setString(3, "user_" + user + "@club.com"); // Email generado automáticamente
            
            if (idSocio != -1) pst.setInt(4, idSocio);
            else pst.setNull(4, java.sql.Types.INTEGER);
            
            pst.executeUpdate();
            System.out.println("✅ Usuario guardado en PostgreSQL");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- CAMBIAR CONTRASEÑA (UPDATE) ---
    public static boolean cambiarContrasena(String username, String nuevaPass) {
        String sql = "UPDATE usuario SET contrasena = ? WHERE nombre_usuario = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, encriptar(nuevaPass));
            pst.setString(2, username);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // --- ELIMINAR USUARIO (DELETE) ---
    public static void eliminarUsuario(String username) {
        String sql = "DELETE FROM usuario WHERE nombre_usuario = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- BUSCAR Y VERIFICAR ---
    public static Usuario buscarUsuario(String username) {
        if (existeUsuario(username)) return new Usuario(username, "oculta", "SOCIO", "Usuario BD");
        return null;
    }
    
    public static boolean existeUsuario(String username) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // --- REPARACIÓN AVANZADA (Sincroniza y crea faltantes) ---
    public static int repararUsuariosAntiguos() {
        int cambios = 0;
        try (Connection conn = Conexion.conectar()) {
            
            // 1. Corregir nombres antiguos (ej: juanp -> DNI)
            String sqlUpdate = "UPDATE usuario SET nombre_usuario = s.dni FROM socio s WHERE usuario.id_socio = s.id_socio AND usuario.nombre_usuario != s.dni";
            PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate);
            cambios += pstUpdate.executeUpdate();

            // 2. Crear usuarios para socios sin cuenta
            String passDefault = encriptar("1234"); 
            String sqlInsert = "INSERT INTO usuario (nombre_usuario, contrasena, email, id_socio) " +
                               "SELECT s.dni, '" + passDefault + "', 'user_' || s.dni || '@club.com', s.id_socio " +
                               "FROM socio s " +
                               "WHERE NOT EXISTS (SELECT 1 FROM usuario u WHERE u.id_socio = s.id_socio)";
            
            PreparedStatement pstInsert = conn.prepareStatement(sqlInsert);
            cambios += pstInsert.executeUpdate();
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            return -1; 
        }
        return cambios;
    }

    // --- HELPERS PRIVADOS ---
    private static int obtenerIdSocioPorDNI(String dni) {
        String sql = "SELECT id_socio FROM socio WHERE dni = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, dni);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("id_socio");
        } catch (SQLException e) {}
        return -1;
    }
    
    private static String obtenerNombreSocio(int id) {
        String sql = "SELECT nombre || ' ' || apellido as completo FROM socio WHERE id_socio = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("completo");
        } catch (SQLException e) {}
        return null;
    }

    private static String encriptar(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encriptando", e);
        }
    }
}