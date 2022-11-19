package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class VistaGrafica implements IVista {

    //private GeneradorDeImagenes generadorDeImagenes = GeneradorDeImagenes.getInstance();

    private ControladorJuego controladorJuego;

    private String idJugador;

    private DefaultListModel<Mensaje> historialDeChat;
    private DefaultListModel<IJugador> jugadores = new DefaultListModel<>();
    private IJugador jugador;
    private JButton botonEnviar;
    private JButton botonListo;
    private JComponent tablero;
    private boolean esMiTurno;
    private CartaDeJuego cartaSeleccionada;

    private JPanel southPanel;

    final Border LABEL_BORDER = BorderFactory.createLineBorder(Color.BLUE, 2);
    //final Border LABEL_BORDER = BorderFactory.createRaisedBevelBorder();

    private JList<IJugador> listaDeJugadores;

    public VistaGrafica(ControladorJuego controladorJuego, String idJugador) {
        this.idJugador = idJugador;

        this.controladorJuego = controladorJuego;
        controladorJuego.setVista(this);

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(1280, 720);
            // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            // frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Saboteur - " + this.idJugador);

            historialDeChat = new DefaultListModel<>();
            JList<Mensaje> historial = new JList<>(historialDeChat);

            JTextField textoDelUsuario = new JTextField();
            textoDelUsuario.setSize(500, 20);
            textoDelUsuario.setText("Escribir mensaje");

            botonEnviar = new JButton("Enviar");

            // botonEnviar.setEnabled(nombreJugador.endsWith("-1"));

            botonEnviar.addActionListener(event -> {
                String text = textoDelUsuario.getText();
                if (text != null && !text.isEmpty()) {
                    controladorJuego.enviarMensaje(new Mensaje(this.jugador, text));
                }
                textoDelUsuario.setText("Escribir mensaje");
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

            botonListo = new JButton("Listo");
            botonListo.addActionListener(evento -> {
                try {
                    botonListo.setEnabled(false);
                    controladorJuego.marcarListo(jugador);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            });

            Container panelPrincipal = frame.getContentPane();
            panelPrincipal.setLayout(new BorderLayout());

            JPanel panelDeJuego = new JPanel();
            panelDeJuego.setLayout(new GridLayout());

            // this.jugadores =
            /*
             * try { actualizarJugadores(controlador.obtenerNombresJugadores()); } catch
             * (RemoteException e1) { e1.printStackTrace(); } JList<String> listaDeJugadores
             * = new JList<>(this.jugadores); panelDeJuego.add(listaDeJugadores);
             */
            /*
             * JButton botonCoord = new JButton("Mandar coordenada");
             * botonCoord.addActionListener(evento -> { try { Random random = new Random();
             * controladorJuego.jugarCarta((byte) random.nextInt(50), (byte)
             * random.nextInt(5), (byte) random.nextInt(9)); } catch (RemoteException e1) {
             * e1.printStackTrace(); } });
             */
            // panelDeJuego.add(botonCoord);

            // tablero = new Tablero();
            tablero = new JPanel();
            tablero.setLayout(new GridLayout(5, 9));
            for (byte y = 0; y < 5; y++) {
                for (byte x = 0; x < 9; x++) {
                    LabelCarta label = new LabelCarta(null, controladorJuego);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setPreferredSize(new Dimension(11 * 3, 16 * 3));
                    tablero.add(label);
                }
            }

            if ("Jugador-1".equals(this.idJugador)) {
                esMiTurno = true;
                tablero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                tablero.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            // panelDeJuego.add(tablero);

            panelPrincipal.add(tablero, BorderLayout.CENTER);

            Container westPanel = new JPanel();
            westPanel.setLayout(new GridLayout(10, 1));
            westPanel.setPreferredSize(new Dimension(150, 0));
            /*
            westPanel.add(new JLabel("Jugador1"));
            westPanel.add(new JLabel("Jugador2"));
            westPanel.add(new JLabel("Jugador3"));
            westPanel.add(new JLabel("Jugador4"));
            westPanel.add(new JLabel("Jugador5"));
            westPanel.add(new JLabel("Jugador6"));
            westPanel.add(new JLabel("Jugador7"));
            westPanel.add(new JLabel("Jugador8"));
            westPanel.add(new JLabel("Jugador9"));
            westPanel.add(new JLabel("Jugador10"));
            */

            listaDeJugadores = new JList<>(this.jugadores);
            listaDeJugadores.addListSelectionListener(evento -> {
                IJugador jugador = (IJugador) evento.getSource();
                boolean esMiTurno = tablero.getCursor().getType() != Cursor.WAIT_CURSOR;
                if (esMiTurno && cartaSeleccionada != null && cartaSeleccionada instanceof CartaDeAccion) {
                    try {
                        controladorJuego.jugarCarta((IJugador) jugador, (CartaDeAccion) cartaSeleccionada);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("No es tu turno todavía");
                }
            });

            westPanel.add(listaDeJugadores);

            panelPrincipal.add(westPanel, BorderLayout.WEST);

            JComponent panelChat = new JPanel();
            // panelChat.setSize(100, 500);
            panelChat.setLayout(new GridLayout(2, 1));
            panelChat.add(historial);

            JComponent panelInferiorChat = new JPanel();
            panelInferiorChat.setLayout(new FlowLayout());
            panelInferiorChat.add(textoDelUsuario);
            panelInferiorChat.add(botonEnviar);
            panelChat.add(panelInferiorChat);
            panelChat.setPreferredSize(new Dimension(200, 0));

            panelPrincipal.add(panelChat, BorderLayout.EAST);

            southPanel = new JPanel();
            southPanel.setLayout(new GridLayout(1, 10));
            JLabel jLabel = new JLabel("cartas");
            southPanel.add(jLabel);
            southPanel.add(botonListo);
            southPanel.setPreferredSize(new Dimension(0, 200));
            // placeholder.setSize(500, 200);
            panelPrincipal.add(southPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }

    @Override
    public void iniciar() {

    }

    @Override
    public void actualizarJugadores(List<IJugador> jugadores) {
        this.jugadores.clear();
        jugadores.forEach(this.jugadores::addElement);
    }

    @Override
    public void mostrarMensajes(List<Mensaje> mensajes) {
        historialDeChat.clear();
        mensajes
            //.map(mensaje -> String.format("%s: %s", mensaje.getJugador(), mensaje.getTexto()))
            .forEach(historialDeChat::addElement);
    }

    @Override
    public void cambiarTurno(IJugador jugador) {
        System.out.println("Nuevo turno: [" + idJugador + "]");
        if (jugador.equals(this.jugador)) {
            esMiTurno = true;
            tablero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            tablero.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    public String getNombreJugador() {
        return this.idJugador;
    }

    /*
    @Override
    public void mostrarGrilla(byte idCarta, byte x, byte y) {
        Arrays.asList(tablero.getComponents())
            .stream()
            .filter(carta -> {
                if (carta instanceof LabelCarta) {
                    LabelCarta labelCarta = (LabelCarta) carta;
                    return labelCarta.getPosicionX() == x && labelCarta.getPosicionY() == y;
                }
                return false;
            }).findFirst().ifPresent(carta -> {
                if (carta instanceof LabelCarta) {
                    LabelCarta labelCarta = (LabelCarta) carta;
                    CartaDeJuego cartaDeJuego = labelCarta.getCarta();
                    if (cartaDeJuego instanceof CartaDeTunel) {
                        CartaDeTunel cartaDeTunel = (CartaDeTunel) cartaDeJuego;
                        StringBuilder sb = new StringBuilder();
                        List<Entrada> entradas = cartaDeTunel.getEntradas();
                        if (entradas.contains(Entrada.NORTE)) {
                            sb.append("    ^    ");
                        } else {
                            sb.append("         ");
                        }
                        sb.append("\n");
                        if (entradas.contains(Entrada.OESTE)) {
                            sb.append("<       ");
                        } else {
                            sb.append("        ");
                        }
                        if (entradas.contains(Entrada.ESTE)) {
                            sb.append(">");
                        } else {
                            sb.append(" ");
                        }
                        sb.append("\n");
                        if (entradas.contains(Entrada.SUR)) {
                            sb.append("    ∨    ");
                        } else {
                            sb.append("         ");
                        }
                        labelCarta.setText(sb.toString());
                    }
                    // Entrada[] entradas = new Entrada[] { Entrada.NORTE, Entrada.ESTE, Entrada.SUR
                    // };
                    // labelCarta.setIcon(new
                    // ImageIcon(generadorDeImagenes.generarImagen(Entrada.values(), false)));
                }
            });
    }
    */

    public class LabelCarta extends JLabel {

        /**
         * 
         */
        private static final long serialVersionUID = 5370723875059488045L;

        private CartaDeJuego carta;

        public LabelCarta(CartaDeJuego carta, ControladorJuego controlador) {
            super(String.format("(%s, %s)", carta.getX(), carta.getY()));
            this.carta = carta;

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent event) {
                    System.out.println("[" + idJugador + "] -> " + esMiTurno);
                    boolean esMiTurno = tablero.getCursor().getType() != Cursor.WAIT_CURSOR;
                    boolean estaLibre = ((LabelCarta) event.getComponent()).getCarta() == null;
                    if (esMiTurno && cartaSeleccionada != null && estaLibre) {
                        try {
                            if (cartaSeleccionada instanceof CartaDeTunel) {
                                controlador.jugarCarta((CartaDeTunel) cartaSeleccionada);
                            } else if (cartaSeleccionada instanceof CartaDeAccion) {
                                controlador.jugarCarta((CartaDeTunel) cartaSeleccionada);
                            }
                            Component componentToRemove = null;
                            for (Component component : southPanel.getComponents()) {
                                if (component instanceof JLabel) {
                                    CartaDeJuego carta = ((LabelCarta) component).getCarta();
                                    if (carta == cartaSeleccionada) {
                                        componentToRemove = component;
                                    }
                                }
                            }
                            if (componentToRemove != null) {
                                southPanel.remove(componentToRemove);
                            }
                            cartaSeleccionada = null;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("No es tu turno todavía, tenés que seleccionar una carta primero o no está libre");
                    }
                }

                @Override
                public void mouseEntered(MouseEvent event) {
                    ((JLabel) event.getComponent()).setBorder(LABEL_BORDER);
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    ((JLabel) event.getComponent()).setBorder(null);
                }
            });
        }

        public CartaDeJuego getCarta() {
            return carta;
        }
    }

    public class Tablero extends JComponent implements MouseInputListener {

        // https://stackoverflow.com/questions/776180/how-to-make-canvas-with-swing

        /**
         * 
         */
        private static final long serialVersionUID = -9000222691684122499L;

        private Image img;
        private String path = "C:/Users/Administrator/Downloads/Telegram Desktop/SABOTEUR/cartas de accion${n}.jpg";
        private byte index = 1;

        public Tablero() {
            super();
            this.addMouseListener(this);
            try {
                img = ImageIO.read(new File(path.replace("${n}", "1")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.drawImage(img, 0, 0, null);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("hizo click");
            index++;
            if (index > 3) {
                index = 1;
            }
            try {
                img = ImageIO.read(new File(path.replace("${n}", String.valueOf(index))));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

    }

    @Override
    public void iniciarJuego(Evento evento) {
        botonListo.setEnabled(false);
        this.jugador = evento.obtenerJugador(idJugador);
        southPanel.remove(botonListo);
        // https://stackoverflow.com/questions/7117332/dynamically-remove-component-from-jpanel
        southPanel.revalidate();
        southPanel.repaint();

        this.mostarMano();

        this.cargarListaDeJugadores(evento.getJugadores());
    }

    private void cargarListaDeJugadores(List<IJugador> jugadores) {
        this.jugadores.clear();
        for (IJugador jugador : jugadores) {
            this.jugadores.addElement(jugador);
        }
    }

    private void mostarMano() {
        for (CartaDeJuego carta : this.jugador.getMano()) {
            LabelCarta cartaLabel = new LabelCarta(carta, controladorJuego);
            cartaLabel.setHorizontalAlignment(JLabel.CENTER);
            cartaLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent event) {
                    boolean esMiTurno = tablero.getCursor().getType() != Cursor.WAIT_CURSOR;
                    if (esMiTurno) {
                        LabelCarta carta = (LabelCarta) event.getComponent();
                        cartaSeleccionada = carta.getCarta();
                    } else {
                        System.out.println("No es tu turno todavía");
                    }
                }

                @Override
                public void mouseEntered(MouseEvent event) {
                    ((JLabel) event.getComponent()).setBorder(LABEL_BORDER);
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    ((JLabel) event.getComponent()).setBorder(null);
                }
            });
            southPanel.add(cartaLabel);
        }
    }

    @Override
    public String getIdJugador() {
        return idJugador;
    }

    @Override
    public void setJugador(IJugador jugador) {
        this.jugador = jugador;
    }

    @Override
    public void mostrarTablero(CartaDeTunel carta) {
        // TODO Auto-generated method stub
        
    }
}
