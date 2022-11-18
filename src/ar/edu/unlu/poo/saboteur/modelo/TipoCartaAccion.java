package ar.edu.unlu.poo.saboteur.modelo;

import java.util.Arrays;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;

public enum TipoCartaAccion {

    LAMPARA_ROTA(null),
    CARRETILLA_ROTA(null),
    PICO_ROTO(null),
    LAMPARA_REPARADA(LAMPARA_ROTA),
    CARRETILLA_REPARADA(CARRETILLA_ROTA),
    PICO_REPARADO(PICO_ROTO),
    MAPA(null),
    DERRUMBE(null);

    private TipoCartaAccion cartaQueRepara;

    TipoCartaAccion(TipoCartaAccion cartaQueRepara) {
        this.cartaQueRepara = cartaQueRepara;
    }

    public boolean esCartaDeHerramientaRota() {
        return Arrays.asList(LAMPARA_ROTA, CARRETILLA_ROTA, PICO_ROTO).contains(this);
    }

    public boolean esCartaDeHerramientaReparada() {
        return Arrays.asList(LAMPARA_REPARADA, CARRETILLA_REPARADA, PICO_REPARADO).contains(this);
    }


    public boolean arregla(CartaDeAccion carta) {
        return carta.getTipos()
                .stream()
                .anyMatch(tipo -> tipo == this.getCartaQueRepara());
    }

    public TipoCartaAccion getCartaQueRepara() {
        return cartaQueRepara;
    }
}
