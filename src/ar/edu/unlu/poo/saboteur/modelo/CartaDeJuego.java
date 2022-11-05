package ar.edu.unlu.poo.saboteur.modelo;

public abstract class CartaDeJuego {

    private String descripcion;

    public CartaDeJuego(String descripcion) {
        super();
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
