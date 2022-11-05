package ar.edu.unlu.poo.saboteur.modelo;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public interface IChat extends IObservableRemoto {

    public void enviarMensaje(Mensaje mensaje) throws RemoteException;

    public List<Mensaje> getMensajes() throws RemoteException;

    public String generarNombreJugador() throws RemoteException;
}
