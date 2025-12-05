package DATOS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    // --- DATOS DE CONEXIÓN A LA NUBE (NEON.TECH) ---
    
    // 1. URL: Host + Base de Datos + Seguridad SSL
    // Nota: Usamos 'dbgestionDeportiva' porque ya vimos en tu imagen que existe en Neon.
    private static final String URL = "jdbc:postgresql://ep-lucky-wildflower-ahgjk8x8-pooler.c-3.us-east-1.aws.neon.tech/dbgestionDeportiva?sslmode=require";
    
    // 2. USUARIO (El de Neon)
    private static final String USER = "neondb_owner"; 
    
    // 3. CONTRASEÑA (La de Neon)
    private static final String PASS = "npg_zoJX6Apail2s"; 

    public static Connection conectar() {
        Connection link = null;
        try {
            // Cargar el Driver de PostgreSQL
            Class.forName("org.postgresql.Driver");
            
            // Intentar establecer la conexión
            link = DriverManager.getConnection(URL, USER, PASS);
            
            // System.out.println("✅ Conexión Exitosa a Neon.tech"); // Descomenta para probar si conecta
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error Crítico: No se encontró el Driver de PostgreSQL.");
            System.err.println("   -> Asegúrate de haber agregado el archivo .jar al Build Path.");
        } catch (SQLException e) {
            System.err.println("❌ Error de Conexión: " + e.getMessage());
            System.err.println("   -> Verifica tu conexión a internet.");
            System.err.println("   -> Verifica que la base de datos 'dbgestionDeportiva' exista en Neon.");
        }
        return link;
    }
}