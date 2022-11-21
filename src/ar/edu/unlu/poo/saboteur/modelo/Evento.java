package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;

public class Evento implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7521774005062357442L;

    private TipoEvento tipoEvento;

    public Evento(TipoEvento tipoEvento) {
        super();
        this.tipoEvento = tipoEvento;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }
}
