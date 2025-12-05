package DATOS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    // --- CORRECCIÓN: CAMBIAMOS 'dbgestionDeportiva' POR 'neondb' ---
    // Tus tablas están guardadas en 'neondb', así que Java debe buscar ahí.
    private static final String URL = "jdbc:postgresql://ep-lucky-wildflower-ahgjk8x8-pooler.c-3.us-east-1.aws.neon.tech/neondb?sslmode=require";
    
    private static final String USER = "neondb_owner"; 
    
    // Tu contraseña (la que tienes en Neon)
    private static final String PASS = "npg_zoJX6Apail2s"; 

    public static Connection conectar() {
        Connection link = null;
        try {
            Class.forName("org.postgresql.Driver");
            link = DriverManager.getConnection(URL, USER, PASS);
            // System.out.println("✅ Conexión a Neon (neondb) Exitosa");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: No se encontró el Driver JDBC.");
        } catch (SQLException e) {
            System.err.println("❌ Error de Conexión: " + e.getMessage());
        }
        return link;
    }
}