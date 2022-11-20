package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.EstadoPartida;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.RolJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.util.Serializador;
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
        this.cartasDeDestino = new ArrayList<>();
        this.tablero = new ArrayList<>();
        this.cargarCartas();
    }

    /**
     * Genera las instancias de las cartas a partir de los archivos y las coloca en el mazo.
     * 
     * También se guarda las referencias a las cartas iniciales (carta de inicio y las tres
     * cartas de destino)
     * 
     */
    private void cargarCartas() {
        Serializador serializador = new Serializador("assets/cartas.dat");
        List<CartaDeJuego> cartasDeJuego = serializador.deserializarLista(CartaDeJuego.class);
        for (CartaDeJuego cartaDeJuego : cartasDeJuego) {
            boolean agregarAlMazo = true;
            if (cartaDeJuego instanceof CartaDeTunel) {
                CartaDeTunel cartaDeTunel = (CartaDeTunel) cartaDeJuego;
                if (cartaDeTunel.getTipo() == TipoCartaTunel.INICIO) {
                    this.cartaDeInicio = cartaDeTunel;
                    this.agregarAlTablero(cartaDeTunel, null);
                    agregarAlMazo = false;
                } else if (cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_ORO
                        || cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_PIEDRA) {
                    this.cartasDeDestino.add(cartaDeTunel);
                    this.agregarAlTablero(cartaDeTunel, null);
                    agregarAlMazo = false;
                }
            }
            if (agregarAlMazo) {
                this.mazo.add(cartaDeJuego);
            }
        }
    }

    private void agregarAlTablero(CartaDeTunel carta, List<CartaDeTunel> cartasContiguas) {
        if (cartasContiguas != null) {
            cartasContiguas.forEach(cartaContigua -> cartaContigua.conectar(carta));
        }
        this.tablero.add(carta);
        if (cartasContiguas != null) {
            // Si cartasContiguas es null, quiere decir que estamos colocando las cartas iniciales
            // y por lo tanto no hay jugadores todavía
            this.removerCartaDeLaMano(carta);
        }
    }

    private void removerCartaDeLaMano(CartaDeJuego carta) {
        if (this.obtenerJugadorDelTurnoActual().getMano().remove(carta)) {
            System.out.println("Se removió la carta " + carta.getId() + " de la mano del jugador " + this.obtenerJugadorDelTurnoActual().getId());
        } else {
            System.err.println("Error al remover al carta " + carta.getId() + " de la mano del jugador " + this.obtenerJugadorDelTurnoActual().getId());
        }
    }

    private CartaDeTunel obtenerCartaDeTunelAPartirDeCliente(CartaDeTunel cartaDeTunel) {
        // TODO EXE - Unir este método y el de abajo
        return this.obtenerJugadorDelTurnoActual().getMano()
                .stream()
                .filter(carta -> carta.equals(cartaDeTunel))
                .map(carta -> (CartaDeTunel) carta)
                .findFirst()
                .get();
    }

    private CartaDeAccion obtenerCartaDeAccionAPartirDeCliente(CartaDeAccion cartaDeAccion) {
        return this.obtenerJugadorDelTurnoActual().getMano()
                .stream()
                .filter(carta -> carta.equals(cartaDeAccion))
                .map(carta -> (CartaDeAccion) carta)
                .findFirst()
                .get();
    }

    private boolean validarPosicion(CartaDeTunel carta, List<CartaDeTunel> cartasContiguas) {
        boolean posicionEsValida = this.validarColision(carta)
                && cartasContiguas.stream().allMatch(cartaContigua -> cartaContigua.admiteConexion(carta))
                && cartasContiguas.stream().anyMatch(cartaContigua -> cartaContigua.admiteConexionEstricta(carta) && cartaContigua.estaConectadaConCarta(cartaDeInicio));
        this.tablero.forEach(cartaTablero -> cartaTablero.setYaRevisada(false));
        return posicionEsValida;
    }

    @Override
    public boolean jugarCarta(CartaDeTunel cartaDeTunelCliente) {
        System.out.println(String.format("Carta de túnel en (%s,%s)", cartaDeTunelCliente.getX(), cartaDeTunelCliente.getY()));
        CartaDeTunel carta = this.obtenerCartaDeTunelAPartirDeCliente(cartaDeTunelCliente);
        List<CartaDeTunel> cartasContiguas = this.obtenerCartasContiguas(carta);
        if (this.validarPosicion(carta, cartasContiguas)) {
            this.agregarAlTablero(carta, cartasContiguas);
            return true;
        }
        return false;
    }

    private List<CartaDeTunel> obtenerCartasContiguas(CartaDeTunel carta) {
        return this.tablero
            .stream()
            .filter(cartaDelTablero -> cartaDelTablero.sonContiguas(carta))
            .collect(Collectors.toList());
    }

    @Override
    public boolean jugarCarta(IJugador jugadorDestinoCliente, CartaDeAccion cartaCliente) throws RemoteException {
        IJugador jugadorDestino = this.obtenerJugadorAPartirDeCliente(jugadorDestinoCliente);
        IJugador jugadorActual = this.obtenerJugadorDelTurnoActual();
        CartaDeAccion carta = this.obtenerCartaDeAccionAPartirDeCliente(cartaCliente);
        if (carta.esCartaDeHerramientaRota()) {
            if (!jugadorDestino.getHerramientasRotas().contains(carta)) {
                jugadorDestino.romperHerramienta(carta);
                jugadorActual.removerCartaDeLaMano(carta);
            }
        } else if (carta.esCartaDeHerramientaReparada()) {
            CartaDeAccion herramientaReparada = jugadorDestino.repararHerramienta(carta);
            if (herramientaReparada != null) {
                descartar(herramientaReparada, jugadorDestino);
                descartar(carta);
            }
        }

        String mensaje = String.format("[%s] aplicó a [%s] la carta [%s]", jugadorActual.getId(), jugadorDestino.getId(), carta);
        this.enviarMensaje(new Mensaje(mensaje));
        return true;
    }

    @Override
    public boolean jugarCarta(CartaDeAccion carta) throws RemoteException {
        // TODO EXE - Podría desambiguar esto en dos métodos, uno que reciba ICartaDeDerrumbe
        //  y otro para ICartaDeMapa
        if (carta.getTipos().get(0) == TipoCartaAccion.DERRUMBE) {
            CartaDeTunel cartaADerrumbar = this.obtenerCartaQueColisiona(carta);
            if (cartaADerrumbar != null) {
                cartaADerrumbar.derrumbar();
                this.descartarCarta(cartaADerrumbar, this.obtenerJugadorDelTurnoActual());
            } else {
                return false;
            }
        } else if (carta.getTipos().get(0) == TipoCartaAccion.MAPA) {
            CartaDeTunel destino = this.obtenerCartaQueColisiona(carta);
            if (destino != null) {
                Evento evento = new Evento(TipoEvento.MOSTRAR_CARTA_DE_DESTINO, destino, this.obtenerJugadorDelTurnoActual());
                try {
                    this.notificarObservadores(evento);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validarColision(CartaDeTunel carta) {
        return this.tablero
                .stream()
                .noneMatch(cartaDelTablero -> cartaDelTablero.colisionaCon(carta));
    }

    @Override
    public void enviarMensaje(Mensaje mensaje) throws RemoteException {
        System.out.println(String.format("[%s] dijo [%s]", mensaje.obtenerOrigen(), mensaje.getTexto()));
        this.mensajes.add(mensaje);
        try {
            Evento evento = new Evento(TipoEvento.NUEVO_MENSAJE);
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
    public IJugador generarJugador() throws RemoteException {
        String idJugador = "Jugador-" + indiceIdJugador;
        Jugador jugador = new Jugador(idJugador);
        jugadores.add(jugador);
        this.notificarObservadores(new Evento(TipoEvento.JUGADOR_ENTRA, this.jugadores));
        indiceIdJugador++;
        return jugador;
    }

    private void incrementarTurno() {
        this.obtenerJugadorDelTurnoActual().cambiarTurno();
        this.indiceJugadorTurno++;
        if (this.jugadores.size() == indiceJugadorTurno) {
            this.indiceJugadorTurno = 0;
        }
        this.obtenerJugadorDelTurnoActual().cambiarTurno();
    }

    @Override
    public List<IJugador> obtenerJugadores() throws RemoteException {
        return jugadores;
    }

    @Override
    public void marcarListo(IJugador jugadorCliente) throws RemoteException {
        IJugador jugador = this.obtenerJugadorAPartirDeCliente(jugadorCliente);
        jugador.marcarListo();

        System.out.println("[" + jugador.getId() + "] está listo");

        if (jugadores.stream().allMatch(IJugador::estaListo)) {
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

        this.inicializarRonda();

        Evento evento = new Evento(TipoEvento.INICIA_JUEGO, jugadores, this.tablero);
        this.notificarObservadores(evento);
        this.enviarMensaje(new Mensaje(null, "Comenzó el juego"));
    }

    /**
     * Remueve las cartas del tablero, dela pila de descarte y de las manos de los jugadores
     * y las coloca nuevamente en el mazo.
     * 
     */
    private void inicializarRonda() {
        Iterator<CartaDeTunel> tableroIterator = this.tablero.iterator();
        while (tableroIterator.hasNext()) {
            CartaDeTunel carta = tableroIterator.next();
            if (carta != cartaDeInicio && !cartasDeDestino.contains(carta)) {
                // Son cartas con posiciones fijas en el tablero que no deberían quitarse
                tableroIterator.remove();
            }
            carta.inicializar();
            this.mazo.add(carta);
        }

        Iterator<CartaDeJuego> descarteIterator = this.pilaDeDescarte.iterator();
        while (descarteIterator.hasNext()) {
            CartaDeJuego carta = descarteIterator.next();
            descarteIterator.remove();
            carta.inicializar();
            this.mazo.add(carta);
        }

        for (IJugador jugador : this.jugadores) {
            Iterator<CartaDeJuego> manoIterator = jugador.getMano().iterator();
            while (manoIterator.hasNext()) {
                CartaDeJuego carta = manoIterator.next();
                manoIterator.remove();
                carta.inicializar();
                this.mazo.add(carta);
            }
        }
    }

    private void asignarRoles() {
        // TODO EXE - Implementar distintas probabilidades según la cantidad de jugadores
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

    /**
     * Este método es necesario porque, al enviar y recibir objetos a través de RMI y deserializarlos,
     * la instancia deserializada ya NO es la misma.
     * 
     * Por lo tanto, hay que obtener el objeto correspondiente al que nos envió el cliente.
     * 
     * @param jugadorCliente jugador que proviene del cliente
     * @return jugador correspondiente en la lista de jugadores del servidor
     */
    private IJugador obtenerJugadorAPartirDeCliente(IJugador jugadorCliente) {
        return this.jugadores
                .stream()
                .filter(jugador -> jugador.equals(jugadorCliente))
                .findFirst()
                .get();
    }

    @Override
    public boolean descartar(CartaDeJuego carta, IJugador jugadorCliente) throws RemoteException {
        this.descartarCarta(carta, this.obtenerJugadorAPartirDeCliente(jugadorCliente));
        return true;
    }

    @Override
    public boolean descartar(CartaDeJuego cartaCliente) throws RemoteException {
        CartaDeJuego carta = this.obtenerCartaDeJuegoAPartirDelCliente(cartaCliente);
        this.descartarCarta(carta, this.obtenerJugadorDelTurnoActual());
        return true;
    }

    private CartaDeJuego obtenerCartaDeJuegoAPartirDelCliente(CartaDeJuego cartaCliente) {
        return this.obtenerJugadorDelTurnoActual().getMano()
            .stream()
            .filter(carta -> carta.equals(cartaCliente))
            .findFirst()
            .get();
    }

    private void descartarCarta(CartaDeJuego carta, IJugador jugador) {
        jugador.removerCartaDeLaMano(carta);
        this.pilaDeDescarte.add(carta);
    }

    @Override
    public CartaDeJuego tomarCarta() throws RemoteException {
        return this.mazo.remove(0);
    }

    @Override
    public List<CartaDeTunel> obtenerTablero() throws RemoteException {
        return this.tablero;
    }

    @Override
    public void salir(IJugador jugadorCliente) throws RemoteException {
        IJugador jugador = this.obtenerJugadorAPartirDeCliente(jugadorCliente);
        this.jugadores.remove(jugador);
        try {
            this.notificarObservadores(new Evento(TipoEvento.JUGADOR_SALE, this.jugadores));
        } catch (RemoteException e) {
            // No hay manera de quitar el observador que se fue
            // porque la librería no da acceso a los observadores
            // (permite removerlos pero no hay forma de saber cuál es el que se fue)
            e.printStackTrace();
        }
    }

    @Override
    public void terminarTurno(IJugador jugador) throws RemoteException {
        this.incrementarTurno();
        try {
            this.notificarObservadores(new Evento(TipoEvento.CAMBIO_TURNO, this.jugadores));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
