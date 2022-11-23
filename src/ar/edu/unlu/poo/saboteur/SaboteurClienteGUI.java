package ar.edu.unlu.poo.saboteur;

import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
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
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setUIFont(new FontUIResource(GUIConstants.PLAIN_FONT));

        String nombreJugador = null;

        while (nombreJugador == null || nombreJugador.length() == 0) {
            nombreJugador = JOptionPane.showInputDialog("Ingresar un nombre de usuario");
        }

        final int CLIENT_PORT = obtenerPuertoClienteYActualizarArchivo();
        Cliente cliente = new Cliente(DEFAULT_CLIENT_HOSTNAME, CLIENT_PORT, DEFAULT_SERVER_HOSTNAME,
                DEFAULT_SERVER_PORT);
        try {
            cliente.iniciar(controladorJuego);
        } catch (RemoteException | RMIMVCException e) {
            e.printStackTrace();
        }
        IJugador jugador = controladorJuego.crearJugador(nombreJugador);
        if (jugador == null) {
            System.out.println("Partida ya iniciada");
            System.exit(0);
        }

        System.out.println("Iniciando cliente [" + nombreJugador + "] en puerto [" + CLIENT_PORT + "]");
        vista = new VistaGrafica(controladorJuego, jugador);
        System.out.println("Tu nombre: " + nombreJugador);
        vista.iniciar();
    }

    private static void setUIFont(FontUIResource fontUIResource) {
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource originalFont = (FontUIResource) value;
                Font font = new Font(fontUIResource.getFontName(), originalFont.getStyle(), fontUIResource.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
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
