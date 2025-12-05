package PRESENTACION;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import DATOS.reporteDb;

public class frmReportes extends JFrame {

    private JPanel contentPane;
    private JTable tablaReporte;
    private JComboBox<String> cmbVistas;

    // Nombres amigables para el usuario
    private final String[] OPCIONES = {
        "Seleccione un Reporte...",
        "1. Socios Activos",
        "2. Membresías Vigentes",
        "3. Cuotas Pendientes",
        "4. Detalle de Reservas",
        "5. Ocupación por Cancha"
    };

    // Nombres reales en la Base de Datos (Deben coincidir en orden)
    private final String[] VISTAS_BD = {
        "",
        "vw_socios_activos",
        "vw_membresias_vigentes",
        "vw_cuotas_pendientes",
        "vw_reservas_detalle",
        "vw_ocupacion_horaria"
    };

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frmReportes frame = new frmReportes();
                frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public frmReportes() {
        setTitle("Sistema de Reportes y Vistas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 600);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // HEADER
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(0, 102, 102)); // Verde petróleo
        panelHeader.setBounds(0, 0, 934, 70);
        panelHeader.setLayout(null);
        contentPane.add(panelHeader);

        JLabel lblTitulo = new JLabel("VISOR DE REPORTES (BASE DE DATOS)");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBounds(30, 20, 500, 30);
        panelHeader.add(lblTitulo);

        // SELECTOR DE VISTAS
        JLabel lblSel = new JLabel("Seleccione la Vista a consultar:");
        lblSel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSel.setBounds(30, 90, 250, 20);
        contentPane.add(lblSel);

        cmbVistas = new JComboBox<>(OPCIONES);
        cmbVistas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbVistas.setBounds(30, 120, 300, 40);
        contentPane.add(cmbVistas);

        JButton btnConsultar = new JButton("🔍 CONSULTAR VISTA");
        btnConsultar.setBackground(new Color(0, 102, 204));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConsultar.setBounds(340, 120, 180, 40);
        btnConsultar.addActionListener(e -> cargarDatos());
        contentPane.add(btnConsultar);

        // TABLA DE RESULTADOS
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 180, 880, 360);
        scrollPane.setBorder(new TitledBorder(new LineBorder(Color.GRAY), "Resultados de la Consulta SQL", TitledBorder.LEADING, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
        contentPane.add(scrollPane);

        tablaReporte = new JTable();
        tablaReporte.setRowHeight(25);
        tablaReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scrollPane.setViewportView(tablaReporte);
    }

    private void cargarDatos() {
        int index = cmbVistas.getSelectedIndex();
        
        if (index <= 0) return; // Si no seleccionó nada

        String vistaSQL = VISTAS_BD[index];
        
        // Llamada a la capa de DATOS (Mágica y dinámica)
        DefaultTableModel modeloNuevo = reporteDb.cargarVista(vistaSQL);
        tablaReporte.setModel(modeloNuevo);
    }
}