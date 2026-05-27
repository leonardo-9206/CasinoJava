package GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import models.Usuario;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Usuario usuarioLogueado;
	private JPanel panelCentral;
	private JLabel lblFecha;

	public MainFrame() {
		this(null); 
	}

	public MainFrame(Usuario usuario) {
		this.usuarioLogueado = usuario;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1050, 720);
		setLocationRelativeTo(null); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		// ==========================================
		// 1. BARRA LATERAL (SIDEBAR)
		// ==========================================
		JPanel panelMenu = new JPanel();
		panelMenu.setBackground(new Color(10, 15, 35)); 
		panelMenu.setBounds(0, 0, 250, 720);
		panelMenu.setLayout(null);
		contentPane.add(panelMenu);
		
		// -- IMAGEN DEL LOGO DIAMANTE --
		try {
			ImageIcon iconDiamanteOriginal = new ImageIcon(MainFrame.class.getResource("/diamante.png"));
			Image imgEscalada = iconDiamanteOriginal.getImage().getScaledInstance(120, -1, Image.SCALE_SMOOTH);
			JLabel lblLogoImagen = new JLabel(new ImageIcon(imgEscalada));
			// Centrado horizontalmente (250/2 - 120/2 = 65)
			lblLogoImagen.setBounds(65, 20, 120, 80); 
			panelMenu.add(lblLogoImagen);
		} catch (Exception ex) {
			System.out.println("No se pudo cargar diamante.png en el Menú Principal");
		}
		
		// -- Título "DIAMOND CASINO" --
		JLabel lblDiamond = new JLabel("DIAMOND");
		lblDiamond.setForeground(new Color(230, 230, 235));
		lblDiamond.setFont(new Font("Segoe UI Light", Font.BOLD, 32));
		lblDiamond.setHorizontalAlignment(SwingConstants.CENTER);
		lblDiamond.setBounds(0, 100, 250, 40); 
		panelMenu.add(lblDiamond);
		
		JLabel lblCasino = new JLabel("C A S I N O");
		lblCasino.setForeground(new Color(192, 192, 192));
		lblCasino.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblCasino.setHorizontalAlignment(SwingConstants.CENTER);
		lblCasino.setBounds(0, 140, 250, 20); 
		panelMenu.add(lblCasino);
		
		// -- Perfil del Usuario --
		JLabel lblUsuarioActivo = new JLabel();
		lblUsuarioActivo.setForeground(Color.WHITE);
		lblUsuarioActivo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		lblUsuarioActivo.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsuarioActivo.setBounds(0, 180, 250, 20); 
		if (usuario != null) {
			lblUsuarioActivo.setText("Hola, " + usuario.getNombre() + " (" + usuario.getRol() + ")");
			setTitle("Diamond Casino - " + usuario.getRol());
		}
		panelMenu.add(lblUsuarioActivo);
		
		// -- Fecha del Sistema --
		lblFecha = new JLabel("Fecha: " + managers.FechaManager.getFechaActual());
		lblFecha.setForeground(new Color(150, 150, 150));
		lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblFecha.setHorizontalAlignment(SwingConstants.CENTER);
		lblFecha.setBounds(0, 200, 250, 20); 
		panelMenu.add(lblFecha);

		// ==========================================
		// 2. BOTONES DEL MENÚ (CÓDIGO PURO WINDOWBUILDER)
		// ==========================================
		
		JButton btnPuntoVenta = new JButton("PUNTO DE VENTA");
		try { btnPuntoVenta.setIcon(new ImageIcon(MainFrame.class.getResource("/icono_venta.png"))); } catch(Exception e) {}
		btnPuntoVenta.setFocusPainted(false);
		btnPuntoVenta.setBorderPainted(false);
		btnPuntoVenta.setHorizontalAlignment(SwingConstants.LEFT);
		btnPuntoVenta.setForeground(Color.WHITE);
		btnPuntoVenta.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnPuntoVenta.setBorder(new EmptyBorder(0, 30, 0, 0));
		btnPuntoVenta.setBackground(new Color(10, 15, 35));
		btnPuntoVenta.setBounds(0, 230, 250, 45);
		panelMenu.add(btnPuntoVenta);
		
		JButton btnInventario = new JButton("INVENTARIO");
		try { btnInventario.setIcon(new ImageIcon(MainFrame.class.getResource("/icono_inventario.png"))); } catch(Exception e) {}
		btnInventario.setFocusPainted(false);
		btnInventario.setBorderPainted(false);
		btnInventario.setHorizontalAlignment(SwingConstants.LEFT);
		btnInventario.setForeground(Color.WHITE);
		btnInventario.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnInventario.setBorder(new EmptyBorder(0, 30, 0, 0));
		btnInventario.setBackground(new Color(10, 15, 35));
		btnInventario.setBounds(0, 285, 250, 45);
		panelMenu.add(btnInventario);
		
		JButton btnUsuarios = new JButton("USUARIOS");
		try { btnUsuarios.setIcon(new ImageIcon(MainFrame.class.getResource("/icono_usuarios.png"))); } catch(Exception e) {}
		btnUsuarios.setFocusPainted(false);
		btnUsuarios.setBorderPainted(false);
		btnUsuarios.setHorizontalAlignment(SwingConstants.LEFT);
		btnUsuarios.setForeground(Color.WHITE);
		btnUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnUsuarios.setBorder(new EmptyBorder(0, 30, 0, 0));
		btnUsuarios.setBackground(new Color(10, 15, 35));
		btnUsuarios.setBounds(0, 340, 250, 45);
		panelMenu.add(btnUsuarios);
		
		JButton btnReportes = new JButton("REPORTES");
		try { btnReportes.setIcon(new ImageIcon(MainFrame.class.getResource("/icono_reportes.png"))); } catch(Exception e) {}
		btnReportes.setFocusPainted(false);
		btnReportes.setBorderPainted(false);
		btnReportes.setHorizontalAlignment(SwingConstants.LEFT);
		btnReportes.setForeground(Color.WHITE);
		btnReportes.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnReportes.setBorder(new EmptyBorder(0, 30, 0, 0));
		btnReportes.setBackground(new Color(10, 15, 35));
		btnReportes.setBounds(0, 395, 250, 45);
		panelMenu.add(btnReportes);
		
		JButton btnHistorial = new JButton("MIS COMPRAS");
		try { btnHistorial.setIcon(new ImageIcon(MainFrame.class.getResource("/icono_historial.png"))); } catch(Exception e) {}
		btnHistorial.setFocusPainted(false);
		btnHistorial.setBorderPainted(false);
		btnHistorial.setHorizontalAlignment(SwingConstants.LEFT);
		btnHistorial.setForeground(Color.WHITE);
		btnHistorial.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnHistorial.setBorder(new EmptyBorder(0, 30, 0, 0));
		btnHistorial.setBackground(new Color(10, 15, 35));
		btnHistorial.setBounds(0, 230, 250, 45);
		btnHistorial.setVisible(false); 
		panelMenu.add(btnHistorial);
		
		JButton btnCerrarSesion = new JButton("CERRAR SESIÓN");
		try { btnCerrarSesion.setIcon(new ImageIcon(MainFrame.class.getResource("/icono_cerrar.png"))); } catch(Exception e) {}
		btnCerrarSesion.setFocusPainted(false);
		btnCerrarSesion.setBorderPainted(false);
		btnCerrarSesion.setHorizontalAlignment(SwingConstants.LEFT);
		btnCerrarSesion.setForeground(Color.WHITE);
		btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnCerrarSesion.setBorder(new EmptyBorder(0, 30, 0, 0));
		btnCerrarSesion.setBackground(new Color(10, 15, 35));
		btnCerrarSesion.setBounds(0, 620, 250, 45);
		panelMenu.add(btnCerrarSesion);

		// ==========================================
		// 3. LA MAGIA DE LOS ROLES (Ocultar botones)
		// ==========================================
		if (usuarioLogueado != null) {
			if (usuarioLogueado.getRol().equals("Empleado")) {
				btnUsuarios.setVisible(false);
				
			} else if (usuarioLogueado.getRol().equals("Cliente")) {
				btnPuntoVenta.setVisible(false);
				btnInventario.setVisible(false);
				btnUsuarios.setVisible(false);
				btnReportes.setVisible(false);
				
				btnHistorial.setVisible(true); // Solo el cliente ve este
			}
		}
		
		// ==========================================
		// 4. ÁREA CENTRAL (El Canvas de trabajo)
		// ==========================================
		panelCentral = new JPanel();
		panelCentral.setBackground(new Color(20, 25, 40)); 
		panelCentral.setBounds(250, 0, 800, 720);
		panelCentral.setLayout(null);
		contentPane.add(panelCentral);
		

		
		// ==========================================
		// 5. ACCIONES (Eventos Base y Transiciones)
		// ==========================================
		
		btnPuntoVenta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelCentral.removeAll();
				PanelVentas vistaVentas = new PanelVentas();
				panelCentral.add(vistaVentas);
				panelCentral.revalidate();
				panelCentral.repaint();
			}
		});

		btnInventario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Borramos lo que haya en la parte derecha
				panelCentral.removeAll();
				// 2. Creamos la vista de Inventario
				PanelInventario vistaInventario = new PanelInventario();
				// 3. La agregamos y actualizamos pantalla
				panelCentral.add(vistaInventario);
				panelCentral.revalidate();
				panelCentral.repaint();
			}
		});

		btnReportes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelCentral.removeAll();
				PanelReportes vistaReportes = new PanelReportes();
				panelCentral.add(vistaReportes);
				panelCentral.revalidate();
				panelCentral.repaint();
			}
		});

		btnUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelCentral.removeAll();
				PanelUsuarios vistaUsuarios = new PanelUsuarios();
				panelCentral.add(vistaUsuarios);
				panelCentral.revalidate();
				panelCentral.repaint();
			}
		});

		btnCerrarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); // Cierra el menú
				Login login = new Login();
				login.setVisible(true); // Regresa a la pantalla de login
			}
		});
	}
	
	// Método público para que otros paneles actualicen la fecha visualmente
	public void actualizarFecha() {
	    if (lblFecha != null) {
	        lblFecha.setText("Fecha: " + managers.FechaManager.getFechaActual());
	    }
	}
}
