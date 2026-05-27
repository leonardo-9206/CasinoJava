package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import managers.UsuarioManager;
import models.Admin;
import models.Cliente;
import models.Empleado;
import models.Usuario;

public class PanelUsuarios extends JPanel {

    private static final long serialVersionUID = 1L;
    private UsuarioManager usuarioManager;
    private JTextArea textAreaUsuarios;

    public PanelUsuarios() {
        usuarioManager = new UsuarioManager();

        setBackground(new Color(20, 25, 40));
        setBounds(0, 0, 800, 720);
        setLayout(null);

        // ==========================================
        // 1. ZONA SUPERIOR: BARRA DE HERRAMIENTAS
        // ==========================================
        JPanel panelHerramientas = new JPanel();
        panelHerramientas.setBackground(new Color(15, 20, 35));
        panelHerramientas.setBounds(20, 20, 760, 60);
        panelHerramientas.setLayout(null);
        add(panelHerramientas);

        JButton btnAnadirUsuario = new JButton("AÑADIR USUARIO");
        try { btnAnadirUsuario.setIcon(new ImageIcon(PanelUsuarios.class.getResource("/icono_añadirusuario.png"))); } catch(Exception e){}
        btnAnadirUsuario.setBounds(10, 10, 180, 40);
        btnAnadirUsuario.setBackground(new Color(20, 25, 40));
        btnAnadirUsuario.setForeground(Color.WHITE);
        btnAnadirUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAnadirUsuario.setFocusPainted(false);
        panelHerramientas.add(btnAnadirUsuario);

        JButton btnEliminarUsuario = new JButton("ELIMINAR USUARIO");
        try { btnEliminarUsuario.setIcon(new ImageIcon(PanelUsuarios.class.getResource("/icono_eliminarusuario.png"))); } catch(Exception e){}
        btnEliminarUsuario.setBounds(200, 10, 180, 40);
        btnEliminarUsuario.setBackground(new Color(20, 25, 40));
        btnEliminarUsuario.setForeground(Color.WHITE);
        btnEliminarUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminarUsuario.setFocusPainted(false);
        panelHerramientas.add(btnEliminarUsuario);

        // ==========================================
        // 2. ZONA INFERIOR: VISUALIZADOR
        // ==========================================
        textAreaUsuarios = new JTextArea();
        textAreaUsuarios.setEditable(false);
        textAreaUsuarios.setBackground(new Color(10, 15, 30));
        textAreaUsuarios.setForeground(new Color(0, 255, 100));
        textAreaUsuarios.setFont(new Font("Consolas", Font.PLAIN, 14));
        textAreaUsuarios.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textAreaUsuarios);
        scrollPane.setBounds(20, 100, 760, 580);
        scrollPane.setBorder(null);
        add(scrollPane);

        actualizarPantalla();

        // ==========================================
        // 3. EVENTOS
        // ==========================================

        btnAnadirUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = solicitarInput("Ingresa el nombre del nuevo usuario:");
                if (nombre == null || nombre.trim().isEmpty()) return;

                if (usuarioManager.existeUsuario(nombre)) {
                    mostrarError("El nombre de usuario ya está en uso.");
                    return;
                }
                
                String idUsuario = solicitarInput("Ingresa el ID para el usuario:");
                if (idUsuario == null || idUsuario.trim().isEmpty()) return;
                
                if (usuarioManager.existeIdUsuario(idUsuario)) {
                    mostrarError("El ID de usuario ya está en uso. Por favor, revisa la tabla y elige otro.");
                    return;
                }

                String password = solicitarInput("Ingresa la contraseña para " + nombre + ":");
                if (password == null || password.trim().isEmpty()) return;

                String[] opciones = {"Admin", "Empleado", "Cliente"};
                int seleccion = JOptionPane.showOptionDialog(
                    PanelUsuarios.this,
                    "¿Qué rol tendrá el usuario?",
                    "Seleccionar Rol",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(new ImageIcon(PanelUsuarios.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)),
                    opciones,
                    opciones[1]
                );

                if (seleccion >= 0) {
                    String rol = opciones[seleccion];
                    Usuario nuevoUsuario = null;

                    // POLIMORFISMO en acción al crear el objeto hijo correspondiente
                    if (rol.equals("Admin")) {
                        nuevoUsuario = new Admin(idUsuario, nombre, password);
                    } else if (rol.equals("Empleado")) {
                        nuevoUsuario = new Empleado(idUsuario, nombre, password);
                    } else {
                        nuevoUsuario = new Cliente(idUsuario, nombre, password);
                    }

                    usuarioManager.agregarUsuario(nuevoUsuario);
                    mostrarMensaje("Usuario agregado exitosamente con el ID: " + idUsuario);
                    actualizarPantalla();
                }
            }
        });

        btnEliminarUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = solicitarInput("Ingresa el nombre del usuario a eliminar:");
                if (nombre != null && !nombre.trim().isEmpty()) {
                    if (nombre.equals("admin")) {
                        mostrarError("No puedes eliminar al administrador principal del sistema.");
                        return;
                    }
                    
                    int confirmacion = JOptionPane.showConfirmDialog(
                            PanelUsuarios.this, 
                            "¿Estás seguro de que deseas eliminar permanentemente a '" + nombre + "'?", 
                            "Confirmar Eliminación", 
                            JOptionPane.YES_NO_OPTION);
                            
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        boolean borrado = usuarioManager.eliminarUsuario(nombre);
                        if (borrado) {
                            mostrarMensaje("Usuario eliminado exitosamente.");
                            actualizarPantalla();
                        } else {
                            mostrarError("No se encontró ningún usuario con ese nombre.");
                        }
                    }
                }
            }
        });
    }

    private void actualizarPantalla() {
        textAreaUsuarios.setText(usuarioManager.obtenerUsuariosTexto());
    }

    // ==========================================
    // WRAPPERS DE JOPTIONPANE
    // ==========================================

    private String solicitarInput(String mensaje) {
        return (String) JOptionPane.showInputDialog(this, mensaje, "Diamond Casino", JOptionPane.QUESTION_MESSAGE, new ImageIcon(new ImageIcon(PanelUsuarios.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)), null, "");
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Diamond Casino", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(new ImageIcon(PanelUsuarios.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(PanelUsuarios.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
    }
}
