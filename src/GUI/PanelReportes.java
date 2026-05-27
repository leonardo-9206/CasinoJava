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

import managers.InventarioManager;
import managers.ReportesManager;
import managers.VentasManager;
import models.CuentaCliente;

public class PanelReportes extends JPanel {

    private static final long serialVersionUID = 1L;
    private VentasManager ventasManager;
    private InventarioManager invManager;
    private JTextArea textAreaReportes;

    public PanelReportes() {
        invManager = new InventarioManager();
        ventasManager = new VentasManager(invManager);

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

        // Botón 1: MOSTRAR VENTAS (Muestra en textarea)
        JButton btnMostrarVentas = new JButton("Mostrar ventas");
        try { btnMostrarVentas.setIcon(new ImageIcon(PanelReportes.class.getResource("/icono_historial.png"))); } catch(Exception e){}
        btnMostrarVentas.setBounds(10, 10, 145, 40);
        btnMostrarVentas.setBackground(new Color(20, 25, 40));
        btnMostrarVentas.setForeground(Color.WHITE);
        btnMostrarVentas.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnMostrarVentas.setFocusPainted(false);
        panelHerramientas.add(btnMostrarVentas);

        // Botón 2: LOG RESTOCK (Muestra en textarea)
        JButton btnLogRestock = new JButton("Mostrar restock");
        try { btnLogRestock.setIcon(new ImageIcon(PanelReportes.class.getResource("/icono_mostrarrestock.png"))); } catch(Exception e){}
        btnLogRestock.setBounds(160, 10, 145, 40);
        btnLogRestock.setBackground(new Color(20, 25, 40));
        btnLogRestock.setForeground(Color.WHITE);
        btnLogRestock.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLogRestock.setFocusPainted(false);
        panelHerramientas.add(btnLogRestock);

        // Botón 3: REPORTE X CUENTA (Genera .txt)
        JButton btnRepCuenta = new JButton("Reporte por ID");
        try { btnRepCuenta.setIcon(new ImageIcon(PanelReportes.class.getResource("/icono_reportecliente.png"))); } catch(Exception e){}
        btnRepCuenta.setBounds(310, 10, 145, 40);
        btnRepCuenta.setBackground(new Color(20, 25, 40));
        btnRepCuenta.setForeground(Color.WHITE);
        btnRepCuenta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRepCuenta.setFocusPainted(false);
        panelHerramientas.add(btnRepCuenta);

        // Botón 4: REPORTE X FECHA (Genera .txt)
        JButton btnRepFecha = new JButton("Reporte por fecha");
        try { btnRepFecha.setIcon(new ImageIcon(PanelReportes.class.getResource("/icono_reportefecha.png"))); } catch(Exception e){}
        btnRepFecha.setBounds(460, 10, 145, 40);
        btnRepFecha.setBackground(new Color(20, 25, 40));
        btnRepFecha.setForeground(Color.WHITE);
        btnRepFecha.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRepFecha.setFocusPainted(false);
        panelHerramientas.add(btnRepFecha);

        // Botón 5: REPORTE X PRODUCTO (Genera .txt)
        JButton btnRepProducto = new JButton("Reporte por producto");
        try { btnRepProducto.setIcon(new ImageIcon(PanelReportes.class.getResource("/icono_reporteprod.png"))); } catch(Exception e){}
        btnRepProducto.setBounds(610, 10, 145, 40);
        btnRepProducto.setBackground(new Color(20, 25, 40));
        btnRepProducto.setForeground(Color.WHITE);
        btnRepProducto.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRepProducto.setFocusPainted(false);
        panelHerramientas.add(btnRepProducto);

        // ==========================================
        // 2. ZONA INFERIOR: VISUALIZADOR
        // ==========================================
        textAreaReportes = new JTextArea();
        textAreaReportes.setEditable(false);
        textAreaReportes.setBackground(new Color(10, 15, 30));
        textAreaReportes.setForeground(new Color(0, 255, 100));
        textAreaReportes.setFont(new Font("Consolas", Font.PLAIN, 14));
        textAreaReportes.setBorder(new EmptyBorder(10, 10, 10, 10));
        textAreaReportes.setText("Selecciona un boton");

        JScrollPane scrollPane = new JScrollPane(textAreaReportes);
        scrollPane.setBounds(20, 100, 760, 580);
        scrollPane.setBorder(null);
        add(scrollPane);

        // ==========================================
        // 3. EVENTOS
        // ==========================================

        // Botón 1: Mostrar todas las ventas en el textarea
        btnMostrarVentas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textAreaReportes.setText(ventasManager.obtenerTodasLasVentasTexto());
            }
        });

        // Botón 2: Mostrar log de restock en el textarea
        btnLogRestock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textAreaReportes.setText(ReportesManager.leerLogRestock());
            }
        });

        // Botón 3: Generar .txt de reporte por cuenta
        btnRepCuenta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idCuenta = solicitarInput("Ingresa el ID de la cuenta para generar el reporte:");
                if (idCuenta != null && !idCuenta.trim().isEmpty()) {
                    CuentaCliente cliente = ventasManager.buscarCliente(idCuenta);
                    String resultado = ReportesManager.generarReporteCliente(cliente);
                    mostrarMensaje(resultado);
                }
            }
        });

        // Botón 4: Generar .txt de reporte por fecha
        btnRepFecha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fecha = solicitarInput("Ingresa la fecha a consultar (dd/mm/aaaa):");
                if (fecha != null && !fecha.trim().isEmpty()) {
                    String resultado = ReportesManager.generarReportePorFecha(ventasManager, fecha);
                    mostrarMensaje(resultado);
                }
            }
        });

        // Botón 5: Generar .txt de reporte por producto
        btnRepProducto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idProd = solicitarInput("Ingresa el ID del producto a consultar:");
                if (idProd != null && !idProd.trim().isEmpty()) {
                    String resultado = ReportesManager.generarReportePorProducto(ventasManager, idProd);
                    mostrarMensaje(resultado);
                }
            }
        });
    }

    // ==========================================
    // WRAPPERS DE JOPTIONPANE
    // ==========================================

    private String solicitarInput(String mensaje) {
        return (String) JOptionPane.showInputDialog(this, mensaje, "Diamond Casino", JOptionPane.QUESTION_MESSAGE, new ImageIcon(new ImageIcon(PanelReportes.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)), null, "");
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Diamond Casino", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(new ImageIcon(PanelReportes.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(PanelReportes.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
    }
}
