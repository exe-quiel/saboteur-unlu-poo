package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;

public class Evento implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7521774005062357442L;
    private String jugadorOrigen;
    private String jugadorDestino;
    private byte x;
    private byte y;
    private TipoEvento tipoEvento;

    public Evento(TipoEvento tipoEvento, String nombreJugador) {
        super();
        this.tipoEvento = tipoEvento;
        this.jugadorOrigen = nombreJugador;
    }

    public Evento(TipoEvento tipoEvento, byte x, byte y) {
        super();
        this.tipoEvento = tipoEvento;
        this.x = x;
        this.y = y;
    }

    public Evento(TipoEvento tipoEvento) {
        super();
        this.tipoEvento = tipoEvento;
    }

    public Evento(TipoEvento tipoEvento, String nombreJugador, byte x, byte y) {
        this(tipoEvento, x, y);
        this.jugadorOrigen = nombreJugador;
    }

    public String getJugadorOrigen() {
        return jugadorOrigen;
    }

    public String getJugadorDestino() {
        return jugadorDestino;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }
}
