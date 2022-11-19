package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.EstadoPartida;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.RolJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

public class Juego extends ObservableRemoto implements IJuego {

    private byte indiceIdJugador = 1;
    private byte indiceJugadorTurno = 0;
    private List<IJugador> jugadores = new LinkedList<>();
    private List<Mensaje> mensajes;

    private List<CartaDeJuego> mazo;
    private List<CartaDeJuego> pilaDeDescarte;

    private List<CartaDeTunel> tablero;
    private CartaDeTunel cartaDeInicio;
    private List<CartaDeTunel> cartasDeDestino;

    private EstadoPartida partidaEmpezo;

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
            if (jugadorDestino.repararHerramienta(carta)) {
                descartar(carta);
            }
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
                this.descartar(cartaADerrumbar);
                Evento evento = new Evento(TipoEvento.CARTA_DERRUMBADA, cartaADerrumbar);
                try {
                    this.notificarObservadores(evento);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (carta.getTipos().get(0) == TipoCartaAccion.MAPA) {
            CartaDeTunel destino = this.obtenerCartaQueColisiona(carta);
            if (destino != null) {
                Evento evento = new Evento(TipoEvento.MOSTRAR_CARTA_DE_DESTINO, destino, this.obtenerJugadorDelTurnoActual());
                try {
                    this.notificarObservadores(evento);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
    public void marcarListo(IJugador jugador) throws RemoteException {
        jugador.marcarListo();

        System.out.println("[" + jugador.getId() + "] está listo");

        if (jugadores.stream().allMatch(IJugador::getListo)) {
            comenzarJuego();
        }
    }

    private void comenzarJuego() throws RemoteException {
        System.out.println("Todos listos");
        this.partidaEmpezo = EstadoPartida.PRIMERA_RONDA;
        this.mezclarMazo();
        this.asignarRoles();
        for (int i = 0; i < 10; i++) {
            for (IJugador jugador : jugadores) {
                jugador.getMano().add(this.mazo.remove(0));
            }
        }
        Evento evento = new Evento(TipoEvento.INICIA_JUEGO, jugadores);
        this.notificarObservadores(evento);
        this.enviarMensaje(new Mensaje(null, "Comenzó el juego"));
    }

    private void asignarRoles() {
        Random random = new Random();
        this.jugadores.forEach(jugador -> jugador.setRol(random.nextBoolean()
                ? RolJugador.SABOTEADOR
                : RolJugador.BUSCADOR));
    }

    private void mezclarMazo() {
        Collections.shuffle(this.mazo);
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
        this.obtenerJugadorDelTurnoActual().removerCartaDeLaMano(carta);
        this.pilaDeDescarte.add(carta);
    }

    @Override
    public CartaDeJuego tomarCarta() {
        return this.mazo.remove(0);
    }

    @Override
    public int[][] getGrilla() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
}
