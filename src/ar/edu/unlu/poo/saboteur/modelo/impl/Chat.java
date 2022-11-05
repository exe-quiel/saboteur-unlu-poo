package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IChat;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

public class Chat extends ObservableRemoto implements IChat {

    private byte indiceNombreJugador = 1;
    private byte indiceJugadorTurno = 0;
    private List<Jugador> jugadores = new LinkedList<>();
    private List<Mensaje> mensajes;

    public Chat() {
        this.mensajes = new ArrayList<>();
    }

    @Override
    public void enviarMensaje(Mensaje mensaje) throws RemoteException {
        System.out.println(String.format("Jugador [%s] dijo [%s]", mensaje.getJugador(), mensaje.getTexto()));
        this.mensajes.add(mensaje);
        try {
            //this.notificarObservadores(new Evento(TipoEvento.NUEVO_MENSAJE));
            incrementarTurno();
            this.notificarObservadores(new Evento(jugadores.get(indiceJugadorTurno).getNombre(), TipoEvento.NUEVO_MENSAJE));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Mensaje> getMensajes() throws RemoteException {
        return mensajes;
    }

    @Override
    public String generarNombreJugador() throws RemoteException {
        String nombreJugador = "Jugador-" + indiceNombreJugador;
        jugadores.add(new Jugador(nombreJugador));
        indiceNombreJugador++;
        return nombreJugador;
    }

    private void incrementarTurno() {
        this.indiceJugadorTurno++;
        if (this.jugadores.size() == indiceJugadorTurno) {
            this.indiceJugadorTurno = 0;
        }
    }
}
