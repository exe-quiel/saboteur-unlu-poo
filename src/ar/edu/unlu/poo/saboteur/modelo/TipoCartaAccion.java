package ar.edu.unlu.poo.saboteur.modelo;

import java.util.Arrays;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;

public enum TipoCartaAccion {

    LAMPARA_ROTA("Lámpara rota"),
    CARRETILLA_ROTA("Carretilla rota"),
    PICO_ROTO("Pico roto"),
    LAMPARA_REPARADA(LAMPARA_ROTA, "Lámpara reparada"),
    CARRETILLA_REPARADA(CARRETILLA_ROTA, "Carretilla reparada"),
    PICO_REPARADO(PICO_ROTO, "Pico reparado"),
    MAPA("Mapa"),
    DERRUMBE("Derrumbe");

    private TipoCartaAccion cartaQueRepara;
    private String nombreUserFriendly;

    TipoCartaAccion(String nombreUserFriendly) {
        this.cartaQueRepara = null;
        this.nombreUserFriendly = nombreUserFriendly;
    }

    TipoCartaAccion(TipoCartaAccion cartaQueRepara, String nombreUserFriendly) {
        this.cartaQueRepara = cartaQueRepara;
        this.nombreUserFriendly = nombreUserFriendly;
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

    @Override
    public String toString() {
        return this.nombreUserFriendly;
    }
}
