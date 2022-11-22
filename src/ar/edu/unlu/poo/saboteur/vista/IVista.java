package ar.edu.unlu.poo.saboteur.vista;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;

public interface IVista {

    public void iniciar();

    public IJugador getJugador();

    public void setJugador(IJugador jugador);

    public void iniciarRonda();

    public void actualizarJuego();

    public void actualizarJugadores();

    public void actualizarChat();

    public void actualizarResultados(TipoEvento tipoEvento);

    public void setCartaSeleccionada(CartaDeJuego cartaSeleccionada);

    public ControladorJuego getControlador();

    public CartaDeJuego getCartaSeleccionada();



}
