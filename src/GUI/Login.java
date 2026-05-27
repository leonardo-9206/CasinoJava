package GUI;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import managers.UsuarioManager;
import models.Usuario;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsuario;
	private JPasswordField txtContraseña;
	
	// Nuestro "Cerebro" que lee el archivo txt y valida contraseñas
	private UsuarioManager usuarioManager;
	private JButton btnIniciarSesion;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Login() {
		// Inicializamos el cerebro de usuarios (esto cargará el archivo .txt o creará al admin por defecto)
		usuarioManager = new UsuarioManager();
		
		// --- 1. CONFIGURACIÓN DE LA VENTANA ---
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Agrandé un poco la ventana para que quepa el logo
		setBounds(100, 100, 487, 479);
		setTitle("Diamond Casino - Iniciar Sesión");
		setLocationRelativeTo(null); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// COLOR DE FONDO: Más negro, menos azul (15, 15, 20)
		contentPane.setBackground(new Color(15, 15, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// --- 2. LOGOTIPO DIAMANTE ---
		try {
			ImageIcon iconDiamanteOriginal = new ImageIcon(Login.class.getResource("/diamante.png"));
			// Al poner -1 en el alto, Java calcula la altura exacta para no "apachurrar" la imagen,
			// manteniendo su proporción real respecto a tu ancho preferido (216).
			Image imgEscalada = iconDiamanteOriginal.getImage().getScaledInstance(216, -1, Image.SCALE_SMOOTH);
			
			JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
			// Aumenté el alto del cajón a 120 para que la imagen (ya sin deformar) tenga espacio para dibujarse
			lblLogo.setBounds(118, 20, 216, 120); 
			contentPane.add(lblLogo);
		} catch (Exception ex) {
			System.out.println("No se pudo cargar diamante.png - Verifica la ruta");
		}
		
		// --- 3. TÍTULO ELEGANTE ---
		JLabel lblDiamond = new JLabel("DIAMOND CASINO");
		lblDiamond.setHorizontalAlignment(SwingConstants.CENTER);
		lblDiamond.setBounds(20, 150, 414, 30); // Lo bajamos un poco por el logo
		// FUENTE: Segoe UI Light (es delgada y súper elegante) | COLOR: Plateado brillante/claro
		lblDiamond.setFont(new Font("Segoe UI Light", Font.PLAIN, 26));
		lblDiamond.setForeground(new Color(230, 230, 235)); 
		contentPane.add(lblDiamond);
		
		// --- 4. ETIQUETAS (LABELS) ---
		JLabel lblUsuario = new JLabel("Usuario");
		lblUsuario.setBounds(107, 202, 93, 15);
		// Regresé a Segoe UI normal para que sea muy legible
		lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblUsuario.setForeground(Color.WHITE); 
		contentPane.add(lblUsuario);
		
		JLabel lblContraseña = new JLabel("Contraseña");
		lblContraseña.setBounds(106, 257, 94, 15);
		lblContraseña.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblContraseña.setForeground(Color.WHITE);
		contentPane.add(lblContraseña);
		
		// --- 5. CAJAS DE TEXTO (TEXTFIELDS) ---
		txtUsuario = new JTextField();
		txtUsuario.setBounds(243, 198, 140, 25);
		// ESTILO OSCURO CON BORDE PLATEADO (Color casi negro para contraste)
		txtUsuario.setBackground(new Color(30, 30, 35));
		txtUsuario.setForeground(Color.WHITE);
		txtUsuario.setCaretColor(Color.WHITE); 
		txtUsuario.setBorder(new LineBorder(new Color(192, 192, 192), 1));
		txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPane.add(txtUsuario);
		txtUsuario.setColumns(10);
		
		txtContraseña = new JPasswordField(); 
		txtContraseña.setBounds(243, 253, 140, 25);
		txtContraseña.setBackground(new Color(30, 30, 35));
		txtContraseña.setForeground(Color.WHITE);
		txtContraseña.setCaretColor(Color.WHITE);
		txtContraseña.setBorder(new LineBorder(new Color(192, 192, 192), 1));
		txtContraseña.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPane.add(txtContraseña);
		
		// --- 6. BOTÓN DE INICIAR SESIÓN ---
		btnIniciarSesion = new JButton("INICIAR SESIÓN");
		btnIniciarSesion.setBounds(162, 315, 150, 35);
		// ESTILO DEL BOTÓN
		btnIniciarSesion.setBackground(new Color(192, 192, 192)); 
		btnIniciarSesion.setForeground(Color.BLACK); 
		btnIniciarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnIniciarSesion.setFocusPainted(false); 
		contentPane.add(btnIniciarSesion);
		
		// --- 7. ACCIONES DEL BOTÓN ---
		btnIniciarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Obtenemos lo que se escribió en las cajas
				String usr = txtUsuario.getText();
				String pass = new String(txtContraseña.getPassword()); // getPassword es más seguro en Java
				
				// 2. Le mandamos los datos al cerebro para que revise en su "base de datos" (.txt)
				Usuario usuarioLogueado = usuarioManager.login(usr, pass);
				
				// 3. Tomamos la decisión
				if (usuarioLogueado != null) {
					// ¡Éxito! Mostrar ventanita verde
					JOptionPane.showMessageDialog(null, 
						"¡Bienvenido al sistema, " + usuarioLogueado.getNombre() + "!\nTu rol es: " + usuarioLogueado.getRol(), 
						"Acceso Concedido", 
						JOptionPane.INFORMATION_MESSAGE);
					
					// ¡Transición de ventanas!
					// 1. Destruye la ventana de Login
					dispose(); 
					
					// 2. Construye el Menú Principal pasándole los datos del usuario
					MainFrame menu = new MainFrame(usuarioLogueado);
					
					// 3. Muestra el menú en pantalla
					menu.setVisible(true);
					
				} else {
					// Fallo total. Mostrar ventanita de error
					JOptionPane.showMessageDialog(null, 
						"Usuario o contraseña incorrectos. Intente de nuevo.", 
						"Acceso Denegado", 
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// --- 8. IMAGEN DE FONDO ---
		// (Eliminado temporalmente por ahora)
	}
}
