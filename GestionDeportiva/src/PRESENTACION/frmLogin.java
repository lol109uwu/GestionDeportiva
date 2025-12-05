package PRESENTACION;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import DATOS.usuarioDb;
import LOGICA.Usuario;

public class frmLogin extends JFrame {

    private JPanel contentPane;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JLayeredPane layeredPane; // Necesario para el fondo

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frmLogin frame = new frmLogin();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public frmLogin() {
        setTitle("Acceso al Club Deportivo ADAMANT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        
        // Configuración de capas
        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);
        layeredPane.setLayout(null);

        // --- CAPA 1: IMAGEN DE FONDO ---
        JLabel lblFondo = new JLabel("");
        lblFondo.setBounds(0, 0, 850, 550);
        // Usamos el método robusto para cargar la imagen
        cargarImagen(lblFondo, "IMAGENES/fondo_club.png"); 
        layeredPane.add(lblFondo, JLayeredPane.DEFAULT_LAYER);

        // --- CAPA 2: PANEL SEMI-TRANSPARENTE ---
        JPanel panelContenedor = new JPanel();
        panelContenedor.setBackground(new Color(0, 0, 0, 180)); // Negro transparente
        panelContenedor.setBounds(250, 50, 350, 420);
        panelContenedor.setLayout(null);
        layeredPane.add(panelContenedor, JLayeredPane.PALETTE_LAYER);

        // --- CAPA 3: CONTROLES ---
        
        // Logo
        JLabel lblLogo = new JLabel("");
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        cargarImagen(lblLogo, "IMAGENES/logo.png");
        lblLogo.setBounds(135, 20, 80, 80);
        panelContenedor.add(lblLogo);

        // Título
        JLabel lblTitulo = new JLabel("BIENVENIDO");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(75, 110, 200, 30);
        panelContenedor.add(lblTitulo);

        // Usuario
        JLabel lblUser = new JLabel("USUARIO / DNI");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(200, 200, 200));
        lblUser.setBounds(40, 160, 150, 20);
        panelContenedor.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setBounds(40, 185, 270, 35);
        txtUser.setBorder(new EmptyBorder(5, 10, 5, 10)); // Espacio interno
        panelContenedor.add(txtUser);

        // Contraseña
        JLabel lblPass = new JLabel("CONTRASEÑA");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(200, 200, 200));
        lblPass.setBounds(40, 235, 150, 20);
        panelContenedor.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setBounds(40, 260, 270, 35);
        txtPass.setBorder(new EmptyBorder(5, 10, 5, 10));
        panelContenedor.add(txtPass);

        // Botón Entrar
        JButton btnEntrar = new JButton("INICIAR SESIÓN");
        btnEntrar.setBackground(new Color(0, 153, 255)); // Azul brillante
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setBounds(40, 330, 270, 45);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                intentarLogin();
            }
        });
        panelContenedor.add(btnEntrar);
    }

    // --- MÉTODO UNIVERSAL PARA IMÁGENES (A PRUEBA DE ERRORES) ---
    private void cargarImagen(JLabel label, String ruta) {
        try {
            // 1. Limpieza de ruta (por si viene con "src/")
            String rutaLimpia = ruta;
            if (ruta.startsWith("src/")) {
                rutaLimpia = ruta.substring(4);
            }
            if (!rutaLimpia.startsWith("/")) {
                rutaLimpia = "/" + rutaLimpia;
            }

            // 2. Cargar como recurso (Funciona en Eclipse Y en el JAR)
            URL url = getClass().getResource(rutaLimpia);

            if (url != null) {
                ImageIcon imagen = new ImageIcon(url);
                // Dimensiones por defecto si el label no tiene tamaño aún
                int w = label.getWidth() > 0 ? label.getWidth() : 80;
                int h = label.getHeight() > 0 ? label.getHeight() : 80;
                
                Image imgEscalada = imagen.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(imgEscalada));
            } else {
                System.err.println("⚠️ Imagen no encontrada: " + rutaLimpia);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void intentarLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete los campos.");
            return;
        }

        Usuario u = usuarioDb.autenticar(user, pass);

        if (u != null) {
            this.dispose(); // Cierra login
            if (u.getRol().equals("ADMIN")) {
                new frmAdmin().setVisible(true);
            } else {
                new frmReservas(u).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}