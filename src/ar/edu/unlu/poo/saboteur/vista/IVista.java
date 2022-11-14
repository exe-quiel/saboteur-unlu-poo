package ar.edu.unlu.poo.saboteur.vista;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;

public interface IVista {

    public void iniciar();

    public void mostrarMensajes(List<Mensaje> mensajes);

    public void cambiarTurno(String idJugador);

    public void actualizarJugadores(List<String> jugadores);

    public void mostrarGrilla(byte idCarta, byte x, byte y);
}
