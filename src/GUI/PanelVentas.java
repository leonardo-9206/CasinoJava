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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import managers.FechaManager;
import managers.InventarioManager;
import managers.VentasManager;
import models.CuentaCliente;
import models.Producto;
import models.Venta;

public class PanelVentas extends JPanel {

    private static final long serialVersionUID = 1L;
    private VentasManager ventasManager;
    private InventarioManager invManager;
    private JTextArea textAreaCuentas;

    public PanelVentas() {
        // Inicializamos los managers
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

        JButton btnNuevaVenta = crearBotonHerramienta("NUEVA VENTA", "/icono_nuevaventa.png", 10);
        panelHerramientas.add(btnNuevaVenta);

        JButton btnCorteCaja = crearBotonHerramienta("CORTE CAJA", "/icono_corte.png", 190);
        panelHerramientas.add(btnCorteCaja);

        // ==========================================
        // 2. ZONA INFERIOR: VISUALIZADOR DE CUENTAS
        // ==========================================
        textAreaCuentas = new JTextArea();
        textAreaCuentas.setEditable(false);
        textAreaCuentas.setBackground(new Color(10, 15, 30));
        textAreaCuentas.setForeground(new Color(0, 255, 100));
        textAreaCuentas.setFont(new Font("Consolas", Font.PLAIN, 14));
        textAreaCuentas.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textAreaCuentas);
        scrollPane.setBounds(20, 100, 760, 580);
        scrollPane.setBorder(null);
        add(scrollPane);

        actualizarPantalla();

        // ==========================================
        // 3. EVENTOS Y FLUJO C++
        // ==========================================

        btnNuevaVenta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Pedir ID de cliente
                String idCliente = solicitarInput("Ingresa el ID del cliente para la venta:");
                if (idCliente == null || idCliente.trim().isEmpty()) return;

                CuentaCliente cuenta = ventasManager.buscarCliente(idCliente);
                
                // 2. Si no existe, lo creamos
                if (cuenta == null) {
                    mostrarMensaje("Cliente no encontrado. Creando nueva cuenta.");
                    String nombre = solicitarInput("Ingresa el nombre del nuevo cliente:");
                    if (nombre == null || nombre.trim().isEmpty()) return;
                    
                    cuenta = new CuentaCliente(idCliente, nombre);
                    ventasManager.agregarCliente(cuenta);
                    actualizarPantalla(); // Actualiza el JTextArea
                }

                // 3. Creamos la venta base con la fecha actual del sistema
                Venta nuevaVenta = new Venta(idCliente, FechaManager.getFechaActual());
                
                // 4. Bucle para agregar productos (while)
                boolean seguir = true;
                while (seguir) {
                    String inputIdProd = solicitarInput("ID del Producto a comprar (o escribe FIN para terminar):");
                    
                    if (inputIdProd == null || inputIdProd.equalsIgnoreCase("FIN")) {
                        seguir = false;
                        continue;
                    }

                    Producto p = invManager.buscarProducto(inputIdProd);
                    if (p == null) {
                        mostrarError("No se encontró un producto con esa ID.");
                    } else {
                        String inputCant = solicitarInput("¿Cantidad a vender de " + p.getNombre() + "?");
                        if (inputCant != null) {
                            try {
                                int cant = Integer.parseInt(inputCant);
                                
                                // Validación de stock
                                if (cant > p.getCantidad()) {
                                    mostrarError("Error, no puedes vender más de lo que hay en stock (" + p.getCantidad() + ").");
                                } else {
                                    int stockFinal = p.getCantidad() - cant;
                                    if (stockFinal <= p.getStockMin()) {
                                        mostrarMensaje("Aviso: Al vender esto, el stock quedará en " + stockFinal + " (Nivel mínimo o inferior).");
                                    }
                                    
                                    nuevaVenta.agregarDetalle(p, cant);
                                    mostrarMensaje("¡Se agregó '" + p.getNombre() + " x" + cant + "' a la venta!");
                                }
                            } catch (Exception ex) {
                                mostrarError("Cantidad inválida.");
                            }
                        }
                    }
                }
                
                // 5. Finalizar Venta si agregaron algo
                if (!nuevaVenta.getDetalles().isEmpty()) {
                    boolean procesado = ventasManager.procesarVenta(nuevaVenta);
                    if (procesado) {
                        mostrarMensaje("¡Venta completada!\nTotal a pagar: $" + nuevaVenta.getTotal());
                    }
                } else {
                    mostrarMensaje("Venta cancelada (No se agregaron productos).");
                }
            }
        });

        btnCorteCaja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Confirmación
                int resp = JOptionPane.showConfirmDialog(null, 
                        "¿Estás seguro de hacer el corte de caja para la fecha " + FechaManager.getFechaActual() + "?\nEsto avanzará el día en el sistema.", 
                        "Confirmar Corte", JOptionPane.YES_NO_OPTION);
                
                if (resp == JOptionPane.YES_OPTION) {
                    // Generar reporte .txt
                    String resultado = ventasManager.generarCorteCaja(FechaManager.getFechaActual());
                    mostrarMensaje(resultado);
                    
                    // Avanzar día en el archivo local
                    FechaManager.avanzarDia();
                    
                    // Actualizar etiqueta del MainFrame
                    MainFrame mainForm = (MainFrame) SwingUtilities.getWindowAncestor(PanelVentas.this);
                    if (mainForm != null) {
                        mainForm.actualizarFecha();
                    }
                    mostrarMensaje("Se ha avanzado el dia en uno: " + FechaManager.getFechaActual());
                }
            }
        });
    }

    private void actualizarPantalla() {
        textAreaCuentas.setText(ventasManager.obtenerCuentasText());
    }

    private JButton crearBotonHerramienta(String texto, String rutaIcono, int x) {
        JButton btn = new JButton(texto);
        try { btn.setIcon(new ImageIcon(PanelVentas.class.getResource(rutaIcono))); } catch (Exception e){}
        btn.setBounds(x, 10, 180, 40);
        btn.setBackground(new Color(20, 25, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    //para poder reescalar bien la imagen y no estar poniendo el codigo aca 500 veces
    private String solicitarInput(String mensaje) {
        return (String) JOptionPane.showInputDialog(this, mensaje, "Diamond Casino", JOptionPane.QUESTION_MESSAGE, new ImageIcon(new ImageIcon(PanelVentas.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)), null, "");
    }
    
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Diamond Casino", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(new ImageIcon(PanelVentas.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE, new ImageIcon(new ImageIcon(PanelVentas.class.getResource("/diamante.png")).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
    }
}
