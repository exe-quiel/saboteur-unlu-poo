package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.util.GUIConstants;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class PanelPartida extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 463591282215678145L;

    private long ultimoClicSobreJugador = -1;

    private ControladorJuego controlador;
    private IJugador jugador;

    private JList<IJugador> listaDeJugadores;

    private Container panelMano;

    private IVista vista;

    private Container panelTablero;

    public PanelPartida(IVista vista) {
        this.controlador = vista.getControlador();
        this.jugador = vista.getJugador();
        this.vista = vista;

        JButton botonListo = new JButton("¡Estoy listo!");
        botonListo.addActionListener(evento -> {
            int cantidadJugadores = vista.getControlador().obtenerJugadores().size();
            if (cantidadJugadores >= 3 && cantidadJugadores <= 10) {
                botonListo.setEnabled(false);
                vista.getControlador().marcarListo(vista.getJugador());
            } else {
                System.err.println("Debe haber entre 3 y 10 jugadores inclusive para poder jugar");
            }
        });

        setLayout(new BorderLayout());

        panelTablero = new JPanel();
        panelTablero.setLayout(new GridLayout(7, 11));

        add(panelTablero, BorderLayout.CENTER);

        Container panelJugadores = new JPanel();
        panelJugadores.setLayout(new FlowLayout());
        // panelJugadores.setPreferredSize(new Dimension(200, 500));
        TitledBorder border = new TitledBorder("Jugadores");
        ((JPanel) panelJugadores).setBorder(border);

        listaDeJugadores = new JList<>();
        listaDeJugadores.setCellRenderer(new JugadoresCellRenderer());
        listaDeJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listaDeJugadores.addListSelectionListener(evento -> {
            int indiceDelJugador = evento.getFirstIndex();
            IJugador jugador = listaDeJugadores.getModel().getElementAt(indiceDelJugador);

            if (!vista.getJugador().esMiTurno()) {
                System.err.println("No es tu turno todavía");
            }

            CartaDeJuego cartaSeleccionada = vista.getCartaSeleccionada();
            if (vista.getCartaSeleccionada() == null) {
                System.err.println("No seleccionaste ninguna carta");
            }

            boolean resultadoAccion = false;
            if (!(cartaSeleccionada instanceof CartaDeAccion)) {
                System.err.println("No seleccionaste una carta de acción");
            }

            CartaDeAccion cartaDeAccion = (CartaDeAccion) cartaSeleccionada;
            boolean dispararAccion = true;
            if (cartaDeAccion.esCartaDeHerramientaRota() && jugador.equals(vista.getJugador())) {
                dispararAccion = false;
            }

            // A veces se dispara el evento de clic varias veces seguidas.
            // Con esto se evita que se llame más de una vez por segundo.
            if (ultimoClicSobreJugador == -1) {
                ultimoClicSobreJugador = System.currentTimeMillis();
            } else {
                long ahora = System.currentTimeMillis();
                if ((ahora - ultimoClicSobreJugador < 1000)) {
                    ultimoClicSobreJugador = ahora;
                    dispararAccion = false;
                }
            }

            if (dispararAccion) {
                System.out.println("Hiciste clic en el usuario " + jugador.getNombre());
                resultadoAccion = vista.getControlador().jugarCarta(jugador, (CartaDeAccion) cartaSeleccionada);
                if (resultadoAccion) {
                    vista.getControlador().avanzar();
                } else {
                    System.err.println("Ocurrió un error");
                }
            }
            listaDeJugadores.clearSelection();
        });

        panelJugadores.add(listaDeJugadores);

        add(panelJugadores, BorderLayout.WEST);

        actualizarJugadores();

        if (vista.getJugador().esMiTurno()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        panelMano = new JPanel();
        panelMano.setLayout(new GridLayout(1, 10));
        //panelMano.setLayout(new FlowLayout());
        panelMano.add(botonListo);
        // panelMano.setPreferredSize(new Dimension(0, 200));
        add(panelMano, BorderLayout.SOUTH);
    }

    public void actualizar() {
        this.actualizarJugadores();
        this.actualizarTurno();
        this.actualizarTablero();
        this.actualizarMano();
    }

    public void actualizarJugadores() {
        List<IJugador> jugadores = this.controlador.obtenerJugadores();
        for (IJugador jugador : jugadores) {
            if (jugador.equals(this.jugador)) {
                // Actualizar la instancia con el último estado proveniente del servidor
                this.jugador = jugador;
                this.vista.setJugador(jugador);
            }
        }
        this.listaDeJugadores.setModel(new ModeloJugadores(jugadores));
    }

    private void actualizarTurno() {
        if (this.jugador.esMiTurno()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    private void actualizarTablero() {
        List<CartaDeTunel> tablero = this.controlador.obtenerTablero();
        panelTablero.removeAll();
        for (int y = -1; y < 6; y++) {
            for (int x = -1; x < 10; x++) {
                CartaDeTunel cartaEnPosicion = null;
                for (CartaDeTunel cartaDeTunel : tablero) {
                    if (cartaDeTunel.getX().equals(x) && cartaDeTunel.getY().equals(y)) {
                        cartaEnPosicion = cartaDeTunel;
                        break;
                    }
                }
                CartaTablero label = new CartaTablero(cartaEnPosicion, x, y, this.vista);
                label.setHorizontalAlignment(JLabel.CENTER);
                // label.setPreferredSize(new Dimension(11 * 3, 16 * 3));
                panelTablero.add(label);
            }
        }
        // https://stackoverflow.com/questions/7117332/dynamically-remove-component-from-jpanel
        panelTablero.revalidate();
        panelTablero.repaint();
    }

    private void actualizarMano() {
        panelMano.removeAll();

        if (this.jugador.getMano().isEmpty()) {
            JButton botonPasar = new JButton("Pasar");
            botonPasar.addActionListener(evento -> this.controlador.pasar());
            panelMano.add(botonPasar);
        } else {
            for (CartaDeJuego carta : this.jugador.getMano()) {
                CartaMano cartaLabel = new CartaMano(carta, vista);
                cartaLabel.setHorizontalAlignment(JLabel.CENTER);
                panelMano.add(cartaLabel);
            }
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    public class ModeloJugadores extends AbstractListModel<IJugador> {

        /**
         * 
         */
        private static final long serialVersionUID = 8994216166367505605L;

        private List<IJugador> jugadores;

        public ModeloJugadores(List<IJugador> jugadores) {
            this.jugadores = jugadores;
        }

        @Override
        public int getSize() {
            return jugadores.size();
        }

        @Override
        public IJugador getElementAt(int index) {
            return jugadores.get(index);
        }
    }

    public class JugadoresCellRenderer extends DefaultListCellRenderer {

        /**
         * 
         */
        private static final long serialVersionUID = -216238111892207439L;

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof IJugador) {
                IJugador jugador = (IJugador) value;

                StringBuilder sb = new StringBuilder("<html>");
                if (jugador.esMiTurno()) {
                    sb.append(">>");
                }
                sb.append(jugador.getNombre());

                if (jugador.equals(vista.getJugador())) {
                    if (jugador.getRol() != null) {
                        sb.append(" (");
                        sb.append(jugador.getRol());
                        sb.append(")");
                    }
                    setFont(GUIConstants.BOLD_FONT);
                }

                sb.append("<br>")
                        .append(jugador.getHerramientasRotas().stream()
                                .map(herramientaRota -> "&#8594;"
                                        .concat(herramientaRota.getTipos().get(0).toString()))
                                .collect(Collectors.joining("<br>")));
                sb.append("</html>");
                setText(sb.toString());
            }
            return this;
        }
    }
}
