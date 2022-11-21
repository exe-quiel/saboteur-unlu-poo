package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;
import java.util.List;

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
}
