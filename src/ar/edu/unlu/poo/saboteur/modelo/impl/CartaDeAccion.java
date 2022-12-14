package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;

public class CartaDeAccion extends CartaDeJuego {

    /**
     * 
     */
    private static final long serialVersionUID = -3807907674421956310L;

    private List<TipoCartaAccion> tipos;

    public CartaDeAccion(int id) {
        super(id);
    }

    public CartaDeAccion(int id, List<TipoCartaAccion> tipos) {
        this(id);
        this.tipos = tipos;
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
}
