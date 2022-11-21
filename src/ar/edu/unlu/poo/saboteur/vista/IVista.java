package ar.edu.unlu.poo.saboteur.vista;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;

public interface IVista {

    public void iniciar();

    public void actualizarMensajes();

    public void actualizarVistaJugadores(List<IJugador> jugadores);

    public void actualizarJugadoresTablero();

    public void actualizarTablero(List<CartaDeTunel> tablero);

    public void actualizarTablero();

    public void iniciarRonda(Evento evento);

    public IJugador getJugador();

    public void setJugador(IJugador obtenerJugador);

    public void actualizarPanelTablero();

    public void mostrarTablero();

    public void mostrarResultados(TipoEvento tipoEvento);

    public void actualizarMano();

}
