package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
import ar.edu.unlu.poo.saboteur.util.GUIHelper;
import ar.edu.unlu.poo.saboteur.vista.IVista;

/**
 * Representa una carta en la mano del jugador.
 *
 */
public class CartaMano extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = 5370723875059488045L;

    private CartaDeJuego carta;

    public CartaMano(CartaDeJuego carta, IVista vista) {
        super();
        this.setCarta(carta);

        IJugador jugadorCliente = vista.getJugador();
        ControladorJuego controlador = vista.getControlador();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                System.out.println("[" + jugadorCliente.getNombre() + "] -> " + jugadorCliente.esMiTurno());
                if (!jugadorCliente.esMiTurno()) {
                    System.err.println("No es tu turno todavía");
                }
                if (event.getButton() == MouseEvent.BUTTON1) {
                    vista.setCartaSeleccionada(carta);
                    System.out.println("Seleccionaste la carta " + carta.getId());
                } else if (event.getButton() == MouseEvent.BUTTON3) {
                    vista.setCartaSeleccionada(carta);
                    controlador.descartar(carta);
                    controlador.avanzar();
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                ((JLabel) event.getComponent()).setBorder(GUIConstants.LABEL_BORDER);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                ((JLabel) event.getComponent()).setBorder(null);
            }
        });
    }

    public CartaDeJuego getCarta() {
        return carta;
    }

    public void setCarta(CartaDeJuego carta) {
        this.carta = carta;

        this.setText(GUIHelper.obtenerRepresentacionGraficaCarta(carta));

        if (carta instanceof CartaDeTunel) {
            CartaDeTunel cartaDeTunel = (CartaDeTunel) carta;
            if (cartaDeTunel.getTipo() == TipoCartaTunel.INICIO || cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_ORO
                    || cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_PIEDRA) {
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
            }
            if (cartaDeTunel.isVisible()) {
                if (cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_ORO) {
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);
                } else if (cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_PIEDRA) {
                    setBackground(Color.GRAY);
                    setForeground(Color.BLACK);
                }
            }
        }
    }
}