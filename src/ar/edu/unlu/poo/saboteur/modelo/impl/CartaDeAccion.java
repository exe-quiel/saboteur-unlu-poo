package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.util.Arrays;

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

    public static boolean esCartaDeHerramientaRota(CartaDeAccion cartaDeAccion) {
        return Arrays.asList(TipoCartaAccion.LAMPARA_ROTA, TipoCartaAccion.CARRETILLA_ROTA, TipoCartaAccion.PICO_ROTO)
                .contains(cartaDeAccion.getTipo());
    }

    public static boolean esCartaDeHerramientaReparada(CartaDeAccion cartaDeAccion) {
        return Arrays.asList(TipoCartaAccion.LAMPARA_REPARADA, TipoCartaAccion.CARRETILLA_REPARADA, TipoCartaAccion.PICO_REPARADO)
                .contains(cartaDeAccion.getTipo());
    }
}
