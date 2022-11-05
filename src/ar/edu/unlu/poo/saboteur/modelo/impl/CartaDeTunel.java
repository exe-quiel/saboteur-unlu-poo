package ar.edu.unlu.poo.saboteur.modelo.impl;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;

public class CartaDeTunel extends CartaDeJuego {

    private TipoCartaTunel tipo;
    private boolean sinSalida;
    private Entrada[] entradas;

    public CartaDeTunel(TipoCartaTunel tipo, boolean sinSalida, Entrada[] entradas, String descripcion) {
        super(descripcion);
        this.tipo = tipo;
        this.sinSalida = sinSalida;
        this.entradas = entradas;
    }

    public TipoCartaTunel getTipo() {
        return tipo;
    }

    public boolean isSinSalida() {
        return sinSalida;
    }

    public Entrada[] getEntradas() {
        return entradas;
    }
}
