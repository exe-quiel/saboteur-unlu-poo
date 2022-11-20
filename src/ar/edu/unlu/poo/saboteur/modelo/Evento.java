package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;

public class Evento implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7521774005062357442L;
    private IJugador jugadorOrigen;
    private IJugador jugadorDestino;

    private TipoEvento tipoEvento;
    private CartaDeJuego carta;
    private List<IJugador> jugadores;
    private List<CartaDeTunel> tablero;

    public Evento(TipoEvento tipoEvento) {
        super();
        this.tipoEvento = tipoEvento;
    }

    public Evento(TipoEvento tipoEvento, IJugador jugadorOrigen) {
        super();
        this.tipoEvento = tipoEvento;
        this.jugadorOrigen = jugadorOrigen;
    }

    public Evento(TipoEvento tipoEvento, List<IJugador> jugadores) {
        super();
        this.tipoEvento = tipoEvento;
        this.jugadores = jugadores;
    }

    public Evento(TipoEvento tipoEvento, List<IJugador> jugadores, List<CartaDeTunel> tablero) {
        super();
        this.tipoEvento = tipoEvento;
        this.jugadores = jugadores;
        this.tablero = tablero;
    }

    /**
     * Este constructor se debe usar cuando un jugador usa una carta que se aplica sobre la grilla.
     * 
     * Cada cliente debe implementar saber qué hacer dependiendo del tipo de carta. 
     * 
     * Este constructor se debe usar cuando un jugador:
     * 
     *  <ul>
     *      <li>Usa la carta de mapa</li>
     *      <li>Usa una carta de túnel</li>
     *      <li>Usa una carta de derrumbe</li>
     *  </ul>
     * 
     * @param tipoEvento
     * @param idJugadorOrigen
     * @param idCarta
     * @param x
     * @param y
     */
    public Evento(IJugador jugadorOrigen, CartaDeTunel carta) {
        this(TipoEvento.USA_CARTA);
        this.jugadorOrigen = jugadorOrigen;
        this.carta = carta;
    }

    /**
     * Solo para cuando un jugador coloca una carta en la pila de descarte
     * 
     * @param idJugadorOrigen
     */
    public Evento(IJugador jugadorOrigen) {
        this(TipoEvento.DESCARTA);
        this.jugadorOrigen = jugadorOrigen;
    }

    /**
     * Constructor para cuando un jugador le aplica una carta de herramienta rota o reparada a otro jugador o a sí mismo
     *  
     * @param tipoEvento
     * @param idJugadorOrigen
     * @param idJugadorDestino
     * @param idCarta
     */
    public Evento(IJugador jugadorOrigen, IJugador jugadorDestino, CartaDeAccion carta) {
        this(TipoEvento.USA_CARTA);
        this.jugadorOrigen = jugadorOrigen;
        this.jugadorDestino = jugadorDestino;
        this.carta = carta;
    }

    public Evento(TipoEvento tipoEvento, CartaDeTunel carta, IJugador jugador) {
        this(tipoEvento);
        this.carta = carta;
    }

    public Evento(TipoEvento tipoEvento, CartaDeTunel cartaADerrumbar) {
        this(tipoEvento);
        this.carta = cartaADerrumbar;
    }

    public IJugador getJugadorOrigen() {
        return jugadorOrigen;
    }

    public IJugador getJugadorDestino() {
        return jugadorDestino;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public CartaDeJuego getCarta() {
        return carta;
    }

    public List<IJugador> getJugadores() {
        return jugadores;
    }

    public List<CartaDeTunel> getTablero() {
        return tablero;
    }

    public IJugador obtenerJugadorCorrespondiente(IJugador jugador) {
        return (IJugador) jugadores
                .stream()
                .filter(j -> j.equals(jugador))
                .findFirst()
                .orElse(null);
    }
}
