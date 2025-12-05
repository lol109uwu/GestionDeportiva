package PRESENTACION;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import DATOS.reservaDb;

public class frmReporteReservas extends JFrame {

    private JPanel contentPane;
    private JTable tablaReporte;
    private DefaultTableModel modelo;

    public frmReporteReservas() {
        setTitle("Reporte Global de Reservas - Administrador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana, no la app
        setBounds(100, 100, 700, 500);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // TITULO
        JLabel lblTitulo = new JLabel("HISTORIAL COMPLETO DE RESERVAS");
        lblTitulo.setForeground(new Color(0, 51, 102));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(20, 20, 400, 30);
        contentPane.add(lblTitulo);

        // TABLA
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 70, 640, 370);
        contentPane.add(scrollPane);

        String[] columnas = {"Socio", "Instalación", "Fecha", "Hora Inicio", "Hora Fin"};
        modelo = new DefaultTableModel(null, columnas);
        tablaReporte = new JTable(modelo);
        tablaReporte.setRowHeight(25);
        tablaReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReporte.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaReporte.getTableHeader().setBackground(new Color(230,230,230));
        
        scrollPane.setViewportView(tablaReporte);

        cargarDatos();
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        // Llamamos al nuevo método de la base de datos
        Object[][] datos = reservaDb.obtenerReporteGlobal();
        
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
}