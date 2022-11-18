package ar.edu.unlu.poo.saboteur.modelo;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;

public interface IJugadorBase {

    public String getId();

    public List<CartaDeAccion> getHerramientasRotas();
}
