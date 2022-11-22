package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
import ar.edu.unlu.poo.saboteur.util.GUIHelper;
import ar.edu.unlu.poo.saboteur.vista.IVista;

/**
 * Representa un espacio (slot) en el tablero, que puede estar libre o no.
 *
 */
public class CartaTablero extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = 5370723875059488045L;

    private CartaDeJuego carta;
    private Integer x;
    private Integer y;

    public CartaTablero(CartaDeJuego carta, Integer x, Integer y, IVista vista) {
        super();
        this.setFont(GUIConstants.PLAIN_FONT);
        this.x = x;
        this.y = y;
        this.setCarta(carta);
        this.setOpaque(true);

        ControladorJuego controlador = vista.getControlador();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                IJugador jugadorCliente = vista.getJugador();
                System.out.println(jugadorCliente.getNombre() + " -> " + jugadorCliente.esMiTurno());
                if (!jugadorCliente.esMiTurno()) {
                    System.err.println("No es tu turno todavía");
                    return;
                }
                if (!jugadorCliente.getHerramientasRotas().isEmpty()) {
                    System.err.println("Tenés herramientas rotas");
                    return;
                }
                CartaDeJuego cartaSeleccionada = vista.getCartaSeleccionada();
                if (cartaSeleccionada == null) {
                    System.err.println("Tenés que seleccionar una carta primero");
                    return;
                }
                boolean resultadoAccion = false;
                cartaSeleccionada.setPosicion(getPosX(), getPosY());
                if (cartaSeleccionada instanceof CartaDeTunel) {
                    boolean estaLibre = ((CartaTablero) event.getComponent()).getCarta() == null;
                    if (estaLibre) {
                        resultadoAccion = controlador.jugarCarta((CartaDeTunel) cartaSeleccionada);
                    } else {
                        System.err.println("Ya hay una carta en ese lugar");
                    }
                } else if (cartaSeleccionada instanceof CartaDeAccion) {
                    resultadoAccion = controlador.jugarCarta((CartaDeAccion) cartaSeleccionada);
                }
                if (resultadoAccion) {
                    controlador.avanzar();
                } else {
                    cartaSeleccionada.setPosicion(null, null);
                    System.err.println("Ocurrió un error");
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
        if (carta != null) {
            this.carta.setPosicion(this.x, this.y);
        }
    }

    public Integer getPosX() {
        return this.x;
    }

    public Integer getPosY() {
        return this.y;
    }
}