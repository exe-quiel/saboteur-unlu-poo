package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

public class Juego extends ObservableRemoto implements IJuego {

    private int[][] grilla = new int[6][9];
    private byte indiceIdJugador = 1;
    private byte indiceJugadorTurno = 0;
    private List<Jugador> jugadores = new LinkedList<>();
    private List<Mensaje> mensajes;

    private boolean partidaEmpezo;

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
    public void jugarCarta(byte idCarta, byte x, byte y) {
        System.out.println(String.format("Carta %s en (%s,%s)", idCarta, x, y));
        if (this.grilla[y][x] != 1) {
            this.grilla[y][x] = 1;
            try {
                incrementarTurno();
                this.notificarObservadores(new Evento(jugadores.get(indiceJugadorTurno).getId(), idCarta, x, y));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void jugarCarta(String idJugadorDestino, byte idCarta) throws RemoteException {
        try {
            incrementarTurno();
            this.notificarObservadores(new Evento(jugadores.get(indiceJugadorTurno).getId(), idJugadorDestino, idCarta));
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
            Evento evento = new Evento(TipoEvento.NUEVO_MENSAJE, jugadores.get(indiceJugadorTurno).getId());
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
    public String generarIdJugador() throws RemoteException {
        String idJugador = "Jugador-" + indiceIdJugador;
        //this.notificarObservadores(new Evento(TipoEvento.NUEVO_JUGADOR));
        jugadores.add(new Jugador(idJugador));
        indiceIdJugador++;
        return idJugador;
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
                .map(jugador -> jugador.getId())
                .collect(Collectors.toList());
    }

    @Override
    public int[][] getGrilla() throws RemoteException {
        return grilla;
    }

    @Override
    public void marcarListo(String idJugador) throws RemoteException {
        jugadores
            .stream()
            .filter(jugador -> jugador.getId().equals(idJugador))
            .findFirst()
            .ifPresent(Jugador::marcarListo);
        System.out.println("[" + idJugador + "] está listo");

        if (jugadores.stream().allMatch(Jugador::getListo)) {
            comenzarJuego(idJugador);
        }
    }

    private void comenzarJuego(String idJugador) throws RemoteException {
        System.out.println("Todos listos");
        this.partidaEmpezo = true;
        for (Jugador jugador : jugadores) {
            Random random = new Random();
            List<Byte> mano = new ArrayList<>();
            List<Byte> herramientasRotas = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                mano.add((byte) random.nextInt(50));
                if (i < 3) {
                    herramientasRotas.add((byte) random.nextInt(50));
                }
            }
            System.out.println("Jugador [" + jugador.getId() + "] mano " + mano);
            jugador.recibirCartas(mano);
            jugador.setHerramientasRotas(herramientasRotas);
        }
        Evento evento = new Evento(TipoEvento.INICIA_JUEGO, jugadores);
        this.notificarObservadores(evento);
        this.enviarMensaje(new Mensaje("Sistema", "Comenzó el juego"));
    }

    @Override
    public void aplicarCartaDeHerramienta(byte cartaSeleccionada, String idJugadorDestino) throws RemoteException {
        Jugador jugadorDestino = this.jugadores
            .stream()
            .filter(jugador -> jugador.getId().equals(idJugadorDestino))
            .findFirst()
            .get();

        if (!jugadorDestino.getHerramientasRotas().contains(cartaSeleccionada)) {
            jugadorDestino.getHerramientasRotas().add(cartaSeleccionada);
        }
        // TODO EXE - Enviar mensaje del sistema con el evento
        // ("Jugador X aplicó a jugador Y la herramienta rota Z")
        String mensaje = String.format("[%s] aplicó a [%s] la carta [%s]", this.obtenerIdDelJugadorActual(), idJugadorDestino, cartaSeleccionada);
        this.enviarMensaje(new Mensaje("Sistema", mensaje));
    }

    private String obtenerIdDelJugadorActual() {
        return this.jugadores.get(indiceJugadorTurno).getId();
    }
}
