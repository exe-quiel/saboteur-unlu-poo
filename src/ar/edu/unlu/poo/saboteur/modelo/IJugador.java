package ar.edu.unlu.poo.saboteur.modelo;

import java.util.List;

public interface IJugador extends IJugadorBase {

    public List<Byte> getMano();

    public int getPuntaje();

    public RolJugador getRol();

    public void marcarListo();

    public boolean getListo();

    public void recibirCartas(List<Byte> mano);

    public void setHerramientasRotas(List<Byte> herramientasRotas);
}
