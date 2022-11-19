package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;

import ar.edu.unlu.poo.saboteur.modelo.IJugadorBase;

public class Mensaje implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4980136064296825830L;

    private IJugadorBase jugador;
    private String texto;

    public Mensaje(IJugadorBase jugador, String texto) {
        super();
        this.jugador = jugador;
        this.texto = texto;
    }

    public boolean esDelSistema() {
        return this.jugador == null;
    }

    public String obtenerOrigen() {
        if (esDelSistema()) {
            return "SISTEMA";
        }
        return this.jugador.getId();
    }

    public IJugadorBase getJugador() {
        return jugador;
    }

    public void setJugador(IJugadorBase jugador) {
        this.jugador = jugador;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
