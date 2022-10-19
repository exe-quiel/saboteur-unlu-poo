package ar.edu.unlu.poo.saboteur.modelo;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public interface IChat extends IObservableRemoto {

	public void enviarMensaje(String mensaje) throws RemoteException;
	public List<String> getMensajes() throws RemoteException;
}
