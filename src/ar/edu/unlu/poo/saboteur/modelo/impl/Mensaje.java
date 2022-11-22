package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;

import ar.edu.unlu.poo.saboteur.modelo.IJugador;

public class Mensaje implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4980136064296825830L;

    private IJugador jugador;
    private String texto;

    public Mensaje(String texto) {
        this(null, texto);
    }

    public Mensaje(IJugador jugador, String texto) {
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
        return this.jugador.getNombre();
    }

    public IJugador getJugador() {
        return jugador;
    }

    public void setJugador(IJugador jugador) {
        this.jugador = jugador;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
