package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ar.edu.unlu.poo.saboteur.controlador.Controlador;
import ar.edu.unlu.poo.saboteur.modelo.impl.Jugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class VistaGrafica implements IVista {

    private String nombreJugador;
    private DefaultListModel<String> historialDeChat;
    private DefaultListModel<String> jugadores;
    private Jugador jugador;
    private JButton botonEnviar;

    public VistaGrafica(Controlador controlador, String nombreJugador) {
        this.nombreJugador = nombreJugador;

        controlador.setVista(this);

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Saboteur - " + this.nombreJugador);

            historialDeChat = new DefaultListModel<>();
            JList<String> historial = new JList<>(historialDeChat);

            JTextField textoDelUsuario = new JTextField();
            textoDelUsuario.setSize(500, 20);

            botonEnviar = new JButton("Enviar");

            botonEnviar.setEnabled(nombreJugador.endsWith("-1"));

            botonEnviar.addActionListener(event -> {
                if (textoDelUsuario.getText() != null && !textoDelUsuario.getText().isEmpty()) {
                    controlador.enviarMensaje(new Mensaje(this.nombreJugador, textoDelUsuario.getText()));
                }
                textoDelUsuario.setText("");
            });

            textoDelUsuario.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    if ("\n".charAt(0) == e.getKeyChar() && textoDelUsuario.getText().length() > 0) {
                        System.out.println("Clicked");
                        botonEnviar.doClick();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Sin implementación
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Sin implementación
                }
            });

            Container panelPrincipal = frame.getContentPane();
            panelPrincipal.setLayout(new FlowLayout());

            JComponent panelChat = new JPanel();
            panelChat.setLayout(new GridLayout(2, 1));
            panelChat.add(historial);

            panelPrincipal.add(panelChat);

            JComponent panelInferiorChat = new JPanel();
            panelInferiorChat.setLayout(new GridLayout(1, 2));
            panelInferiorChat.add(textoDelUsuario);
            panelInferiorChat.add(botonEnviar);
            panelChat.add(panelInferiorChat);

            panelPrincipal.add(panelChat);
            frame.setVisible(true);
        });
    }

    @Override
    public void iniciar() {
        
    }

    @Override
    public void mostrarMensajes(List<Mensaje> mensajes) {
        historialDeChat.clear();
        mensajes
            .stream()
            .map(mensaje -> String.format("%s: %s", mensaje.getJugador(), mensaje.getTexto()))
            .forEach(historialDeChat::addElement);
    }

    @Override
    public void cambiarTurno(String idJugador) {
        System.out.println("Nuevo turno: [" + idJugador + "]");
        botonEnviar.setEnabled(this.nombreJugador.equals(idJugador));
    }

    public String getNombreJugador() {
        return nombreJugador;
    }
}
