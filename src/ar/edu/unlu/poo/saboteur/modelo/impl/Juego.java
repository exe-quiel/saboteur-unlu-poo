package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

public class Juego extends ObservableRemoto implements IJuego {

    private int[][] grilla = new int[6][9];
    private byte indiceNombreJugador = 1;
    private byte indiceJugadorTurno = 0;
    private List<Jugador> jugadores = new LinkedList<>();
    private List<Mensaje> mensajes;

    public Juego() {
        super();
        this.mensajes = new ArrayList<>();
        this.reestablecerGrilla();

        
    }

    @Override
    public void reestablecerGrilla() {
        for (int y = 0; y < grilla.length; y++) {
            for (int x = 0; x < grilla[y].length; x++) {
                grilla[y][x] = 0;
            }
        }
    }

    @Override
    public void jugarCarta(byte x, byte y) {
        System.out.println(String.format("(%s,%s)", x, y));
        this.grilla[y][x] = 1;
        try {
            this.notificarObservadores(new Evento(TipoEvento.USA_CARTA, x, y));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void enviarMensaje(Mensaje mensaje) throws RemoteException {
        System.out.println(String.format("Jugador [%s] dijo [%s]", mensaje.getJugador(), mensaje.getTexto()));
        this.mensajes.add(mensaje);
        try {
            //this.notificarObservadores(new Evento(TipoEvento.NUEVO_MENSAJE));
            incrementarTurno();
            Evento evento = new Evento(TipoEvento.NUEVO_MENSAJE, jugadores.get(indiceJugadorTurno).getNombre());
            this.notificarObservadores(evento);
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
        //this.notificarObservadores(new Evento(TipoEvento.NUEVO_JUGADOR));
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

    @Override
    public List<String> getDatosJugadores() throws RemoteException {
        return jugadores
                .stream()
                .map(jugador -> jugador.getNombre())
                .collect(Collectors.toList());
    }

    @Override
    public int[][] getGrilla() throws RemoteException {
        return grilla;
    }
}
