package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

public class Juego extends ObservableRemoto implements IJuego {

    private int[][] grilla = new int[6][9];
    private byte indiceIdJugador = 1;
    private byte indiceJugadorTurno = 0;
    private List<IJugador> jugadores = new LinkedList<>();
    private List<Mensaje> mensajes;

    private List<CartaDeJuego> mazo;
    private List<CartaDeJuego> pilaDeDescarte;

    private List<CartaDeTunel> tablero;
    private CartaDeTunel cartaDeInicio;
    private List<CartaDeTunel> cartasDeDestino;

    private boolean partidaEmpezo;

    public Juego() {
        super();
        this.mensajes = new ArrayList<>();
        this.mazo = new ArrayList<>();
        this.pilaDeDescarte = new ArrayList<>();
        this.cargarCartas();
    }

    /**
     * Genera las instancias de las cartas a partir de los archivos y las coloca en el mazo.
     * 
     */
    private void cargarCartas() {
        this.mazo.addAll(null);
    }

    @Override
    public void jugarCarta(CartaDeTunel carta) {
        System.out.println(String.format("Carta de túnel en (%s,%s)", carta.getX(), carta.getY()));
        if (validarColision(carta)) {
            
            try {
                incrementarTurno();
                this.notificarObservadores(new Evento(jugadores.get(indiceJugadorTurno), carta));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void jugarCarta(IJugador jugadorDestino, CartaDeAccion carta) throws RemoteException {
    
        if (carta.esCartaDeHerramientaRota()) {
            if (!jugadorDestino.getHerramientasRotas().contains(carta)) {
                jugadorDestino.romperHerramienta(carta);
            }
        } else if (carta.esCartaDeHerramientaReparada()) {
            // TODO EXE - Terminar
            //jugadorDestino.
        }
    
        String mensaje = String.format("[%s] aplicó a [%s] la carta [%s]", this.obtenerJugadorDelTurnoActual(), jugadorDestino.getId(), carta);
        this.enviarMensaje(new Mensaje(null, mensaje));
    
        incrementarTurno();
        try {
            this.notificarObservadores(new Evento(jugadores.get(indiceJugadorTurno), jugadorDestino, carta));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void jugarCarta(CartaDeAccion carta) {
        // TODO EXE - Podría desambiguar esto en dos métodos, uno que reciba ICartaDeDerrumbe
        //  y otro para ICartaDeMapa
        if (carta.getTipos().get(0) == TipoCartaAccion.DERRUMBE) {
            CartaDeTunel cartaADerrumbar = this.obtenerCartaQueColisiona(carta);
            if (cartaADerrumbar != null) {
                cartaADerrumbar.derrumbar();
            }
        } else if (carta.getTipos().get(0) == TipoCartaAccion.MAPA) {
            CartaDeTunel destino = this.cartasDeDestino
                .stream()
                .filter(cartaDeDestino -> cartaDeDestino.colisionaCon(carta))
                .findFirst()
                .orElse(null);
            Evento evento = new Evento(TipoEvento.MOSTRAR_CARTA_DE_DESTINO, destino, this.obtenerJugadorDelTurnoActual());
            try {
                this.notificarObservadores(evento);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validarColision(CartaDeTunel carta) {
        return this.tablero
                .stream()
                .noneMatch(cartaDelTablero -> cartaDelTablero.colisionaCon(carta));
    }

    @Override
    public void enviarMensaje(Mensaje mensaje) throws RemoteException {
        System.out.println(String.format("Jugador [%s] dijo [%s]", mensaje.getJugador(), mensaje.getTexto()));
        this.mensajes.add(mensaje);
        try {
            //this.notificarObservadores(new Evento(TipoEvento.NUEVO_MENSAJE));
            Evento evento = new Evento(TipoEvento.NUEVO_MENSAJE, jugadores.get(indiceJugadorTurno));
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
    public List<IJugador> getDatosJugadores() throws RemoteException {
        return jugadores;
    }

    @Override
    public int[][] getGrilla() throws RemoteException {
        return grilla;
    }

    @Override
    public void marcarListo(IJugador jugador) throws RemoteException {
        jugador.marcarListo();

        System.out.println("[" + jugador.getId() + "] está listo");

        if (jugadores.stream().allMatch(IJugador::getListo)) {
            comenzarJuego();
        }
    }

    private void comenzarJuego() throws RemoteException {
        System.out.println("Todos listos");
        this.partidaEmpezo = true;
        for (IJugador jugador : jugadores) {
            Random random = new Random();
            List<CartaDeJuego> mano = new ArrayList<>();
            List<CartaDeAccion> herramientasRotas = new ArrayList<>();
            // TODO EXE - Tomar al azar del mazo
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
        this.enviarMensaje(new Mensaje(null, "Comenzó el juego"));
    }

    private CartaDeTunel obtenerCartaQueColisiona(CartaDeAccion carta) {
        return this.tablero
                .stream()
                .filter(cartaDelTablero -> cartaDelTablero.colisionaCon(carta))
                .findFirst()
                .orElse(null);
    }

    private IJugador obtenerJugadorDelTurnoActual() {
        return this.jugadores.get(indiceJugadorTurno);
    }

    @Override
    public void descartar(CartaDeJuego carta) {
        this.pilaDeDescarte.add(carta);
    }
}
