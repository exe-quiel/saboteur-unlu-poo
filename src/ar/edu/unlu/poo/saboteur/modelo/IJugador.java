package ar.edu.unlu.poo.saboteur.modelo;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;

public interface IJugador extends IJugadorBase {

    public List<CartaDeJuego> getMano();

    public int calcularPuntaje();

    public RolJugador getRol();

    public void setRol(RolJugador rol);

    public void marcarListo();

    public boolean getListo();

    public void recibirCartas(List<CartaDeJuego> mano);

    public void setHerramientasRotas(List<CartaDeAccion> herramientasRotas);

    public boolean romperHerramienta(CartaDeAccion cartaDeAccion);

    public boolean repararHerramienta(CartaDeAccion cartaDeAccion);

    public void removerCartaDeLaMano(CartaDeJuego carta);
}
