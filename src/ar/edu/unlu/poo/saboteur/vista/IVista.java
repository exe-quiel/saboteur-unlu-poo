package ar.edu.unlu.poo.saboteur.vista;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;

public interface IVista {

    public void iniciar();

    public void actualizarMensajes();

    public void actualizarJugadores(List<IJugador> jugadores);

    public void actualizarJugadores();

    public void actualizarTablero(List<CartaDeTunel> tablero);

    public void actualizarTablero();

    public void iniciarJuego(Evento evento);

    public IJugador getJugador();

    public void setJugador(IJugador obtenerJugador);

    public void actualizar();

    public void mostrarResultados();

    public void actualizarMano();
}
