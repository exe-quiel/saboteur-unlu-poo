package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class PanelChat extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -3384882855243075120L;

    private JList<Mensaje> chat;

    private ControladorJuego controlador;

    private JScrollPane scrollablePane;

    private int prevMax = -1;

    public PanelChat(IVista vista) {
        this.controlador = vista.getControlador();

        chat = new JList<>();
        chat.setCellRenderer(new DefaultListCellRenderer() {

            /**
             * 
             */
            private static final long serialVersionUID = 4827397224790241362L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Mensaje) {
                    Mensaje mensaje = (Mensaje) value;
                    setText(mensaje.obtenerOrigen() + ": " + mensaje.getTexto());
                    if (mensaje.esDelSistema()) {
                        setForeground(Color.RED);
                    }
                }
                return this;
            }
        });

        chat.setFont(GUIConstants.PLAIN_FONT);
        chat.setSelectionModel(new ModeloNoSeleccionable());

        scrollablePane = new JScrollPane(chat);
        scrollablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollBar verticalScrollBar = scrollablePane.getVerticalScrollBar();
        verticalScrollBar.addAdjustmentListener(new AvanzarAlFinalAdjustmentListener());

        JTextField textoDelUsuario = new JTextField();
        textoDelUsuario.setFont(GUIConstants.PLAIN_FONT);
        textoDelUsuario.setSize(500, 20);
        textoDelUsuario.setText("Escribir mensaje");

        JButton botonEnviar = new JButton("Enviar");
        botonEnviar.setFont(GUIConstants.PLAIN_FONT);

        botonEnviar.addActionListener(event -> {
            String text = textoDelUsuario.getText();
            if (text != null && !text.isEmpty()) {
                vista.getControlador().enviarMensaje(new Mensaje(vista.getJugador(), text));
            }
            textoDelUsuario.setText("Escribir mensaje");
        });

        textoDelUsuario.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // Enviar mensaje al apretar Enter
                if ("\n".charAt(0) == e.getKeyChar() && textoDelUsuario.getText().length() > 0) {
                    botonEnviar.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Sin implementación
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Sin implementación
            }
        });
        setLayout(new GridLayout(2, 1));
        add(scrollablePane);

        JComponent panelInferiorChat = new JPanel();
        panelInferiorChat.setLayout(new FlowLayout());
        panelInferiorChat.add(textoDelUsuario);
        panelInferiorChat.add(botonEnviar);
        add(panelInferiorChat);
        setPreferredSize(new Dimension(200, 0));
    }

    public void actualizar() {
        List<Mensaje> mensajes = this.controlador.obtenerMensajes();
        this.chat.setModel(new ChatListModel(mensajes));
    }

    public class ModeloNoSeleccionable extends DefaultListSelectionModel {

        /**
         * 
         */
        private static final long serialVersionUID = -1938719531367585786L;

        @Override
        public void setAnchorSelectionIndex(int anchorIndex) {
            
        }

        @Override
        public void setLeadAnchorNotificationEnabled(boolean flag) {
            
        }

        @Override
        public void setLeadSelectionIndex(int leadIndex) {
            
        }

        @Override
        public void setSelectionInterval(int index0, int index1) {
            
        }
    }

    public class ChatListModel extends AbstractListModel<Mensaje> {

        /**
         * 
         */
        private static final long serialVersionUID = 4015518047830903054L;

        private List<Mensaje> mensajes;

        public ChatListModel(List<Mensaje> mensajes) {
            this.mensajes = mensajes;
        }

        @Override
        public int getSize() {
            return mensajes.size();
        }

        @Override
        public Mensaje getElementAt(int index) {
            return mensajes.get(index);
        }
    }

    /**
     * Desplazar chat automáticamente al final si hay un mensaje nuevo
     * 
     * @see <a href="https://stackoverflow.com/questions/5147768/scroll-jscrollpane-to-bottom">
     *          https://stackoverflow.com/questions/5147768/scroll-jscrollpane-to-bottom
     *      </a>
     *
     */
    public class AvanzarAlFinalAdjustmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            int maximum = e.getAdjustable().getMaximum();
            if (prevMax != maximum) {
                prevMax = maximum;
                e.getAdjustable().setValue(maximum);
            }
        }
    }
}
