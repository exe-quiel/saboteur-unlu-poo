package ar.edu.unlu.poo.saboteur.modelo;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public interface IJuego extends IObservableRemoto {

    public void jugarCarta(byte x, byte y) throws RemoteException;

    public void reestablecerGrilla() throws RemoteException;

    public void enviarMensaje(Mensaje mensaje) throws RemoteException;

    public List<Mensaje> getMensajes() throws RemoteException;

    public String generarNombreJugador() throws RemoteException;

    public List<String> getDatosJugadores() throws RemoteException;

    public int[][] getGrilla() throws RemoteException;
}
