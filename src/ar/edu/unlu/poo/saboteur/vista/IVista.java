package ar.edu.unlu.poo.saboteur.vista;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;

public interface IVista {

    public void iniciar();

    public void actualizarMensajes();

    public void cambiarTurno(IJugador jugador);

    public void actualizarJugadores(List<IJugador> jugadores);

    public void mostrarTablero(CartaDeTunel carta);

    public void iniciarJuego(Evento evento);

    public IJugador getJugador();

    public void setJugador(IJugador obtenerJugador);
}
