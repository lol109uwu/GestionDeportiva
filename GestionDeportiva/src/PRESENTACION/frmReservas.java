package PRESENTACION;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalDate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DATOS.reservaDb;
import DATOS.usuarioDb;
import LOGICA.ClubException;
import LOGICA.Usuario;

public class frmReservas extends JFrame {

    private JLayeredPane layeredPane; 
    private JTextField txtSocio;
    private JComboBox<String> cmbInstalaciones;
    private JComboBox<String> cmbHoraInicio;
    private JSpinner spinDuracion;
    private JTable tablaMatriz;
    private DefaultTableModel modeloMatriz;
    
    private Usuario usuarioActual;

    private final String[] NOMBRES_INSTALACIONES = {
        "Tenis 1", "Tenis 2", "Fútbol", "Piscina O.", "Tenis 3", "Gimnasio"
    };

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // Usuario de prueba para diseño
                frmReservas frame = new frmReservas(new Usuario("test", "123", "SOCIO", "Usuario Test"));
                frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public frmReservas(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("Bienvenido " + usuario.getNombreReal());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
        
        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);
        layeredPane.setLayout(null);

        // --- CAPA 1: FONDO ---
        JLabel lblFondo = new JLabel("");
        lblFondo.setBounds(0, 0, 1200, 700);
        cargarImagen(lblFondo, "IMAGENES/fondoreserva.png"); // Usa el método robusto
        layeredPane.add(lblFondo, JLayeredPane.DEFAULT_LAYER);

