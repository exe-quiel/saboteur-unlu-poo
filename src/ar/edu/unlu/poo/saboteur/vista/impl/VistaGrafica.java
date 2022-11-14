package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.BorderLayout;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.impl.Jugador;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.util.GeneradorDeImagenes;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class VistaGrafica implements IVista {

    private GeneradorDeImagenes generadorDeImagenes = GeneradorDeImagenes.getInstance();

    private String idJugador;
    private DefaultListModel<String> historialDeChat;
    private DefaultListModel<String> jugadores = new DefaultListModel<>();
    private Jugador jugador;
    private JButton botonEnviar;
    private JComponent tablero;
    private boolean esMiTurno;

    public VistaGrafica(ControladorJuego controladorJuego, String idJugador) {
        this.idJugador = idJugador;

        controladorJuego.setVista(this);

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(1280, 720);
            //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            // frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Saboteur - " + this.idJugador);

            historialDeChat = new DefaultListModel<>();
            JList<String> historial = new JList<>(historialDeChat);

            JTextField textoDelUsuario = new JTextField();
            textoDelUsuario.setSize(500, 20);
            textoDelUsuario.setText("Escribir mensaje");

            botonEnviar = new JButton("Enviar");

            // botonEnviar.setEnabled(nombreJugador.endsWith("-1"));

            botonEnviar.addActionListener(event -> {
                String text = textoDelUsuario.getText();
                if (text != null && !text.isEmpty()) {
                    controladorJuego.enviarMensaje(new Mensaje(this.idJugador, text));
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
            JButton botonCoord = new JButton("Mandar coordenada");
            botonCoord.addActionListener(evento -> {
                try {
                    Random random = new Random();
                    controladorJuego.jugarCarta((byte) random.nextInt(50), (byte) random.nextInt(5), (byte) random.nextInt(9));
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            });
            // panelDeJuego.add(botonCoord);

            // tablero = new Tablero();
            tablero = new JPanel();
            tablero.setLayout(new GridLayout(5, 9));
            for (byte y = 0; y < 5; y++) {
                for (byte x = 0; x < 9; x++) {
                    LabelCarta label = new LabelCarta(null, x, y, controladorJuego);
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

            JPanel westPanel = new JPanel();
            westPanel.setLayout(new GridLayout(4, 1));
            westPanel.setPreferredSize(new Dimension(300, 0));
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
            panelChat.setPreferredSize(new Dimension(300, 0));

            panelPrincipal.add(panelChat, BorderLayout.EAST);

            JPanel southPanel = new JPanel();
            JLabel jLabel = new JLabel("cartas");
            southPanel.add(jLabel);
            southPanel.setPreferredSize(new Dimension(0, 300));
            // placeholder.setSize(500, 200);
            panelPrincipal.add(southPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }

    @Override
    public void iniciar() {
        
    }

    @Override
    public void actualizarJugadores(List<String> jugadores) {
        this.jugadores.clear();
        jugadores.forEach(this.jugadores::addElement);
    }

    @Override
    public void mostrarMensajes(List<Mensaje> mensajes) {
        historialDeChat.clear();
        mensajes.stream().map(mensaje -> String.format("%s: %s", mensaje.getJugador(), mensaje.getTexto()))
                .forEach(historialDeChat::addElement);
    }

    @Override
    public void cambiarTurno(String idJugador) {
        System.out.println("Nuevo turno: [" + idJugador + "]");
        if (idJugador.equals(this.idJugador)) {
            esMiTurno = true;
            tablero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            tablero.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    public String getNombreJugador() {
        return this.idJugador;
    }

    @Override
    public void mostrarGrilla(byte idCarta, byte x, byte y) {
        Arrays.asList(tablero.getComponents()).stream()
            .filter(carta -> {
                    if (carta instanceof LabelCarta) {
                        LabelCarta labelCarta = (LabelCarta) carta;
                        return labelCarta.getPosicionX() == x && labelCarta.getPosicionY() == y;
                    }
                    return false;
            })
            .findFirst()
            .ifPresent(carta -> {
                if (carta instanceof LabelCarta) {
                    LabelCarta labelCarta = (LabelCarta) carta;
                    labelCarta.setText("");
                    Entrada[] entradas = new Entrada[] { Entrada.ESTE, Entrada.SUR };
                    labelCarta.setIcon(new ImageIcon(generadorDeImagenes.generarImagen(Entrada.values(), false)));
                }
            });
    }

    public class LabelCarta extends JLabel {

        /**
         * 
         */
        private static final long serialVersionUID = 5370723875059488045L;

        private CartaDeJuego carta;
        private byte posicionX;
        private byte posicionY;

        public LabelCarta(CartaDeJuego carta, byte x, byte y, ControladorJuego controlador) {
            super(String.format("(%s, %s)", x, y));
            this.carta = carta;
            this.posicionX = x;
            this.posicionY = y;

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent event) {
                    System.out.println("[" + idJugador + "] -> " + esMiTurno);
                    boolean esMiTurno = tablero.getCursor().getType() != Cursor.WAIT_CURSOR;
                    if (esMiTurno) {
                        try {
                            byte idCarta = 0;
                            controlador.jugarCarta(idCarta, posicionX, posicionY);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("No es tu turno todavía");
                    }
                }
            });
        }

        public CartaDeJuego getCarta() {
            return carta;
        }

        public byte getPosicionX() {
            return posicionX;
        }

        public byte getPosicionY() {
            return posicionY;
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
}
