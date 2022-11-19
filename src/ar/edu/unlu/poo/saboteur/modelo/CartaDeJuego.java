package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;

public abstract class CartaDeJuego implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3715044853329504112L;
    private int id;
    private Integer x;
    private Integer y;

    public CartaDeJuego(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setPosicion(Integer x, Integer y) {
        this.setX(x);
        this.setY(y);
    }
}
