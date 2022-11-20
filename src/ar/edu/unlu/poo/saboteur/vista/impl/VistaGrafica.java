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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class VistaGrafica implements IVista {

    //private GeneradorDeImagenes generadorDeImagenes = GeneradorDeImagenes.getInstance();

    private ControladorJuego controladorJuego;

    private List<IJugador> jugadores;
    private IJugador jugadorCliente;
    private JButton botonEnviar;
    private JButton botonListo;
    private JComponent panelTablero;
    private List<CartaDeTunel> tablero;
    private CartaDeJuego cartaSeleccionada;
    // Work-around para que no se disparen varios clics seguido en la lista de jugadores
    private long ultimoClicSobreJugador = -1;

    private JPanel panelMano;

    final Border LABEL_BORDER = BorderFactory.createLineBorder(Color.BLUE, 2);
    //final Border LABEL_BORDER = BorderFactory.createRaisedBevelBorder();

    private JList<IJugador> listaDeJugadores;

    private JList<Mensaje> chat;

    public VistaGrafica(ControladorJuego controladorJuego, IJugador jugadorCliente) {
        this.jugadorCliente = jugadorCliente;

        this.controladorJuego = controladorJuego;
        this.controladorJuego.setVista(this);

        this.jugadores = new ArrayList<>();

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(1280, 720);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            //frame.setUndecorated(true);
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    controladorJuego.salir();
                    System.exit(0);
                }
            });
            frame.setTitle("Saboteur - " + this.jugadorCliente.getId());

            chat = new JList<>();
            chat.setCellRenderer(new DefaultListCellRenderer() {

                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Mensaje) {
                        Mensaje mensaje = (Mensaje) value;
                        setText(mensaje.obtenerOrigen() + ": " + mensaje.getTexto());
                        if (mensaje.esDelSistema()) {
                            setForeground(Color.RED);
                        }
                    }
                    return this;
                }
            });

            JTextField textoDelUsuario = new JTextField();
            textoDelUsuario.setSize(500, 20);
            textoDelUsuario.setText("Escribir mensaje");

            botonEnviar = new JButton("Enviar");

            botonEnviar.addActionListener(event -> {
                String text = textoDelUsuario.getText();
                if (text != null && !text.isEmpty()) {
                    controladorJuego.enviarMensaje(new Mensaje(this.jugadorCliente, text));
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
                botonListo.setEnabled(false);
                controladorJuego.marcarListo(jugadorCliente);
            });

            Container panelPrincipal = frame.getContentPane();
            panelPrincipal.setLayout(new BorderLayout());

            JPanel panelDeJuego = new JPanel();
            panelDeJuego.setLayout(new GridLayout());

            panelTablero = new JPanel();
            panelTablero.setLayout(new GridLayout(7, 11));

            this.actualizarTablero();

            panelPrincipal.add(panelTablero, BorderLayout.CENTER);

            Container panelJugadores = new JPanel();
            panelJugadores.setLayout(new GridLayout(10, 1));
            panelJugadores.setPreferredSize(new Dimension(150, 0));

            listaDeJugadores = new JList<>();
            listaDeJugadores.setCellRenderer(new DefaultListCellRenderer() {

                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof IJugador) {
                        IJugador jugador = (IJugador) value;
                        setText(jugador.getId());
                        if (jugador.equals(jugadorCliente)) {
                            setForeground(Color.RED);
                        }
                    }
                    return this;
                }
            });
            listaDeJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            listaDeJugadores.addListSelectionListener(evento -> {
                int indiceDelJugador = evento.getFirstIndex();
                IJugador jugador = listaDeJugadores.getModel().getElementAt(indiceDelJugador);
                boolean esMiTurno = panelTablero.getCursor().getType() != Cursor.WAIT_CURSOR;
                if (esMiTurno) {
                    if (cartaSeleccionada != null) {
                        boolean resultadoAccion = false;
                        if (cartaSeleccionada instanceof CartaDeAccion) {
                            boolean dispararAccion = true;
                            if (ultimoClicSobreJugador == -1) {
                                ultimoClicSobreJugador = System.currentTimeMillis();
                            } else {
                                long ahora = System.currentTimeMillis();
                                if ((ahora - ultimoClicSobreJugador < 1000000000)) {
                                    ultimoClicSobreJugador = ahora;
                                    dispararAccion = false;
                                }
                            }
                            if (dispararAccion) {
                                System.out.println("Hiciste clic en el usuario " + jugador.getId());
                                resultadoAccion = controladorJuego.jugarCarta(jugador, (CartaDeAccion) cartaSeleccionada);
                                if (resultadoAccion) {
                                    Component componentToRemove = null;
                                    for (Component component : panelMano.getComponents()) {
                                        if (component instanceof CartaMano) {
                                            CartaDeJuego carta = ((CartaMano) component).getCarta();
                                            if (carta == cartaSeleccionada) {
                                                componentToRemove = component;
                                            }
                                        }
                                    }
                                    if (componentToRemove != null) {
                                        panelMano.remove(componentToRemove);
                                        panelMano.revalidate();
                                        panelMano.repaint();
                                    }
                                    cartaSeleccionada = null;
                                    controladorJuego.terminarTurno();
                                } else {
                                    System.err.println("Ocurrió un error");
                                }
                            }
                        } else {
                            System.err.println("No seleccionaste una carta de acción");
                        }
                    } else {
                        System.err.println("No seleccionaste ninguna carta");
                    }
                } else {
                    System.err.println("No es tu turno todavía");
                }
                listaDeJugadores.clearSelection();
            });

            panelJugadores.add(listaDeJugadores);

            panelPrincipal.add(panelJugadores, BorderLayout.WEST);

            this.actualizarJugadores();

            if (this.jugadorCliente.esMiTurno()) {
                panelTablero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                panelTablero.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            JComponent panelChat = new JPanel();
            // panelChat.setSize(100, 500);
            panelChat.setLayout(new GridLayout(2, 1));
            panelChat.add(chat);

            JComponent panelInferiorChat = new JPanel();
            panelInferiorChat.setLayout(new FlowLayout());
            panelInferiorChat.add(textoDelUsuario);
            panelInferiorChat.add(botonEnviar);
            panelChat.add(panelInferiorChat);
            panelChat.setPreferredSize(new Dimension(200, 0));

            panelPrincipal.add(panelChat, BorderLayout.EAST);

            panelMano = new JPanel();
            panelMano.setLayout(new GridLayout(1, 10));
            panelMano.add(botonListo);
            panelMano.setPreferredSize(new Dimension(0, 200));
            panelPrincipal.add(panelMano, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }

    @Override
    public void iniciar() {

    }

    @Override
    public void actualizarJugadores(List<IJugador> jugadores) {
        this.listaDeJugadores.setModel(new AbstractListModel<IJugador>() {

            @Override
            public int getSize() {
                return jugadores.size();
            }

            @Override
            public IJugador getElementAt(int index) {
                return jugadores.get(index);
            }
            
        });
    }

    @Override
    public void actualizarJugadores() {
        List<IJugador> jugadores = this.controladorJuego.obtenerJugadores();
        this.jugadores.clear();
        for (IJugador jugador : jugadores) {
            if (jugador.equals(this.jugadorCliente)) {
                // Actualizar la instancia con el último estado proveniente del servidor
                this.jugadorCliente = jugador;
            }
            this.jugadores.add(jugador);
        }
        this.actualizarJugadores(this.jugadores);
    }

    private void actualizarTurno() {
        if (this.jugadorCliente.esMiTurno()) {
            panelTablero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            panelTablero.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    @Override
    public void actualizarMensajes() {
        List<Mensaje> mensajes = this.controladorJuego.obtenerMensajes();
        this.chat.setModel(new AbstractListModel<Mensaje>() {

            @Override
            public int getSize() {
                return mensajes.size();
            }

            @Override
            public Mensaje getElementAt(int index) {
                return mensajes.get(index);
            }
        });
    }

    @Override
    public void iniciarJuego(Evento evento) {
        botonListo.setEnabled(false);
        panelMano.remove(botonListo);

        this.actualizar();
    }

    private void actualizarMano() {
        panelMano.removeAll();

        for (CartaDeJuego carta : this.jugadorCliente.getMano()) {
            CartaMano cartaLabel = new CartaMano(carta, controladorJuego);
            cartaLabel.setHorizontalAlignment(JLabel.CENTER);
            panelMano.add(cartaLabel);
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    @Override
    public IJugador getJugador() {
        return jugadorCliente;
    }

    @Override
    public void setJugador(IJugador jugador) {
        this.jugadorCliente = jugador;
    }

    @Override
    public void actualizarTablero(List<CartaDeTunel> tablero) {
        this.tablero = tablero;
        panelTablero.removeAll();
        for (int y = -1; y < 6; y++) {
            for (int x = -1; x < 10; x++) {
                CartaDeTunel cartaEnPosicion = null;
                for (CartaDeTunel cartaDeTunel : tablero) {
                    if (cartaDeTunel.getX() == x && cartaDeTunel.getY() == y) {
                        cartaEnPosicion = cartaDeTunel;
                        break;
                    }
                }
                CartaTablero label = new CartaTablero(cartaEnPosicion, x, y, controladorJuego);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setPreferredSize(new Dimension(11 * 3, 16 * 3));
                panelTablero.add(label);
            }
        }
        // https://stackoverflow.com/questions/7117332/dynamically-remove-component-from-jpanel
        panelTablero.revalidate();
        panelTablero.repaint();
    }

    @Override
    public void actualizarTablero() {
        List<CartaDeTunel> tablero = this.controladorJuego.obtenerTablero();
        this.actualizarTablero(tablero);
    }

    @Override
    public void actualizar() {
        this.actualizarJugadores();
        this.actualizarTurno();
        this.actualizarTablero();
        this.actualizarMano();
    }

    /**
     * Representa un espacio (slot) en el tablero, que puede estar libre o no.
     *
     */
    public class CartaTablero extends JLabel {
    
        /**
         * 
         */
        private static final long serialVersionUID = 5370723875059488045L;
    
        private CartaDeJuego carta;
        private Integer x;
        private Integer y;
    
        public CartaTablero(CartaDeJuego carta, Integer x, Integer y, ControladorJuego controlador) {
            super();
            this.x = x;
            this.y = y;
            this.setCarta(carta);
            this.setOpaque(true);
    
            addMouseListener(new MouseAdapter() {
    
                @Override
                public void mouseClicked(MouseEvent event) {
                    System.out.println("[" + jugadorCliente.getId() + "] -> " + jugadorCliente.esMiTurno());
                    if (jugadorCliente.esMiTurno()) {
                        if (jugadorCliente.getHerramientasRotas().isEmpty()) {
                            if (cartaSeleccionada != null) {
                                boolean resultadoAccion = false;
                                cartaSeleccionada.setPosicion(getPosX(), getPosY());
                                if (cartaSeleccionada instanceof CartaDeTunel) {
                                    boolean estaLibre = ((CartaTablero) event.getComponent()).getCarta() == null;
                                    if (estaLibre) {
                                        resultadoAccion = controlador.jugarCarta((CartaDeTunel) cartaSeleccionada);
                                    } else {
                                        System.err.println("Ya hay una carta en ese lugar");
                                    }
                                } else if (cartaSeleccionada instanceof CartaDeAccion) {
                                    resultadoAccion = controlador.jugarCarta((CartaDeAccion) cartaSeleccionada);
                                }
                                if (resultadoAccion) {
                                    Component componentToRemove = null;
                                    for (Component component : panelMano.getComponents()) {
                                        if (component instanceof CartaMano) {
                                            CartaDeJuego carta = ((CartaMano) component).getCarta();
                                            if (carta == cartaSeleccionada) {
                                                componentToRemove = component;
                                            }
                                        }
                                    }
                                    if (componentToRemove != null) {
                                        panelMano.remove(componentToRemove);
                                        panelMano.revalidate();
                                        panelMano.repaint();
                                    }
                                    cartaSeleccionada = null;
                                    controladorJuego.terminarTurno();
                                } else {
                                    System.err.println("Ocurrió un error");
                                }
                            } else {
                                System.err.println("Tenés que seleccionar una carta primero");
                            }
                        } else {
                            System.err.println("Tenés herramientas rotas");
                        }
                    } else {
                        System.err.println("No es tu turno todavía");
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
    
        public void setCarta(CartaDeJuego carta) {
            // TODO EXE - Generar texto que represente las entradas y el centro de la carta
            this.carta = carta;
            if (carta == null) {
                this.setText("X");
            } else {
                StringBuilder sb = new StringBuilder("<html><pre>");
                if (carta instanceof CartaDeAccion) {
                    sb.append(carta.getId() + "<br>");
                    sb.append("A<br>");
                    CartaDeAccion c = (CartaDeAccion) carta;
                    if (c.getTipos().contains(TipoCartaAccion.DERRUMBE)) {
                        sb.append("D");
                    } else if (c.getTipos().contains(TipoCartaAccion.MAPA)) {
                        sb.append("D");
                    } else if (c.esCartaDeHerramientaReparada()) {
                        sb.append("REP");
                    } else if (c.esCartaDeHerramientaRota()) {
                        sb.append("ROT");
                    }
                } else if (carta instanceof CartaDeTunel) {
                    CartaDeTunel c = (CartaDeTunel) carta;
                    if (c.getTipo() == TipoCartaTunel.INICIO
                            || c.getTipo() == TipoCartaTunel.DESTINO_ORO
                            || c.getTipo() == TipoCartaTunel.DESTINO_PIEDRA) {
                        this.setBackground(Color.BLACK);
                        this.setForeground(Color.WHITE);
                    }
                    if (c.estaDadaVuelta()) {
                        sb.append("    ?    ");
                    } else if (c.getTipo() == TipoCartaTunel.DESTINO_ORO) {
                        sb.append("   ORO   ");
                        this.setBackground(Color.YELLOW);
                        this.setForeground(Color.BLACK);
                    } else if (c.getTipo() == TipoCartaTunel.DESTINO_PIEDRA){
                        sb.append("..PIEDRA..");
                        this.setBackground(Color.GRAY);
                        this.setForeground(Color.BLACK);
                    } else {
                        sb.append(carta.getId());
                        List<Entrada> entradas = c.getEntradas();
                        if (entradas.contains(Entrada.NORTE)) {
                            sb.append(carta.getId() < 10 ? "   &#8593;    " : "  &#8593;    ");
                        } else {
                            sb.append("         ");
                        }
                        sb.append("<br>");
                        if (entradas.contains(Entrada.OESTE)) {
                            sb.append("&#8592;   ");
                        } else {
                            sb.append("    ");
                        }
                        if (c.isSinSalida()) {
                            sb.append("x");
                        } else {
                            sb.append(" ");
                        }
                        if (entradas.contains(Entrada.ESTE)) {
                            sb.append("   &#8594;");
                        } else {
                            sb.append("    ");
                        }
                        sb.append("<br>");
                        if (entradas.contains(Entrada.SUR)) {
                            sb.append("    &#8595;    ");
                        } else {
                            sb.append("         ");
                        }
                    }
                }
                // https://stackoverflow.com/questions/1090098/newline-in-jlabel
                sb.append("</pre></html>");
                this.setText(sb.toString());
                this.carta.setPosicion(this.x, this.y);
            }
        }

        public Integer getPosX() {
            return this.x;
        }

        public Integer getPosY() {
            return this.y;
        }
    }

    /**
     * Representa una carta en la mano del jugador.
     *
     */
    public class CartaMano extends JLabel {
    
        /**
         * 
         */
        private static final long serialVersionUID = 5370723875059488045L;
    
        private CartaDeJuego carta;
    
        public CartaMano(CartaDeJuego carta, ControladorJuego controlador) {
            super();
            this.setCarta(carta);
    
            addMouseListener(new MouseAdapter() {
    
                @Override
                public void mouseClicked(MouseEvent event) {
                    System.out.println("[" + jugadorCliente.getId() + "] -> " + jugadorCliente.esMiTurno());
                    if (jugadorCliente.esMiTurno()) {
                        cartaSeleccionada = carta;
                        System.out.println("Seleccionaste la carta " + carta.getId());
                    } else {
                        System.err.println("No es tu turno todavía");
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
    
        public void setCarta(CartaDeJuego carta) {
            // TODO EXE - Generar texto que represente las entradas y el centro de la carta
            this.carta = carta;
            StringBuilder sb = new StringBuilder("<html><pre>");
            sb.append(carta.getId());
            if (carta instanceof CartaDeAccion) {
                sb.append(" A ");
                CartaDeAccion c = (CartaDeAccion) carta;
                if (c.getTipos().contains(TipoCartaAccion.DERRUMBE)) {
                    sb.append("D");
                } else if (c.getTipos().contains(TipoCartaAccion.MAPA)) {
                    sb.append("D");
                } else if (c.esCartaDeHerramientaReparada()) {
                    sb.append("REP");
                } else if (c.esCartaDeHerramientaRota()) {
                    sb.append("ROT");
                }
            } else if (carta instanceof CartaDeTunel) {
                CartaDeTunel c = (CartaDeTunel) carta;
                List<Entrada> entradas = c.getEntradas();
                if (entradas.contains(Entrada.NORTE)) {
                    sb.append(carta.getId() < 10 ? "   &#8593;    " : "  &#8593;    ");
                } else {
                    sb.append("         ");
                }
                sb.append("<br>");
                if (entradas.contains(Entrada.OESTE)) {
                    sb.append("&#8592;   ");
                } else {
                    sb.append("    ");
                }
                if (c.isSinSalida()) {
                    sb.append("x");
                } else {
                    sb.append(" ");
                }
                if (entradas.contains(Entrada.ESTE)) {
                    sb.append("   &#8594;");
                } else {
                    sb.append("    ");
                }
                sb.append("<br>");
                if (entradas.contains(Entrada.SUR)) {
                    sb.append("    &#8595;    ");
                } else {
                    sb.append("         ");
                }
            }
            sb.append("</pre></html>");
            this.setText(sb.toString());
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
    
        }}
}
