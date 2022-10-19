package ar.edu.unlu.poo.saboteur;

import java.rmi.RemoteException;

import ar.edu.unlu.poo.saboteur.controlador.Controlador;
import ar.edu.unlu.poo.saboteur.vista.IVista;
import ar.edu.unlu.poo.saboteur.vista.impl.VistaGrafica;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.cliente.Cliente;

public class SaboteurCliente {

	private static final String DEFAULT_CLIENT_HOSTNAME = "127.0.0.1";
	private static final int DEFAULT_CLIENT_PORT = 9999;
	private static final String DEFAULT_SERVER_HOSTNAME = "127.0.0.1";
	private static final int DEFAULT_SERVER_PORT = 8888;

	private static IVista vista;

	public static void main(String[] args) {
		Controlador controlador = new Controlador();
		vista = new VistaGrafica(controlador);
		Cliente cliente = new Cliente(DEFAULT_CLIENT_HOSTNAME, DEFAULT_CLIENT_PORT, DEFAULT_SERVER_HOSTNAME, DEFAULT_SERVER_PORT);
		vista.iniciar();
		try {
			cliente.iniciar(controlador);
		} catch (RemoteException | RMIMVCException e) {
			e.printStackTrace();
		}
	}
}
