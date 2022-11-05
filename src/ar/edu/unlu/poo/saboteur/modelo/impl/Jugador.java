package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;

public class Jugador implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3336584393172378611L;
    private final String nombre;
    

    public Jugador(String nombre) {
        super();
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
