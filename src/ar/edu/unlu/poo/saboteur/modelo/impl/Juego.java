package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDePuntos;
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
    private List<CartaDePuntos> cartasDePuntos;

    private List<CartaDeTunel> tablero;
    private CartaDeTunel cartaDeInicio;
    private CartaDeTunel cartaDeDestinoOro;
    private List<CartaDeTunel> cartasDeDestino;

    private EstadoPartida estadoPartida;

    public Juego() {
        super();
        estadoPartida = EstadoPartida.LOBBY;
        this.mensajes = new ArrayList<>();
        this.mazo = new ArrayList<>();
        this.pilaDeDescarte = new ArrayList<>();
        this.cartasDeDestino = new ArrayList<>();
        this.tablero = new ArrayList<>();
        this.cartasDePuntos = new ArrayList<>();
        this.cargarCartas();
        this.aleatorizarPosicionDeCartasDeDestino();
    }

    /**
     * Genera las instancias de las cartas a partir de los archivos y las coloca en el mazo.
     * 
     * También se guarda las referencias a las cartas iniciales (carta de inicio y las tres
     * cartas de destino)
     * 
     */
    private void cargarCartas() {
        Serializador serializadorCartasDeJuego = new Serializador("assets/cartas_de_juego.dat");
        List<CartaDeJuego> cartasDeJuego = serializadorCartasDeJuego.deserializarLista(CartaDeJuego.class);
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
                    if (cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_ORO) {
                        this.cartaDeDestinoOro = cartaDeTunel;
                    }
                    agregarAlMazo = false;
                }
            }
            if (agregarAlMazo) {
                this.mazo.add(cartaDeJuego);
            }
        }
        Serializador serializadorCartasDePuntos = new Serializador("assets/cartas_de_puntos.dat");
        List<CartaDePuntos> cartasDePuntos = serializadorCartasDePuntos.deserializarLista(CartaDePuntos.class);
        this.cartasDePuntos.addAll(cartasDePuntos);
        // Ordenar de mayor a menor
        this.cartasDePuntos.sort(new Comparator<CartaDePuntos>() {

            @Override
            public int compare(CartaDePuntos o1, CartaDePuntos o2) {
                return o2.getPuntos() - o1.getPuntos();
            }
        });
        //System.out.println(this.cartasDePuntos.stream().map(carta -> String.valueOf(carta.getPuntos())).collect(Collectors.joining(", ")));
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
        boolean colisionEsValida = this.validarColision(carta);
        boolean contiguasAdmitenConexion = cartasContiguas != null
                && !cartasContiguas.isEmpty()
                && cartasContiguas.stream().allMatch(cartaContigua -> cartaContigua.admiteConexion(carta));
        /*
         * Este caso contempla que dos cartas que son contiguas y SÍ se conectan por un túnel
         * estén conectadas al inicio
         */
        boolean contiguasConectadasAlInicio = cartasContiguas != null
                && !cartasContiguas.isEmpty()
                && cartasContiguas
                    .stream()
                    .anyMatch(cartaContigua -> cartaContigua.admiteConexionEstricta(carta) && cartaContigua.estaConectadaConCarta(cartaDeInicio));

        System.out.println("colisionEsValida " + colisionEsValida);
        System.out.println("contiguasAdmitenConexion " + contiguasAdmitenConexion);
        System.out.println("contiguasConectadasAlInicio " + contiguasConectadasAlInicio);

        // Reinicio flag
        this.tablero.forEach(cartaTablero -> cartaTablero.setYaRevisada(false));

        return colisionEsValida && contiguasAdmitenConexion && contiguasConectadasAlInicio;
    }

    @Override
    public boolean jugarCarta(CartaDeTunel cartaDeTunelCliente) {
        System.out.println(String.format("Carta de túnel en (%s,%s)", cartaDeTunelCliente.getX(), cartaDeTunelCliente.getY()));
        CartaDeTunel carta = this.obtenerCartaDeTunelAPartirDeCliente(cartaDeTunelCliente);
        carta.setPosicion(cartaDeTunelCliente.getX(), cartaDeTunelCliente.getY());
        List<CartaDeTunel> cartasContiguas = this.obtenerCartasContiguas(carta);
        System.out.println(String.format("Hay %s cartas contiguas", cartasContiguas.size()));
        if (this.validarPosicion(carta, cartasContiguas)) {
            this.agregarAlTablero(carta, cartasContiguas);
            String mensaje = String.format("[%s] colocó la carta [%s] en (%s,%s)", this.obtenerJugadorDelTurnoActual().getId(), carta.getId(), carta.getX(), carta.getY());
            this.enviarMensajeDeSistema(mensaje);

            this.verificarCondicionDeLlegadaADestino(carta);
            return true;
        }
        return false;
    }

    private void verificarCondicionDeLlegadaADestino(CartaDeTunel carta) {
        this.cartasDeDestino.forEach(destino -> {
            if (destino.estaConectadaConCarta(carta)) {
                destino.setVisible(true);
            }
        });
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
            boolean resultadoAccion = jugadorDestino.romperHerramienta(carta);
            if (!resultadoAccion) {
                return false;
            }
            jugadorActual.removerCartaDeLaMano(carta);
        } else if (carta.esCartaDeHerramientaReparada()) {
            CartaDeAccion herramientaReparada = jugadorDestino.repararHerramienta(carta);
            if (herramientaReparada == null) {
                return false;
            }
            descartar(herramientaReparada, jugadorDestino);
            descartar(carta);
        } else {
            return false;
        }

        String mensaje = String.format("[%s] aplicó a [%s] la carta [%s]", jugadorActual.getId(), jugadorDestino.getId(), carta.getId());
        this.enviarMensajeDeSistema(mensaje);
        return true;
    }

    @Override
    public boolean jugarCarta(CartaDeAccion cartaCliente) throws RemoteException {
        // TODO EXE - Podría desambiguar esto en dos métodos, uno que reciba ICartaDeDerrumbe
        //  y otro para ICartaDeMapa
        CartaDeAccion cartaDeAccion = this.obtenerCartaDeAccionAPartirDeCliente(cartaCliente);
        cartaDeAccion.setPosicion(cartaCliente.getX(), cartaCliente.getY());

        if (cartaDeAccion.getTipos().get(0) == TipoCartaAccion.DERRUMBE) {
            CartaDeTunel cartaADerrumbar = this.obtenerCartaQueColisiona(cartaDeAccion);
            if (cartaADerrumbar != null && !esCartaInicial(cartaADerrumbar)) {
                cartaADerrumbar.derrumbar();
                this.descartarCarta(cartaADerrumbar, this.tablero);
                this.descartarCarta(cartaDeAccion, this.obtenerJugadorDelTurnoActual());

                String mensaje = String.format("[%s] usó la carta [%s] en (%s,%s)", this.obtenerJugadorDelTurnoActual().getId(), cartaCliente.getId(), cartaCliente.getX(), cartaCliente.getY());
                this.enviarMensajeDeSistema(mensaje);
                return true;
            } else {
                return false;
            }
        } else if (cartaDeAccion.getTipos().get(0) == TipoCartaAccion.MAPA) {
            CartaDeTunel destino = this.obtenerCartaQueColisiona(cartaDeAccion);
            if (destino != null && esCartaDeDestino(destino)) {
                String mensajeGeneral = String.format("[%s] vio la carta de destino en (%s,%s)", this.obtenerJugadorDelTurnoActual().getId(), cartaCliente.getX(), cartaCliente.getY());
                this.enviarMensajeDeSistema(mensajeGeneral);

                // TODO EXE - Esto debería ser solo para el jugador que jugó la carta de mapa
                String mensajeParaJugador = String.format("La carta que miraste es %s", destino.getTipo() == TipoCartaTunel.DESTINO_ORO ? "ORO" : "PIEDRA");
                this.enviarMensajeDeSistema(mensajeParaJugador);
                this.descartarCarta(cartaDeAccion, this.obtenerJugadorDelTurnoActual());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void descartarCarta(CartaDeTunel cartaADerrumbar, List<CartaDeTunel> tablero) {
        this.tablero.remove(cartaADerrumbar);
    }

    private boolean esCartaInicial(CartaDeTunel carta) {
        return carta == this.cartaDeInicio || esCartaDeDestino(carta);
    }

    private boolean esCartaDeDestino(CartaDeTunel carta) {
        return this.cartasDeDestino.contains(carta);
    }

    private boolean validarColision(CartaDeTunel carta) {
        return this.tablero
                .stream()
                .noneMatch(cartaDelTablero -> cartaDelTablero.colisionaCon(carta));
    }

    private void enviarMensajeDeSistema(String mensaje) {
        try {
            this.enviarMensaje(new Mensaje(mensaje));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
    public IJugador crearJugador() throws RemoteException {
        if (estadoPartida == EstadoPartida.LOBBY) {
            String idJugador = "Jugador-" + indiceIdJugador;
            Jugador jugador = new Jugador(idJugador);
            jugadores.add(jugador);
            this.enviarMensajeDeSistema(idJugador + " se unió a la partida");
            this.notificarObservadores(new Evento(TipoEvento.JUGADOR_ENTRA, this.jugadores));
            indiceIdJugador++;
            return jugador;
        }
        return null;
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
        this.estadoPartida = EstadoPartida.PRIMERA_RONDA;
        this.mezclarMazo();
        this.asignarRoles();
        this.asignarTurno();
        this.repartirCartas();

        // Esto solo hay que hacerlo en las rondas 2 y 3
        //this.inicializarRonda();

        this.enviarMensajeDeSistema("Comenzó el juego");

        Evento evento = new Evento(TipoEvento.INICIA_JUEGO, jugadores, this.tablero);
        this.notificarObservadores(evento);
    }

    private void aleatorizarPosicionDeCartasDeDestino() {
        List<Integer[]> posiciones = new ArrayList<>();
        posiciones.add(new Integer[] {8, 0});
        posiciones.add(new Integer[] {8, 2});
        posiciones.add(new Integer[] {8, 4});
        Collections.shuffle(posiciones);
        this.cartasDeDestino.forEach(carta -> {
            Integer[] posicion = posiciones.remove(0);
            carta.setPosicion(posicion[0], posicion[1]);
        });
    }

    private void repartirCartas() {
        // TODO EXE - La cantidad de cartas a repartir depende de la cantidad de jugadores
        int cantidadDeJugadores = this.jugadores.size();
        int cantidadARepartir = -1;
        if (cantidadDeJugadores >= 3 && cantidadDeJugadores <= 5) {
            cantidadARepartir = 6;
        } else if (cantidadDeJugadores == 6 || cantidadDeJugadores == 7) {
            cantidadARepartir = 5;
        } else if (cantidadDeJugadores >= 8 && cantidadDeJugadores <= 10) {
            cantidadARepartir = 4;
        }
        for (int i = 0; i < cantidadARepartir; i++) {
            for (IJugador jugador : jugadores) {
                jugador.getMano().add(this.mazo.remove(0));
            }
        }
    }

    private void asignarTurno() {
        this.jugadores.get(0).cambiarTurno();
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

            Iterator<CartaDeAccion> herramientasRotasIterator = jugador.getHerramientasRotas().iterator();
            while (herramientasRotasIterator.hasNext()) {
                CartaDeAccion carta = herramientasRotasIterator.next();
                herramientasRotasIterator.remove();
                this.mazo.add(carta);
            }
        }
    }

    private void asignarRoles() {
        List<RolJugador> roles = this.obtenerRolesParaRepartir();
        this.jugadores.forEach(jugador -> jugador.setRol(roles.remove(0)));
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
        this.enviarMensajeDeSistema("[" + this.obtenerJugadorDelTurnoActual().getId() + "] descartó una carta");
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
        if (!this.mazo.isEmpty()) {
            CartaDeJuego carta = this.mazo.remove(0);
            this.obtenerJugadorDelTurnoActual().getMano().add(carta);
            return carta;
        }
        return null;
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
    public void avanzar() throws RemoteException {
        if (terminoLaRonda()) { // Condición de fin de ronda
            if (ganaronLosBuscadores()) {
                System.out.println("Ganaron los buscadores");
                if (this.cartasDePuntos.isEmpty()) {
                    System.err.println("No quedan cartas de puntos");
                } else {
                    List<IJugador> buscadores = this.jugadores.stream().filter(jugador -> jugador.getRol() == RolJugador.BUSCADOR).collect(Collectors.toList());
                    int cantidadDePepitasARepartir = this.jugadores.size() < 10 ? this.jugadores.size() : 9;
                    for (int i = 0; i < cantidadDePepitasARepartir; i++) {
                        // TODO EXE - Esto es lo mismo de más abajo. Extraer a un método
                        int indiceBuscador = i % buscadores.size();
                        IJugador jugador = buscadores.get(indiceBuscador);
                        CartaDePuntos cartaDePuntos = this.cartasDePuntos.remove(0);
                        jugador.recibirPuntos(cartaDePuntos);
                        System.out.println("Buscador [" + jugador.getId() + "] recibe " + cartaDePuntos.getPuntos() + " puntos");
                    }
                }
            } else if (ganaronLosSaboteadores()) {
                if (this.cartasDePuntos.isEmpty()) {
                    System.err.println("No quedan cartas de puntos");
                } else {
                    System.out.println("Ganaron los saboteadores");
                    List<IJugador> saboteadores = this.jugadores.stream().filter(jugador -> jugador.getRol() == RolJugador.SABOTEADOR).collect(Collectors.toList());
                    if (saboteadores.size() == 1) {
                        // TODO EXE - Darle 4 pepitas
                        for (int i = 0; i < saboteadores.size(); i++) {
                            IJugador jugador = saboteadores.get(i);
                            CartaDePuntos cartaDePuntos = this.cartasDePuntos.remove(0);
                            jugador.recibirPuntos(cartaDePuntos);
                            System.out.println("Saboteador [" + jugador.getId() + "] recibe " + cartaDePuntos.getPuntos() + " puntos");
                        }
                    } else if (saboteadores.size() == 2 || saboteadores.size() == 3) {
                        // TODO EXE - Darle 3 pepitas a cada uno
                        for (int i = 0; i < saboteadores.size(); i++) {
                            IJugador jugador = saboteadores.get(i);
                            CartaDePuntos cartaDePuntos = this.cartasDePuntos.remove(0);
                            jugador.recibirPuntos(cartaDePuntos);
                            System.out.println("Saboteador [" + jugador.getId() + "] recibe " + cartaDePuntos.getPuntos() + " puntos");
                        }
                    } else {
                        // TODO EXE - Darle 2 pepitas a cada uno
                        for (int i = 0; i < saboteadores.size(); i++) {
                            IJugador jugador = saboteadores.get(i);
                            CartaDePuntos cartaDePuntos = this.cartasDePuntos.remove(0);
                            jugador.recibirPuntos(cartaDePuntos);
                            System.out.println("Saboteador [" + jugador.getId() + "] recibe " + cartaDePuntos.getPuntos() + " puntos");
                        }
                    }
                }
            }
            this.estadoPartida = this.estadoPartida.getSiguienteEstado();
            if (EstadoPartida.RESULTADOS == estadoPartida) {
                this.notificarObservadores(new Evento(TipoEvento.FIN_JUEGO));
            } else {
                this.notificarObservadores(new Evento(TipoEvento.FIN_RONDA));
            }
        } else {
            this.incrementarTurno();
            System.out.println("Siguiente turno: " + this.obtenerJugadorDelTurnoActual().getId());
            try {
                this.notificarObservadores(new Evento(TipoEvento.CAMBIO_TURNO, this.jugadores));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean ganaronLosBuscadores() {
        return this.cartaDeDestinoOro.isVisible();
    }

    private boolean ganaronLosSaboteadores() {
        return this.mazo.isEmpty() && this.jugadores.stream().allMatch(jugador -> jugador.getMano().isEmpty());
    }

    /**
     * Evalúa si se cumplen las condiciones para que termine la ronda.
     * 
     * @return true si se llegó a la carta de oro o si no se llegó y además ya no quedan cartas por jugar
     */
    private boolean terminoLaRonda() {
        return ganaronLosBuscadores() || ganaronLosSaboteadores();
    }

    private List<RolJugador> obtenerRolesParaRepartir() {
        List<RolJugador> roles = new ArrayList<>();
        if (this.jugadores.size() == 3) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 4) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 5) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 6) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 7) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 8) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 9) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        } else if (this.jugadores.size() == 10) {
            roles.addAll(Arrays.asList(
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.SABOTEADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR,
                    RolJugador.BUSCADOR));
        }
        Collections.shuffle(roles);
        return roles;
    }
}
