package ar.edu.unlu.poo.saboteur.controlador;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public class ControladorJuego implements IControladorRemoto {

    private IJuego juego;
    private IVista vista;

    public ControladorJuego() {

    }

    @Override
    public void actualizar(IObservableRemoto arg0, Object arg1) {
        if (arg1 instanceof Evento) {
            Evento evento = (Evento) arg1;
            switch ((TipoEvento) evento.getTipoEvento()) {
            case JUGADOR_ENTRA:
                // Null-check porque cuando recién ejecuta el main un jugador,
                // la vista aún no está seteada y tira excepción cuando recibe
                // el evento de su propio ingreso al juego
                if (vista != null) {
                    vista.actualizarJugadores(evento.getJugadores());
                }
                break;
            case JUGADOR_SALE:
                vista.actualizarJugadores(evento.getJugadores());
                break;
            case NUEVO_MENSAJE:
                vista.actualizarMensajes();
                //vista.cambiarTurno(evento.getJugadorOrigen());
                break;
            case CAMBIO_TURNO:
                vista.actualizar();
                break;
            case INICIA_JUEGO:
                vista.iniciarJuego(evento);
                break;
            default:
                break;
            }
        }
    }

    public List<Mensaje> obtenerMensajes() {
        try {
            return this.juego.getMensajes();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean jugarCarta(CartaDeTunel carta) {
        try {
            return this.juego.jugarCarta(carta);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean jugarCarta(IJugador jugadorDestino, CartaDeAccion carta) {
        try {
            return this.juego.jugarCarta(jugadorDestino, carta);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permite jugar una carta de accion en el tablero (mapa o derrumbe).
     * 
     * @param carta carta a jugar
     */
    public boolean jugarCarta(CartaDeAccion carta) {
        try {
            return this.juego.jugarCarta(carta);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void enviarMensaje(Mensaje mensaje) {
        try {
            this.juego.enviarMensaje(mensaje);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public IJugador generarJugador() {
        try {
            return this.juego.generarJugador();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void salir() {
        try {
            this.juego.salir(this.vista.getJugador());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void marcarListo(IJugador jugador) {
        try {
            juego.marcarListo(jugador);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<IJugador> obtenerJugadores() {
        try {
            return this.juego.obtenerJugadores();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CartaDeTunel> obtenerTablero() {
        try {
            return this.juego.obtenerTablero();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void terminarTurno() {
        try {
            this.juego.terminarTurno(this.vista.getJugador());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T arg0) throws RemoteException {
        this.juego = (IJuego) arg0;
    }
}
