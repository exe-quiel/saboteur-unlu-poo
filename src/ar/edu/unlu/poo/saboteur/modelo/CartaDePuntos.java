package ar.edu.unlu.poo.saboteur.modelo;

public class CartaDePuntos extends CartaDeJuego {

    private byte puntos;

    public CartaDePuntos(byte puntos, String descripcion) {
        super(descripcion);
    }

    public byte getPuntos() {
        return puntos;
    }
}
