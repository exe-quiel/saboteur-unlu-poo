package ar.edu.unlu.poo.saboteur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;

import ar.edu.unlu.poo.saboteur.modelo.IJuego;
import ar.edu.unlu.poo.saboteur.modelo.impl.Juego;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.servidor.Servidor;

public class SaboteurServidor {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8888;

    public static void main(String[] args) throws IOException {
        reiniciarArchivoPuertoCliente();
        System.out.println(args);
        System.out.println("Iniciando servidor RMI");
        IJuego juego = new Juego();
        Servidor servidor = new Servidor(DEFAULT_HOST, DEFAULT_PORT);
        try {
            servidor.iniciar(juego);
        } catch (RemoteException | RMIMVCException e) {
            throw new RuntimeException("Se rompió todo", e);
        }
        System.out.println("Servidor iniciado");
    }

    private static void reiniciarArchivoPuertoCliente() throws IOException {
        Path path = Paths.get("default_port.txt");
        Files.write(path, "9990".getBytes(), StandardOpenOption.WRITE);
    }
}
