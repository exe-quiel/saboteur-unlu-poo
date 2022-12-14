package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
import ar.edu.unlu.poo.saboteur.util.GUIHelper;
import ar.edu.unlu.poo.saboteur.vista.IVista;

/**
 * Representa un espacio (slot) en el tablero, que puede estar libre o no.
 *
 */
public class PanelCartaTablero extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = 5370723875059488045L;

    private CartaDeJuego carta;
    private Integer x;
    private Integer y;

    public PanelCartaTablero(CartaDeJuego carta, Integer x, Integer y, IVista vista) {
        super();
        this.x = x;
        this.y = y;
        this.setCarta(carta);
        this.setOpaque(true);

        addMouseListener(new CartaTableroMouseAdapter(vista));
    }

    public CartaDeJuego getCarta() {
        return carta;
    }

    public void setCarta(CartaDeJuego carta) {
        this.carta = carta;

        this.setText(GUIHelper.obtenerRepresentacionGraficaCarta(carta));
        if (carta != null) {
            this.carta.setPosicion(this.x, this.y);

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

    public Integer getPosX() {
        return this.x;
    }

    public Integer getPosY() {
        return this.y;
    }

    public class CartaTableroMouseAdapter extends MouseAdapter {

        private IVista vista;

        public CartaTableroMouseAdapter(IVista vista) {
            this.vista = vista;
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            IJugador jugadorCliente = vista.getJugador();
            System.out.println(jugadorCliente.getNombre() + " -> " + jugadorCliente.esMiTurno());
            if (!jugadorCliente.esMiTurno()) {
                System.err.println("No es tu turno todav??a");
                return;
            }
            if (!jugadorCliente.getHerramientasRotas().isEmpty()) {
                System.err.println("Ten??s herramientas rotas");
                return;
            }
            CartaDeJuego cartaSeleccionada = vista.getCartaSeleccionada();
            if (cartaSeleccionada == null) {
                System.err.println("Ten??s que seleccionar una carta primero");
                return;
            }
            boolean resultadoAccion = false;
            cartaSeleccionada.setPosicion(getPosX(), getPosY());
            if (cartaSeleccionada instanceof CartaDeTunel) {
                boolean estaLibre = ((PanelCartaTablero) event.getComponent()).getCarta() == null;
                if (estaLibre) {
                    resultadoAccion = vista.getControlador().jugarCarta((CartaDeTunel) cartaSeleccionada);
                } else {
                    System.err.println("Ya hay una carta en ese lugar");
                }
            } else if (cartaSeleccionada instanceof CartaDeAccion) {
                resultadoAccion = vista.getControlador().jugarCarta((CartaDeAccion) cartaSeleccionada);
            }
            if (resultadoAccion) {
                vista.getControlador().avanzar();
            } else {
                cartaSeleccionada.setPosicion(null, null);
                System.err.println("Ocurri?? un error");
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
    }
}