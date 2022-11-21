package ar.edu.unlu.poo.saboteur.vista.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
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
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputListener;

import ar.edu.unlu.poo.saboteur.controlador.ControladorJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.Evento;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.TipoEvento;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.Mensaje;
import ar.edu.unlu.poo.saboteur.vista.IVista;

public class VistaGrafica implements IVista {

    //private GeneradorDeImagenes generadorDeImagenes = GeneradorDeImagenes.getInstance();

    final static Font FONT = new Font(null, Font.PLAIN, 14);

    private Container panelDePaneles;
    private CardLayout paneles;
    final static String PANEL_JUEGO = "PanelDeJuego";
    final static String PANEL_RESULTADOS = "PanelDeResultados";

    private ControladorJuego controladorJuego;

    private IJugador jugadorCliente;
    private JButton botonEnviar;
    private JButton botonListo;
    private JComponent panelTablero;
    private CartaDeJuego cartaSeleccionada;
    // Work-around para que no se disparen varios clics seguido en la lista de jugadores
    private long ultimoClicSobreJugador = -1;

    private JPanel panelMano;

    final Border LABEL_BORDER = BorderFactory.createLineBorder(Color.BLUE, 2);
    //final Border LABEL_BORDER = BorderFactory.createRaisedBevelBorder();

    private JList<IJugador> listaDeJugadores;

    private JList<Mensaje> chat;

    private JPanel jugadoresResultados;

    private JButton botonContinuar;

