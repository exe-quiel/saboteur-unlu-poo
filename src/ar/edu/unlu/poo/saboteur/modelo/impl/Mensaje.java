package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;

public class Mensaje implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4980136064296825830L;

    private String jugador;
    private String texto;

    public Mensaje(String jugador, String texto) {
        super();
        this.jugador = jugador;
        this.texto = texto;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
