package ar.edu.unlu.poo.saboteur.modelo;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public interface IJuego extends IObservableRemoto {

    public boolean jugarCarta(CartaDeTunel carta) throws RemoteException;

    public boolean jugarCarta(IJugador jugadorDestino, CartaDeAccion carta) throws RemoteException;

    public boolean jugarCarta(CartaDeAccion carta) throws RemoteException;

    public void enviarMensaje(Mensaje mensaje) throws RemoteException;

    public List<Mensaje> getMensajes() throws RemoteException;

    public IJugador crearJugador() throws RemoteException;

    public List<IJugador> obtenerJugadores() throws RemoteException;

    public List<CartaDeTunel> obtenerTablero() throws RemoteException;

    public void marcarListo(IJugador jugadorCliente) throws RemoteException;

    public boolean descartar(CartaDeJuego carta, IJugador jugador) throws RemoteException;

    public boolean descartar(CartaDeJuego carta) throws RemoteException;

    public CartaDeJuego tomarCarta() throws RemoteException;

    public void salir(IJugador jugadorCliente) throws RemoteException ;

    public void avanzar() throws RemoteException;
}