    public VistaGrafica(ControladorJuego controladorJuego, IJugador jugadorCliente) {
        this.jugadorCliente = jugadorCliente;

        this.controladorJuego = controladorJuego;
        this.controladorJuego.setVista(this);

        EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();
            frame.setSize(1280, 720);
            //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
            frame.setTitle("Saboteur - " + this.jugadorCliente.getNombre());

            Container panelPrincipal = frame.getContentPane();
            //panelPrincipal.setLayout();

            panelDePaneles = new JPanel();
            panelDePaneles.setLayout(new CardLayout());

            this.paneles = (CardLayout) panelDePaneles.getLayout();

            panelDePaneles.add(this.crearPanelDeJuego(), PANEL_JUEGO);
            panelDePaneles.add(this.crearPanelDeResultados(), PANEL_RESULTADOS);

            panelPrincipal.add(panelDePaneles);

            /*
            tableroVisible = true;

            JButton switchPanelButton = new JButton("Switch panel");
            switchPanelButton.addActionListener(evento -> {
                if (tableroVisible) {
                    mostrarResultados(TipoEvento.FIN_RONDA);
                } else {
                    mostrarTablero();
                }
                tableroVisible = !tableroVisible;
            });

            panelPrincipal.add(switchPanelButton);
            */

            frame.setVisible(true);
        });
    }

    private Container crearPanelDeResultados() {
        JPanel panelDeResultados = new JPanel();
        panelDeResultados.setLayout(new GridBagLayout());
        jugadoresResultados = new JPanel();
        jugadoresResultados.setLayout(new GridLayout(10, 1));
        jugadoresResultados.setFont(FONT);

        panelDeResultados.add(jugadoresResultados);

        JLabel titulo = new JLabel("FIN DE LA RONDA");
        panelDeResultados.add(titulo);
        botonContinuar = new JButton("Continuar");
        botonContinuar.addActionListener(evento -> {
            botonContinuar.setEnabled(false);
            controladorJuego.marcarListo(jugadorCliente);
        });
        panelDeResultados.add(botonContinuar);
        return panelDeResultados;
    }

    private Container crearPanelDeJuego() {
        chat = new JList<>();
        chat.setCellRenderer(new DefaultListCellRenderer() {

            /**
             * 
             */
            private static final long serialVersionUID = 4827397224790241362L;

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

        chat.setFont(FONT);
        chat.setSelectionModel(new DefaultListSelectionModel() {

            /**
             * 
             */
            private static final long serialVersionUID = -1938719531367585786L;

            @Override
            public void setAnchorSelectionIndex(int anchorIndex) {
                
            }

            @Override
            public void setLeadAnchorNotificationEnabled(boolean flag) {
                
            }

            @Override
            public void setLeadSelectionIndex(int leadIndex) {
                
            }

            @Override
            public void setSelectionInterval(int index0, int index1) {
                
            }
        });

        JScrollPane scrollablePane = new JScrollPane(chat);
        scrollablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JTextField textoDelUsuario = new JTextField();
        textoDelUsuario.setFont(FONT);
        textoDelUsuario.setSize(500, 20);
        textoDelUsuario.setText("Escribir mensaje");

        botonEnviar = new JButton("Enviar");
        botonEnviar.setFont(FONT);

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
            int cantidadJugadores = controladorJuego.obtenerJugadores().size();
            if (cantidadJugadores >= 3 && cantidadJugadores <= 10) {
                botonListo.setEnabled(false);
                controladorJuego.marcarListo(jugadorCliente);
            } else {
                System.err.println("Debe haber entre 3 y 10 jugadores inclusive para poder jugar");
            }
        });

        Container panelDeJuego = new JPanel();
        panelDeJuego.setLayout(new BorderLayout());

        panelTablero = new JPanel();
        panelTablero.setLayout(new GridLayout(7, 11));

        //this.actualizarTablero();

        panelDeJuego.add(panelTablero, BorderLayout.CENTER);

        Container panelJugadores = new JPanel();
        panelJugadores.setLayout(new FlowLayout());
        //panelJugadores.setPreferredSize(new Dimension(200, 500));
        TitledBorder border = new TitledBorder("Jugadores");
        border.setTitleFont(FONT);
        ((JPanel) panelJugadores).setBorder(border);

        listaDeJugadores = new JList<>();
        listaDeJugadores.setCellRenderer(new DefaultListCellRenderer() {

            /**
             * 
             */
            private static final long serialVersionUID = -216238111892207439L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof IJugador) {
                    IJugador jugador = (IJugador) value;

                    StringBuilder sb = new StringBuilder("<html>")
                        .append(jugador.getNombre())
                        .append("<br>");
                    sb.append(jugador.getHerramientasRotas()
                            .stream()
                            .map(herramientaRota -> "\t-".concat(herramientaRota.getTipos().get(0).name()))
                            .collect(Collectors.joining("<br>")));
                    sb.append("</html>");
                    setText(sb.toString());

                    if (jugador.equals(jugadorCliente)) {
                        setForeground(Color.RED);
                    }
                }
                return this;
            }
        });
        listaDeJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaDeJugadores.setFont(FONT);

        listaDeJugadores.addListSelectionListener(evento -> {
            int indiceDelJugador = evento.getFirstIndex();
            IJugador jugador = listaDeJugadores.getModel().getElementAt(indiceDelJugador);

            if (jugadorCliente.esMiTurno()) {
                if (cartaSeleccionada != null && !jugador.equals(jugadorCliente)) {
                    boolean resultadoAccion = false;
                    if (cartaSeleccionada instanceof CartaDeAccion) {
                        boolean dispararAccion = true;
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
                            resultadoAccion = controladorJuego.jugarCarta(jugador, (CartaDeAccion) cartaSeleccionada);
                            if (resultadoAccion) {
                                //removerCartaSeleccionadaDeLaMano();
                                controladorJuego.avanzar();
                            } else {
                                JOptionPane.showConfirmDialog(null, "Error");
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

        panelDeJuego.add(panelJugadores, BorderLayout.WEST);

        this.actualizarJugadoresTablero();

        if (this.jugadorCliente.esMiTurno()) {
            panelTablero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            panelTablero.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        JComponent panelChat = new JPanel();
        // panelChat.setSize(100, 500);
        panelChat.setLayout(new GridLayout(2, 1));
        panelChat.add(scrollablePane);

        JComponent panelInferiorChat = new JPanel();
        panelInferiorChat.setLayout(new FlowLayout());
        panelInferiorChat.add(textoDelUsuario);
        panelInferiorChat.add(botonEnviar);
        panelChat.add(panelInferiorChat);
        panelChat.setPreferredSize(new Dimension(200, 0));

        panelDeJuego.add(panelChat, BorderLayout.EAST);

        panelMano = new JPanel();
        panelMano.setLayout(new GridLayout(1, 10));
        panelMano.add(botonListo);
        panelMano.setPreferredSize(new Dimension(0, 200));
        panelDeJuego.add(panelMano, BorderLayout.SOUTH);

        return panelDeJuego;
    }

    /*
    private void removerCartaSeleccionadaDeLaMano() {
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
    }
    */

    @Override
    public void iniciar() {

    }

    @Override
    public void actualizarVistaJugadores(List<IJugador> jugadores) {
        this.listaDeJugadores.setModel(new AbstractListModel<IJugador>() {

            /**
             * 
             */
            private static final long serialVersionUID = 8994216166367505605L;

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
    public void actualizarJugadoresTablero() {
        List<IJugador> jugadores = this.controladorJuego.obtenerJugadores();
        for (IJugador jugador : jugadores) {
            if (jugador.equals(this.jugadorCliente)) {
                // Actualizar la instancia con el último estado proveniente del servidor
                this.jugadorCliente = jugador;
            }
        }
        this.actualizarVistaJugadores(jugadores);
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

            /**
             * 
             */
            private static final long serialVersionUID = 4015518047830903054L;

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
    public void iniciarRonda(Evento evento) {
        panelMano.remove(botonListo);
        this.mostrarTablero();
    }

    // TODO EXE - No hay motivo para que esto esté en la interfaz. Hacerlo privado
    @Override
    public void actualizarMano() {
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
    public void actualizarPanelTablero() {
        this.actualizarJugadoresTablero();
        this.actualizarTurno();
        this.actualizarTablero();
        this.actualizarMano();
        // TODO EXE - Mover el componente de mensajes hacia afuera del panel del tablero
        // para que se pueda usar también en la pantalla de resultados
        this.actualizarMensajes();
    }

    private void actualizarPanelResultados(TipoEvento tipoEvento) {
        List<IJugador> jugadores = this.controladorJuego.obtenerJugadores();
        jugadores.sort((o1, o2) -> o2.calcularPuntaje() - o1.calcularPuntaje());
        this.jugadoresResultados.removeAll();
        if (tipoEvento == TipoEvento.FIN_RONDA) {
            this.jugadoresResultados.setBorder(new TitledBorder("Resultado de la ronda"));
            jugadores
                .stream()
                .map(jugador -> new StringBuilder()
                        .append(jugador.getNombre())
                        .append(" ")
                        // TODO EXE - Sacar lo de ERROR. Es solo mientras hago pruebas
                        .append(jugador.getRol() == null ? "ERROR" : jugador.getRol().name())
                        .append(" -> ")
                        .append(jugador.calcularPuntaje())
                        .toString())
                .map(JLabel::new)
                .forEach(this.jugadoresResultados::add);
            this.botonContinuar.setEnabled(true);
        } else if (tipoEvento == TipoEvento.FIN_JUEGO) {
            this.jugadoresResultados.setBorder(new TitledBorder("Resultado de la ronda"));
            this.jugadoresResultados.removeAll();
            jugadores
                .stream()
                .map(jugador -> {
                    String contenidoLabel = new StringBuilder()
                        .append(jugador.getNombre())
                        .append(" ")
                        .append(jugador.getRol().name())
                        .append(" -> ")
                        .append(jugador.calcularPuntaje())
                        .toString();
                    JLabel label = new JLabel(contenidoLabel);
                    if (jugador.equals(jugadorCliente)) {
                        label.setForeground(Color.RED);
                    }
                    return label;
                })
                .forEach(this.jugadoresResultados::add);
            this.botonContinuar.setVisible(false);
        }
        this.jugadoresResultados.revalidate();
        this.jugadoresResultados.repaint();
    }

    @Override
    public void mostrarTablero() {
        this.paneles.show(this.panelDePaneles, PANEL_JUEGO);
        this.actualizarPanelTablero();
    }

    @Override
    public void mostrarResultados(TipoEvento tipoEvento) {
        this.paneles.show(this.panelDePaneles, PANEL_RESULTADOS);
        this.actualizarPanelResultados(tipoEvento);
    }

    public String obtenerRepresentacionGraficaCarta(CartaDeJuego carta, JLabel label) {
        if (carta == null) {
            return "X";
        }

        StringBuilder sb = new StringBuilder("<html><pre>");
        if (carta instanceof CartaDeAccion) {
            sb.append(carta.getId() + "<br>");
            CartaDeAccion c = (CartaDeAccion) carta;
            if (c.getTipos().contains(TipoCartaAccion.DERRUMBE)) {
                sb.append("DERRUMBE");
            } else if (c.getTipos().contains(TipoCartaAccion.MAPA)) {
                sb.append("MAPA");
            } else {
                sb.append(c.getTipos().stream().map(TipoCartaAccion::name).collect(Collectors.joining("<br>")));
            }
        } else if (carta instanceof CartaDeTunel) {
            CartaDeTunel c = (CartaDeTunel) carta;
            if (c.getTipo() == TipoCartaTunel.INICIO
                    || c.getTipo() == TipoCartaTunel.DESTINO_ORO
                    || c.getTipo() == TipoCartaTunel.DESTINO_PIEDRA) {
                label.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
            }
            if (!c.isVisible()) {
                sb.append("    ?    ");
            } else if (c.getTipo() == TipoCartaTunel.DESTINO_ORO) {
                sb.append("   ORO   ");
                label.setBackground(Color.YELLOW);
                label.setForeground(Color.BLACK);
            } else if (c.getTipo() == TipoCartaTunel.DESTINO_PIEDRA){
                sb.append("  PIEDRA  ");
                label.setBackground(Color.GRAY);
                label.setForeground(Color.BLACK);
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
        return sb.toString();
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
            this.setFont(FONT);
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
                                    //removerCartaSeleccionadaDeLaMano();
                                    controladorJuego.avanzar();
                                } else {
                                    JOptionPane.showConfirmDialog(null, "Error");
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
            this.carta = carta;

            this.setText(obtenerRepresentacionGraficaCarta(carta, this));
            if (carta != null) {
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
            this.setFont(FONT);
            this.setCarta(carta);
    
            addMouseListener(new MouseAdapter() {
    
                @Override
                public void mouseClicked(MouseEvent event) {
                    System.out.println("[" + jugadorCliente.getNombre() + "] -> " + jugadorCliente.esMiTurno());
                    if (jugadorCliente.esMiTurno()) {
                        if (event.getButton() == MouseEvent.BUTTON1) {
                            cartaSeleccionada = carta;
                            System.out.println("Seleccionaste la carta " + carta.getId());
                        } else if (event.getButton() == MouseEvent.BUTTON3) {
                            cartaSeleccionada = null;
                            controladorJuego.descartar(carta);
                            controladorJuego.avanzar();
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
            this.carta = carta;
            this.setText(obtenerRepresentacionGraficaCarta(carta, this));
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
