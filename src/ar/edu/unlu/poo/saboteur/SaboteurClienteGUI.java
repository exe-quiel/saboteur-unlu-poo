package ar.edu.unlu.poo.saboteur;

import java.rmi.RemoteException;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.vista.IVista;
import ar.edu.unlu.poo.saboteur.vista.impl.VistaGrafica;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.cliente.Cliente;

public class SaboteurClienteGUI {

    private static final String DEFAULT_CLIENT_HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_CLIENT_PORT = 9991;
    private static final String DEFAULT_SERVER_HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;

    private static IVista vista;

    public static void main(String[] args) {
        ControladorJuego controladorJuego = new ControladorJuego();

        Cliente cliente = new Cliente(DEFAULT_CLIENT_HOSTNAME, DEFAULT_CLIENT_PORT, DEFAULT_SERVER_HOSTNAME,
                DEFAULT_SERVER_PORT);
        try {
            cliente.iniciar(controladorJuego);
        } catch (RemoteException | RMIMVCException e) {
            e.printStackTrace();
        }
        String nombreJugador = null;
        try {
            nombreJugador = controladorJuego.generarNombreJugador();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        vista = new VistaGrafica(controladorJuego, nombreJugador);
        System.out.println("Tu nombre: " + nombreJugador);
        vista.iniciar();
    }
}