        // --- HEADER ---
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(0, 51, 102, 220)); // Azul transparente
        panelHeader.setBounds(0, 0, 1184, 60);
        panelHeader.setLayout(null);
        layeredPane.add(panelHeader, JLayeredPane.PALETTE_LAYER);

        JLabel lblTitulo = new JLabel("PANEL DE RESERVAS");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setBounds(30, 15, 400, 30);
        panelHeader.add(lblTitulo);

        // Botón Cambiar Clave
        JButton btnCambiarPass = new JButton("Cambiar Clave");
        btnCambiarPass.setBackground(new Color(255, 165, 0)); 
        btnCambiarPass.setForeground(Color.WHITE);
        btnCambiarPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCambiarPass.setBounds(850, 15, 150, 30);
        btnCambiarPass.addActionListener(e -> cambiarContrasena());
        colocarIcono(btnCambiarPass, "IMAGENES/clave.png", 20); // Icono
        panelHeader.add(btnCambiarPass);

        // Botón Salir
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(128, 128, 128));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBounds(1010, 15, 160, 30);
        btnLogout.addActionListener(e -> {
            this.dispose();
            new frmLogin().setVisible(true);
        });
        colocarIcono(btnLogout, "IMAGENES/logout.png", 20); // Icono
        panelHeader.add(btnLogout);

        // --- FORMULARIO IZQUIERDA ---
        JPanel panelForm = new JPanel();
        panelForm.setBackground(new Color(255, 255, 255, 230)); // Blanco transparente
        panelForm.setBounds(20, 80, 340, 550);
        panelForm.setBorder(new TitledBorder(new LineBorder(Color.GRAY), "Gestionar Reserva", TitledBorder.LEADING, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
        panelForm.setLayout(null);
        layeredPane.add(panelForm, JLayeredPane.PALETTE_LAYER);

        // 1. Socio
        JLabel lblSocio = new JLabel("1. Socio:");
        lblSocio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSocio.setBounds(20, 30, 150, 20);
        panelForm.add(lblSocio);

        txtSocio = new JTextField();
        txtSocio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSocio.setBounds(20, 50, 290, 30);
        txtSocio.setText(usuarioActual.getNombreReal()); 
        txtSocio.setEditable(false); 
        txtSocio.setBackground(new Color(230, 230, 250)); 
        panelForm.add(txtSocio);

        // 2. Instalación
        JLabel lblCancha = new JLabel("2. Instalación:");
        lblCancha.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCancha.setBounds(20, 100, 150, 20);
        panelForm.add(lblCancha);

        cmbInstalaciones = new JComboBox<>(NOMBRES_INSTALACIONES);
        cmbInstalaciones.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cmbInstalaciones.setBounds(20, 120, 290, 35);
        panelForm.add(cmbInstalaciones);

        // 3. Hora
        JLabel lblHora = new JLabel("3. Hora Inicio:");
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHora.setBounds(20, 170, 150, 20);
        panelForm.add(lblHora);

        cmbHoraInicio = new JComboBox<>();
        cmbHoraInicio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cmbHoraInicio.setBounds(20, 190, 130, 35);
        cargarHoras(); 
        panelForm.add(cmbHoraInicio);

        // 4. Duración
        JLabel lblDur = new JLabel("Duración (h):");
        lblDur.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDur.setBounds(170, 170, 100, 20);
        panelForm.add(lblDur);

        spinDuracion = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        spinDuracion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        spinDuracion.setBounds(170, 190, 80, 35);
        panelForm.add(spinDuracion);

        // Botón Crear
        JButton btnReservar = new JButton("CONFIRMAR NUEVA");
        btnReservar.addActionListener(e -> realizarReserva());
        btnReservar.setBackground(new Color(0, 102, 204));
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReservar.setBounds(20, 260, 290, 50);
        colocarIcono(btnReservar, "IMAGENES/guardar.png", 24);
        panelForm.add(btnReservar);

        // Botón Modificar
        JButton btnModificar = new JButton("CAMBIAR HORA/LUGAR");
        btnModificar.addActionListener(e -> modificarReserva());
        btnModificar.setBackground(new Color(255, 140, 0)); 
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificar.setBounds(20, 330, 290, 50);
        colocarIcono(btnModificar, "IMAGENES/editar.png", 24);
        panelForm.add(btnModificar);
        
        JLabel lblHelpMod = new JLabel("<html><center>Selecciona tu reserva en la tabla<br>para cambiarla o borrarla</center></html>");
        lblHelpMod.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHelpMod.setHorizontalAlignment(SwingConstants.CENTER);
        lblHelpMod.setBounds(20, 385, 290, 30);
        panelForm.add(lblHelpMod);

        // Botón Eliminar
        JButton btnEliminar = new JButton("CANCELAR RESERVA");
        btnEliminar.addActionListener(e -> eliminarReserva());
        btnEliminar.setBackground(new Color(204, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setBounds(20, 440, 290, 40);
        colocarIcono(btnEliminar, "IMAGENES/eliminar.png", 24);
        panelForm.add(btnEliminar);
        
        // --- MONITOR (TABLA) ---
        JPanel panelTabla = new JPanel();
        panelTabla.setBackground(new Color(255, 255, 255, 150)); // Más transparencia
        panelTabla.setBounds(380, 80, 780, 550);
        panelTabla.setLayout(null);
        panelTabla.setBorder(new TitledBorder(new LineBorder(Color.GRAY), "Disponibilidad en Vivo", TitledBorder.LEADING, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
        layeredPane.add(panelTabla, JLayeredPane.PALETTE_LAYER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 30, 740, 500);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); 
        panelTabla.add(scrollPane);

        String[] columnas = new String[NOMBRES_INSTALACIONES.length + 1];
        columnas[0] = "HORA";
        for (int i = 0; i < NOMBRES_INSTALACIONES.length; i++) columnas[i + 1] = NOMBRES_INSTALACIONES[i];

        modeloMatriz = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaMatriz = new JTable(modeloMatriz);
        tablaMatriz.setRowHeight(30);
        tablaMatriz.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Fondo semi-transparente para que se note la imagen
        tablaMatriz.setBackground(new Color(255, 255, 255, 200)); 
        tablaMatriz.setSelectionBackground(new Color(0, 120, 215, 200)); 

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i < columnas.length; i++) tablaMatriz.getColumnModel().getColumn(i).setCellRenderer(center);

        scrollPane.setViewportView(tablaMatriz);
        actualizarMatriz();
    }

    // --- CARGA DE IMÁGENES ROBUSTA (Igual que login) ---
    private void cargarImagen(JLabel label, String ruta) {
        try {
            String rutaLimpia = ruta.startsWith("src/") ? ruta.substring(4) : ruta;
            if (!rutaLimpia.startsWith("/")) rutaLimpia = "/" + rutaLimpia;

            URL url = getClass().getResource(rutaLimpia);
            if (url != null) {
                ImageIcon imagen = new ImageIcon(url);
                int w = label.getWidth() > 0 ? label.getWidth() : 80;
                int h = label.getHeight() > 0 ? label.getHeight() : 80;
                Image imgEscalada = imagen.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(imgEscalada));
            } else {
                System.err.println("⚠️ Imagen no encontrada: " + rutaLimpia);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void colocarIcono(JButton boton, String ruta, int tamano) {
        try {
            String rutaLimpia = ruta.startsWith("src/") ? ruta.substring(4) : ruta;
            if (!rutaLimpia.startsWith("/")) rutaLimpia = "/" + rutaLimpia;

            URL url = getClass().getResource(rutaLimpia);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}
    }

    // --- MÉTODOS DE LÓGICA (Sin cambios) ---
    private void cambiarContrasena() {
        String nueva = JOptionPane.showInputDialog(this, "Introduce tu nueva contraseña:", "Cambio de Clave", JOptionPane.QUESTION_MESSAGE);
        if (nueva != null && !nueva.trim().isEmpty()) {
            boolean exito = usuarioDb.cambiarContrasena(usuarioActual.getUsername(), nueva);
            if (exito) JOptionPane.showMessageDialog(this, "✅ Contraseña actualizada.");
            else JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHoras() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (int i = 8; i < 23; i++) model.addElement((i < 10 ? "0" + i : i) + ":00");
        cmbHoraInicio.setModel(model);
    }

    private void realizarReserva() {
        try {
            String nombre = txtSocio.getText(); 
            int idCancha = cmbInstalaciones.getSelectedIndex() + 1; 
            int hInicio = Integer.parseInt(((String)cmbHoraInicio.getSelectedItem()).substring(0, 2));
            int duracion = (int) spinDuracion.getValue();

            reservaDb.guardarReserva(nombre, idCancha, LocalDate.now(), hInicio, duracion);
            JOptionPane.showMessageDialog(this, "✅ Reserva Exitosa");
            actualizarMatriz();
        } catch (ClubException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modificarReserva() {
        int fila = tablaMatriz.getSelectedRow();
        int col = tablaMatriz.getSelectedColumn();
        if (fila == -1 || col <= 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione TU reserva en la tabla.");
            return;
        }
        String contenido = (String) tablaMatriz.getValueAt(fila, col);
        if ("Libre".equals(contenido) || contenido == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Celda vacía.");
            return;
        }
        if (!contenido.contains(usuarioActual.getNombreReal())) {
            JOptionPane.showMessageDialog(this, "⛔ No puedes modificar reservas ajenas.");
            return;
        }

        String horaTxt = (String) tablaMatriz.getValueAt(fila, 0); 
        int oldHora = Integer.parseInt(horaTxt.substring(0, 2));
        int oldCancha = col;

        String nombre = txtSocio.getText(); 
        int newCancha = cmbInstalaciones.getSelectedIndex() + 1;
        int newHora = Integer.parseInt(((String)cmbHoraInicio.getSelectedItem()).substring(0, 2));
        int newDuracion = (int) spinDuracion.getValue();

        try {
            reservaDb.modificarReserva(oldCancha, oldHora, nombre, newCancha, newHora, newDuracion);
            JOptionPane.showMessageDialog(this, "🔄 ¡Reserva movida con éxito!");
            actualizarMatriz();
        } catch (ClubException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cambiar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarReserva() {
        int fila = tablaMatriz.getSelectedRow();
        int col = tablaMatriz.getSelectedColumn();
        if (fila == -1 || col <= 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Selecciona una celda OCUPADA.");
            return;
        }
        String contenido = (String) tablaMatriz.getValueAt(fila, col);
        if ("Libre".equals(contenido)) return;
        
        if (!contenido.contains(usuarioActual.getNombreReal())) {
            JOptionPane.showMessageDialog(this, "⛔ No puedes borrar reservas ajenas.");
            return;
        }

        int hora = Integer.parseInt(((String)tablaMatriz.getValueAt(fila, 0)).substring(0, 2));
        
        if (JOptionPane.showConfirmDialog(this, "¿Cancelar tu reserva?") == JOptionPane.YES_OPTION) {
            try {
                reservaDb.cancelarReserva(col, hora);
                actualizarMatriz();
                JOptionPane.showMessageDialog(this, "🗑️ Eliminado.");
            } catch (ClubException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void actualizarMatriz() {
        modeloMatriz.setRowCount(0);
        for (int h = 8; h <= 22; h++) {
            Object[] fila = new Object[NOMBRES_INSTALACIONES.length + 1];
            fila[0] = (h < 10 ? "0" + h : h) + ":00"; 
            for (int c = 1; c <= NOMBRES_INSTALACIONES.length; c++) {
                if (reservaDb.estaOcupado(c, h)) {
                    fila[c] = "⛔ " + reservaDb.obtenerCliente(c, h);
                } else {
                    fila[c] = "Libre"; 
                }
            }
            modeloMatriz.addRow(fila);
        }
    }
}