package ar.edu.unlu.poo.saboteur.modelo.impl;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;

public class CartaDeAccion extends CartaDeJuego {

    private TipoCartaAccion tipo;

    public CartaDeAccion(TipoCartaAccion tipo, String descripcion) {
        super(descripcion);
        this.tipo = tipo;
    }

    public TipoCartaAccion getTipo() {
        return tipo;
    }
}
