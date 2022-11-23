package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;

public class CartaDePuntos implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5295887972173370164L;
    private int id;
    private int puntos;

    public CartaDePuntos(int id, int puntos) {
        super();
        this.puntos = puntos;
    }

    public int getId() {
        return id;
    }



    public int getPuntos() {
        return puntos;
    }
}
