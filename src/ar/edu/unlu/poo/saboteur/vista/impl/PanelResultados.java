package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class PanelResultados extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 8600523938097587547L;

    private ControladorJuego controladorJuego;

    private JPanel jugadoresResultados;
    private JButton botonContinuar;

    private IVista vista;

    public PanelResultados(IVista vista) {
        this.vista = vista;

        setLayout(new GridBagLayout());

        jugadoresResultados = new JPanel();
        jugadoresResultados.setLayout(new GridLayout(10, 1));
        jugadoresResultados.setFont(GUIConstants.PLAIN_FONT);

        add(jugadoresResultados);

        JLabel titulo = new JLabel("FIN DE LA RONDA");
        add(titulo);

        botonContinuar = new JButton("Continuar");
        botonContinuar.addActionListener(evento -> {
            botonContinuar.setEnabled(false);
            controladorJuego.marcarListo(vista.getJugador());
        });
        add(botonContinuar);
    }

    public void actualizar(TipoEvento tipoEvento) {
        List<IJugador> jugadores = this.controladorJuego.obtenerJugadores();
        IJugador jugador = jugadores
                .stream()
                .filter(j -> j.equals(this.vista.getJugador()))
                .findFirst()
                .get();
        jugadores.sort((o1, o2) -> o2.calcularPuntaje() - o1.calcularPuntaje());

        this.jugadoresResultados.removeAll();

        if (tipoEvento == TipoEvento.FINALIZA_RONDA) {
            this.jugadoresResultados.setBorder(new TitledBorder("Resultado de la ronda"));
            jugadores.stream()
                    .map(j -> {
                        String contenidoLabel = new StringBuilder()
                                .append(j.getNombre())
                                .append(" ")
                                .append(j.getRol())
                                .append(" -> ")
                                .append(j.calcularPuntaje())
                                .toString();
                        JLabel label = new JLabel(contenidoLabel);
                        if (j.equals(jugador)) {
                            label.setForeground(Color.RED);
                        }
                        return label;
                    })
                    .forEach(this.jugadoresResultados::add);
            this.botonContinuar.setEnabled(true);

        } else if (tipoEvento == TipoEvento.FINALIZA_JUEGO) {
            this.jugadoresResultados.setBorder(new TitledBorder("Resultado de la partida"));
            jugadores.stream()
                    .map(j -> {
                        String contenidoLabel = new StringBuilder()
                                .append(j.getNombre())
                                .append(" ")
                                .append(j.getRol())
                                .append(" -> ")
                                .append(j.calcularPuntaje())
                                .toString();
                        JLabel label = new JLabel(contenidoLabel);
                        if (j.equals(jugador)) {
                            label.setForeground(Color.RED);
                        }
                        return label;
                    })
                    .forEach(this.jugadoresResultados::add);
            remove(botonContinuar);
        }

        this.jugadoresResultados.revalidate();
        this.jugadoresResultados.repaint();
    }
}
