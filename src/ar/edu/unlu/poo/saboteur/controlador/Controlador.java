package ar.edu.unlu.poo.saboteur.controlador;

import java.rmi.RemoteException;

import ar.edu.unlu.poo.saboteur.modelo.Eventos;
import ar.edu.unlu.poo.saboteur.modelo.IChat;
import ar.edu.unlu.poo.saboteur.vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public class Controlador implements IControladorRemoto {

    private IChat chat;
    private IVista vista;

    public Controlador() {

    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    @Override
    public void actualizar(IObservableRemoto arg0, Object arg1) throws RemoteException {
        if (arg1 instanceof Eventos) {
            switch ((Eventos) arg1) {
            case NUEVO_MENSAJE:
                vista.mostrarMensajes(this.chat.getMensajes());
                break;
            default:
                break;
            }
        }
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T arg0) throws RemoteException {
        this.chat = (IChat) arg0;
    }

    public void enviarMensaje(String mensaje) {
        try {
            this.chat.enviarMensaje(mensaje);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
