package PRESENTACION;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import DATOS.socioDb;
import DATOS.usuarioDb;
import LOGICA.ClubException;
import LOGICA.Socio;
import LOGICA.Usuario;

public class frmAdmin extends JFrame {

    private JPanel contentPane;
    private JTable tablaSocios;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtApellido, txtDni;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frmAdmin frame = new frmAdmin();
                frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public frmAdmin() {
        setTitle("Panel de Administrador - Gestión Integral");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 650);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240)); 
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // --- HEADER ---
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(0, 51, 102)); 
        panelHeader.setBounds(0, 0, 934, 60);
        panelHeader.setLayout(null);
        contentPane.add(panelHeader);

        JLabel lblTitulo = new JLabel("PANEL DE CONTROL (ADMIN)");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBounds(30, 15, 500, 30);
        panelHeader.add(lblTitulo);
        
        // 1. CERRAR SESIÓN
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(220, 20, 60)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBounds(750, 15, 150, 30);
        btnLogout.addActionListener(e -> cerrarSesion());
        colocarIcono(btnLogout, "IMAGENES/logout.png", 20); // Icono pequeño
        panelHeader.add(btnLogout);

        // --- FORMULARIO IZQUIERDA ---
        JPanel panelForm = new JPanel();
        panelForm.setBackground(Color.WHITE);
        panelForm.setBounds(20, 80, 300, 500); 
        panelForm.setBorder(new TitledBorder(new LineBorder(Color.GRAY), "Registro de Socio", TitledBorder.LEADING, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
        panelForm.setLayout(null);
        contentPane.add(panelForm);

        JLabel lblNom = new JLabel("Nombre (*):");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNom.setBounds(20, 30, 100, 20);
        panelForm.add(lblNom);
        txtNombre = new JTextField();
        txtNombre.setBounds(20, 50, 260, 30);
        panelForm.add(txtNombre);

        JLabel lblApe = new JLabel("Apellido:");
        lblApe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblApe.setBounds(20, 90, 100, 20);
        panelForm.add(lblApe);
        txtApellido = new JTextField();
        txtApellido.setBounds(20, 110, 260, 30);
        panelForm.add(txtApellido);

        JLabel lblDni = new JLabel("DNI / Cédula (*):");
        lblDni.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDni.setBounds(20, 150, 100, 20);
        panelForm.add(lblDni);
        txtDni = new JTextField();
        txtDni.setBounds(20, 170, 260, 30);
        panelForm.add(txtDni);

        // 2. GUARDAR SOCIO
        JButton btnAgregar = new JButton("GUARDAR SOCIO");
        btnAgregar.setBackground(new Color(34, 139, 34)); // Verde
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregar.setBounds(20, 220, 260, 45);
        btnAgregar.addActionListener(e -> agregar());
        colocarIcono(btnAgregar, "IMAGENES/guardar.png", 24);
        panelForm.add(btnAgregar);

        // SEPARADOR
        JLabel lblHerramientas = new JLabel("--- REPORTES Y VISTAS ---");
        lblHerramientas.setForeground(Color.GRAY);
        lblHerramientas.setBounds(75, 280, 200, 20);
        panelForm.add(lblHerramientas);

        // 3. HISTORIAL (FECHAS)
        JButton btnHistorial = new JButton("HISTORIAL (FECHAS)");
        btnHistorial.setToolTipText("Ver lista simple de reservas con fecha y hora");
        btnHistorial.setBackground(new Color(70, 130, 180)); // Azul acero
        btnHistorial.setForeground(Color.WHITE);
        btnHistorial.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHistorial.setBounds(20, 310, 260, 40);
        btnHistorial.addActionListener(e -> {
            frmReporteReservas historial = new frmReporteReservas();
            historial.setVisible(true);
        });
        colocarIcono(btnHistorial, "IMAGENES/historial.png", 24);
        panelForm.add(btnHistorial);

        // 4. VISTAS SQL
        JButton btnVistas = new JButton("VISTAS SQL (BD)");
        btnVistas.setToolTipText("Consultar las 5 vistas de la BD");
        btnVistas.setBackground(new Color(0, 102, 102)); // Verde petróleo
        btnVistas.setForeground(Color.WHITE);
        btnVistas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVistas.setBounds(20, 360, 260, 40);
        btnVistas.addActionListener(e -> {
            frmReportes reporteSQL = new frmReportes();
            reporteSQL.setVisible(true);
        });
        colocarIcono(btnVistas, "IMAGENES/db_sql.png", 24);
        panelForm.add(btnVistas);

        // 5. REPARAR USUARIOS
        JButton btnReparar = new JButton("Reparar Usuarios");
        btnReparar.setBackground(Color.DARK_GRAY);
        btnReparar.setForeground(Color.WHITE);
        btnReparar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReparar.setBounds(20, 440, 260, 35);
        btnReparar.addActionListener(e -> repararDatos());
        colocarIcono(btnReparar, "IMAGENES/reparar.png", 20);
        panelForm.add(btnReparar);

        // --- TABLA DERECHA ---
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(340, 80, 580, 430);
        scrollPane.setBorder(new TitledBorder(new LineBorder(Color.GRAY), "Listado de Socios Activos", TitledBorder.LEADING, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
        contentPane.add(scrollPane);

        modelo = new DefaultTableModel(null, new String[]{"Nombre", "Apellido", "DNI (Usuario)"});
        tablaSocios = new JTable(modelo);
        tablaSocios.setRowHeight(25);
        tablaSocios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scrollPane.setViewportView(tablaSocios);

        // 6. RESETEAR CONTRASEÑA
        JButton btnResetear = new JButton("RESETEAR CLAVE");
        btnResetear.setBackground(new Color(255, 140, 0)); // Naranja
        btnResetear.setForeground(Color.WHITE);
        btnResetear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnResetear.setBounds(340, 520, 200, 45);
        btnResetear.addActionListener(e -> resetearClave());
        colocarIcono(btnResetear, "IMAGENES/reset_pass.png", 24);
        contentPane.add(btnResetear);

        // 7. ELIMINAR SOCIO
        JButton btnEliminar = new JButton("ELIMINAR SOCIO");
        btnEliminar.setBackground(new Color(204, 0, 0)); // Rojo
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setBounds(720, 520, 200, 45);
        btnEliminar.addActionListener(e -> eliminar());
        colocarIcono(btnEliminar, "IMAGENES/eliminar.png", 24);
        contentPane.add(btnEliminar);

        actualizarTabla();
    }

    // --- MÉTODO PARA PONER ICONOS ---
    private void colocarIcono(JButton boton, String ruta, int tamano) {
        try {
            // Intento 1: Directo (Eclipse)
            String rutaDirecta = "src/" + ruta; 
            File archivo = new File(rutaDirecta);
            if (archivo.exists()) {
                ImageIcon icon = new ImageIcon(rutaDirecta);
                Image img = icon.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(img));
                return;
            }
            // Intento 2: Recurso (JAR)
            URL url = getClass().getResource("/" + ruta);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}
    }

    // --- MÉTODOS DE LÓGICA (Sin cambios) ---

    private void cerrarSesion() {
        this.dispose();
        new frmLogin().setVisible(true);
    }

    private void repararDatos() {
        int arreglados = usuarioDb.repararUsuariosAntiguos();
        if (arreglados >= 0) {
            JOptionPane.showMessageDialog(this, "✅ Mantenimiento exitoso.\n" + 
                "Se actualizaron " + arreglados + " socios.");
        } else {
            JOptionPane.showMessageDialog(this, "Error de conexión.");
        }
    }

    private void agregar() {
        try {
            String nom = txtNombre.getText().trim();
            String ape = txtApellido.getText().trim();
            String dni = txtDni.getText().trim();

            if (nom.isEmpty()) throw new ClubException("⚠️ El Nombre es obligatorio.");
            if (dni.isEmpty()) throw new ClubException("⚠️ El DNI es obligatorio.");

            socioDb.agregarSocio(nom, ape, dni);
            String passAleatoria = generarContrasena();
            usuarioDb.agregarUsuario(dni, passAleatoria, "SOCIO", nom + " " + ape);

            actualizarTabla();
            
            JOptionPane.showMessageDialog(this, "✅ Socio registrado.\n\n" +
                                                "Credenciales:\n" +
                                                "👤 Usuario: " + dni + "\n" +
                                                "🔑 Contraseña: " + passAleatoria);
            
            txtNombre.setText(""); txtApellido.setText(""); txtDni.setText("");

        } catch (ClubException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetearClave() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un socio.");
            return;
        }

        String dniUsuario = (String) tablaSocios.getValueAt(fila, 2);
        String nombreSocio = (String) tablaSocios.getValueAt(fila, 0);

        Usuario u = usuarioDb.buscarUsuario(dniUsuario);

        if (u == null) {
            int fix = JOptionPane.showConfirmDialog(this, 
                "El socio no tiene usuario vinculado.\n¿Crearlo ahora?", "Reparar", JOptionPane.YES_NO_OPTION);
            
            if (fix == JOptionPane.YES_OPTION) {
                usuarioDb.repararUsuariosAntiguos();
                u = usuarioDb.buscarUsuario(dniUsuario);
                if (u == null) return; 
            } else {
                return;
            }
        }

        int opcion = JOptionPane.showConfirmDialog(this, 
                "Socio: " + nombreSocio + "\n¿Generar NUEVA contraseña?", 
                "Resetear", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            String nuevaPass = generarContrasena();
            usuarioDb.cambiarContrasena(dniUsuario, nuevaPass);
            JOptionPane.showMessageDialog(this, "✅ Contraseña reseteada.\nNueva Clave: " + nuevaPass);
        }
    }

    private String generarContrasena() {
        String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder pass = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) pass.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        return pass.toString();
    }

    private void eliminar() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio.");
            return;
        }
        
        String dni = (String) tablaSocios.getValueAt(fila, 2);
        
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar socio?") == JOptionPane.YES_OPTION) {
            try {
                socioDb.eliminarSocio(dni);
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Socio eliminado.");
            } catch (ClubException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void actualizarTabla() {
        modelo.setRowCount(0);
        List<Socio> lista = socioDb.obtenerTodos();
        for (Socio s : lista) {
            modelo.addRow(new Object[]{s.getNombre(), s.getApellido(), s.getDni()});
        }
    }
}