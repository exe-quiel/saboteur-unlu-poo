package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class VistaGrafica implements IVista {

    // private GeneradorDeImagenes generadorDeImagenes =
    // GeneradorDeImagenes.getInstance();

    final static String PANEL_JUEGO = "PanelDeJuego";
    final static String PANEL_RESULTADOS = "PanelDeResultados";

    private Container secciones;
    private CardLayout paneles;

    private ControladorJuego controladorJuego;

    private IJugador jugadorCliente;
    private CartaDeJuego cartaSeleccionada;

    private PanelResultados panelResultados;
    private PanelPartida panelPartida;
    private PanelChat panelChat;

    public VistaGrafica(ControladorJuego controladorJuego, IJugador jugadorCliente) {
        this.jugadorCliente = jugadorCliente;

        this.controladorJuego = controladorJuego;
        this.controladorJuego.setVista(this);

        EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();
            frame.setSize(1280, 720);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            //frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            /*
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    controladorJuego.salir();
                    System.exit(0);
                }
            });
            */
            frame.setTitle("Saboteur - " + jugadorCliente.getNombre());

            Container panelPrincipal = frame.getContentPane();
            //panelPrincipal.setLayout(new GridLayout(1, 2));
            //panelPrincipal.setLayout(new GridBagLayout());
            panelPrincipal.setLayout(new BorderLayout());
            //GridBagConstraints panelPrincipalconstraints = new GridBagConstraints();

            secciones = new JPanel();
            secciones.setLayout(new CardLayout());

            /*
            panelPrincipalconstraints.fill = GridBagConstraints.BOTH;
            panelPrincipalconstraints.gridx = 0;
            panelPrincipalconstraints.gridy = 0;
            panelPrincipalconstraints.gridwidth = 1;
            panelPrincipalconstraints.gridheight = 1;
            panelPrincipalconstraints.weightx = 0.9;
            panelPrincipalconstraints.weighty = 1.0;
            */

            panelPrincipal.add(secciones, BorderLayout.CENTER /*panelPrincipalconstraints*/);

            paneles = (CardLayout) secciones.getLayout();

            panelPartida = new PanelPartida(this);
            secciones.add(panelPartida, PANEL_JUEGO);

            panelResultados = new PanelResultados(this);
            secciones.add(panelResultados, PANEL_RESULTADOS);

            panelChat = new PanelChat(this);

            /*
            GridBagConstraints panelChatConstraints = new GridBagConstraints();
            panelChatConstraints.fill = GridBagConstraints.BOTH;
            panelChatConstraints.gridx = 1;
            panelChatConstraints.gridy = 0;
            panelChatConstraints.gridwidth = 1;
            panelChatConstraints.gridheight = 1;
            panelChatConstraints.weightx = 0.1;
            panelChatConstraints.weighty = 1.0;
            */

            panelPrincipal.add(panelChat, BorderLayout.EAST /*panelChatConstraints*/);

            frame.setVisible(true);

            this.paneles.show(secciones, PANEL_JUEGO);
        });
    }

    @Override
    public void iniciar() {
        
    }

    @Override
    public void actualizarJuego() {
        this.panelPartida.actualizar();
    }

    @Override
    public void actualizarJugadores() {
        this.panelPartida.actualizarJugadores();
    }

    @Override
    public void actualizarChat() {
        this.panelChat.actualizar();
    }

    @Override
    public void iniciarRonda() {
        this.paneles.show(secciones, PANEL_JUEGO);
        this.panelPartida.actualizar();
    }

    @Override
    public IJugador getJugador() {
        return jugadorCliente;
    }

    @Override
    public void actualizarResultados(TipoEvento tipoEvento) {
        paneles.show(secciones, PANEL_RESULTADOS);
        panelResultados.actualizar(tipoEvento);
    }

    @Override
    public ControladorJuego getControlador() {
        return controladorJuego;
    }

    @Override
    public void setCartaSeleccionada(CartaDeJuego cartaSeleccionada) {
        this.cartaSeleccionada = cartaSeleccionada;
    }

    @Override
    public CartaDeJuego getCartaSeleccionada() {
        return cartaSeleccionada;
    }

    @Override
    public void setJugador(IJugador jugador) {
        this.jugadorCliente = jugador;
    }
}
