package ar.edu.unlu.poo.saboteur.controlador;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

public class ControladorJuego implements IControladorRemoto {

    private IJuego juego;
    private IVista vista;

    public ControladorJuego() {

    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    @Override
    public void actualizar(IObservableRemoto arg0, Object arg1) throws RemoteException {
        if (arg1 instanceof Evento) {
            Evento evento = (Evento) arg1;
            switch ((TipoEvento) evento.getTipoEvento()) {
            case JUGADOR_ENTRA:
                //vista.actualizarJugadores(this.chat.getDatosJugadores());
                break;
            case JUGADOR_SALE:
                break;
            case USA_CARTA:
                vista.mostrarGrilla(evento.getIdCarta(), evento.getX(), evento.getY());
                vista.cambiarTurno(evento.getIdJugadorOrigen());
                break;
            case NUEVO_MENSAJE:
                vista.mostrarMensajes(this.juego.getMensajes());
                //vista.cambiarTurno(evento.getJugadorOrigen());
                break;
            case CAMBIO_TURNO:
                vista.cambiarTurno(evento.getIdJugadorOrigen());
                break;
            case INICIA_JUEGO:
                vista.iniciarJuego(evento);
                break;
            default:
                break;
            }
        }
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T arg0) throws RemoteException {
        this.juego = (IJuego) arg0;
    }

    public void jugarCarta(byte idCarta, byte x, byte y) throws RemoteException {
        this.juego.jugarCarta(idCarta, x, y);
    }

    public void jugarCarta(String idJugadorDestino, byte idCarta) throws RemoteException {
        this.juego.jugarCarta(idJugadorDestino, idCarta);
    }

    public void enviarMensaje(Mensaje mensaje) {
        try {
            this.juego.enviarMensaje(mensaje);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String generarIdJugador() throws RemoteException {
        return this.juego.generarIdJugador();
    }

    public List<String> obtenerNombresJugadores() throws RemoteException {
        return this.juego.getDatosJugadores();
        
    }

    public void marcarListo(String idJugador) throws RemoteException {
        juego.marcarListo(idJugador);
    }

    public void aplicarCartaDeHerramienta(byte cartaSeleccionada, String idJugadorDestino) throws RemoteException {
        juego.aplicarCartaDeHerramienta(cartaSeleccionada, idJugadorDestino);
    }
}
