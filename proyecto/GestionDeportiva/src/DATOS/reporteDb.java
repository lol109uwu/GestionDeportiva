package DATOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class reporteDb {

    // Método Genérico: Le pasas el nombre de la vista y te devuelve el Modelo para la Tabla
    public static DefaultTableModel cargarVista(String nombreVista) {
        DefaultTableModel modelo = new DefaultTableModel();
        
        // Validamos para evitar inyección SQL básica (solo permitimos nombres conocidos)
        if (!nombreVista.startsWith("vw_")) return modelo; 

        String sql = "SELECT * FROM " + nombreVista;

        try (Connection conn = Conexion.conectar();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            // 1. Obtener nombres de columnas dinámicamente
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();

            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column).toUpperCase());
            }
            modelo.setColumnIdentifiers(columnNames);

            // 2. Obtener datos fila por fila
            while (rs.next()) {
                Vector<Object> rowData = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.add(rs.getObject(i));
                }
                modelo.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelo;
    }
}