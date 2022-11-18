package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.util.Arrays;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;

public class CartaDeAccion implements CartaDeJuego {

    private List<TipoCartaAccion> tipos;
    private int x;
    private int y;

    public CartaDeAccion() {
        super();
        this.x = -1;
        this.y = -1;
    }

    public CartaDeAccion(List<TipoCartaAccion> tipos) {
        this();
        this.tipos = tipos;
    }

    public CartaDeAccion(TipoCartaAccion tipo) {
        this(Arrays.asList(tipo));
    }

    public List<TipoCartaAccion> getTipos() {
        return tipos;
    }

    public boolean esCartaDeHerramientaReparada() {
        return this.tipos
                .stream()
                .anyMatch(TipoCartaAccion::esCartaDeHerramientaReparada);
    }

    public boolean esCartaDeHerramientaRota() {
        return this.tipos
                .stream()
                .anyMatch(TipoCartaAccion::esCartaDeHerramientaRota);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
