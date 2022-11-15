package ar.edu.unlu.poo.saboteur.modelo;

import java.util.Arrays;

public enum TipoCartaAccion {

    LAMPARA_ROTA,
    CARRETILLA_ROTA,
    PICO_ROTO,
    LAMPARA_REPARADA,
    CARRETILLA_REPARADA,
    PICO_REPARADO,
    MAPA,
    DERRUMBE;

    public boolean esCartaDeHerramientaRota() {
        return Arrays.asList(LAMPARA_ROTA, CARRETILLA_ROTA, PICO_ROTO).contains(this);
    }

    public boolean esCartaDeHerramientaReparada() {
        return Arrays.asList(LAMPARA_REPARADA, CARRETILLA_REPARADA, PICO_REPARADO).contains(this);
    }
}
