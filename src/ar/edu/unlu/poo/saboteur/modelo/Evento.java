package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;

public class Evento implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7521774005062357442L;
    private String idJugador;
    private TipoEvento tipoEvento;

    public Evento(TipoEvento tipoEvento) {
        super();
        this.tipoEvento = tipoEvento;
    }

    public Evento(String idJugador, TipoEvento tipoEvento) {
        super();
        this.idJugador = idJugador;
        this.tipoEvento = tipoEvento;
    }

    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}
