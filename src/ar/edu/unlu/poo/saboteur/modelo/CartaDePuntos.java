package ar.edu.unlu.poo.saboteur.modelo;

public class CartaDePuntos implements CartaDeJuego {

    private byte puntos;

    public CartaDePuntos(byte puntos) {
        super();
        this.puntos = puntos;
    }

    public byte getPuntos() {
        return puntos;
    }
}
