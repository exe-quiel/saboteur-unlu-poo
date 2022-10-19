package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.Eventos;
import ar.edu.unlu.poo.saboteur.modelo.IChat;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

public class Chat extends ObservableRemoto implements IChat {

    private List<String> mensajes;

    public Chat() {
        this.mensajes = new ArrayList<>();
    }

    @Override
    public void enviarMensaje(String mensaje) throws RemoteException {
        System.out.println("Legó mensaje: " + mensaje);
        this.mensajes.add(mensaje);
        try {
            this.notificarObservadores(Eventos.NUEVO_MENSAJE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getMensajes() throws RemoteException {
        return mensajes;
    }
}
