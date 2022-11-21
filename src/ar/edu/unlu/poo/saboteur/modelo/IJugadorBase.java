package ar.edu.unlu.poo.saboteur.modelo;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;

public interface IJugadorBase {

    public int getId();

    public String getNombre();

    public List<CartaDeAccion> getHerramientasRotas();
}
