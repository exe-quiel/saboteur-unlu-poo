package ar.edu.unlu.poo.saboteur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.vista.IVista;
import ar.edu.unlu.poo.saboteur.vista.impl.VistaGrafica;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.cliente.Cliente;

public class SaboteurClienteGUI {

    private static final String DEFAULT_CLIENT_HOSTNAME = "127.0.0.1";
    //private static final int DEFAULT_CLIENT_PORT = 9990;
    private static final String DEFAULT_SERVER_HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;

    private static IVista vista;

    public static void main(String[] args) throws NumberFormatException, IOException {
        ControladorJuego controladorJuego = new ControladorJuego();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

        final int CLIENT_PORT = obtenerPuertoClienteYActualizarArchivo();
        Cliente cliente = new Cliente(DEFAULT_CLIENT_HOSTNAME, CLIENT_PORT, DEFAULT_SERVER_HOSTNAME,
                DEFAULT_SERVER_PORT);
        try {
            cliente.iniciar(controladorJuego);
        } catch (RemoteException | RMIMVCException e) {
            e.printStackTrace();
        }
        String nombreJugador = null;
        try {
            nombreJugador = controladorJuego.generarIdJugador();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Iniciando cliente [" + nombreJugador + "] en puerto [" + CLIENT_PORT + "]");
        vista = new VistaGrafica(controladorJuego, nombreJugador);
        System.out.println("Tu nombre: " + nombreJugador);
        vista.iniciar();
    }

    /**
     * Esto es para no tener que cambiar el puerto a mano
     * cada vez que ejecuto el {@code main} de esta clase
     * 
     * @see SaboteurServidor#reiniciarArchivoPuertoCliente
     * 
     * @return puerto para que use el cliente
     * @throws IOException si falla la lectura del archivo
     */
    private static int obtenerPuertoClienteYActualizarArchivo() throws IOException {
        Path path = Paths.get("default_port.txt");
        Integer clientPort = Integer.valueOf(Files.readAllLines(path).get(0));
        Files.write(path, String.valueOf(clientPort + 1).getBytes(), StandardOpenOption.WRITE);
        return clientPort;
    }
}
