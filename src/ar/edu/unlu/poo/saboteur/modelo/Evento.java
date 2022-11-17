package ar.edu.unlu.poo.saboteur.modelo;

import java.io.Serializable;
import java.util.List;

public class Evento implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7521774005062357442L;
    private String idJugadorOrigen;
    private String idJugadorDestino;
    private byte x;
    private byte y;
    private TipoEvento tipoEvento;
    private byte idCarta;
    private List<? extends IJugadorBase> jugadores;
    

    public Evento(TipoEvento tipoEvento) {
        super();
        this.tipoEvento = tipoEvento;
    }

    public Evento(TipoEvento tipoEvento, String idJugadorOrigen) {
        super();
        this.tipoEvento = tipoEvento;
        this.idJugadorOrigen = idJugadorOrigen;
    }

    public Evento(TipoEvento tipoEvento, List<? extends IJugadorBase> jugadores) {
        super();
        this.tipoEvento = tipoEvento;
        this.jugadores = jugadores;
    }

    /**
     * Este constructor se debe usar cuando un jugador usa una carta que se aplica sobre la grilla.
     * 
     * Cada cliente debe implementar saber qué hacer dependiendo del tipo de carta. 
     * 
     * Este constructor se debe usar cuando un jugador:
     * 
     *  <ul>
     *      <li>Usa la carta de mapa</li>
     *      <li>Usa una carta de túnel</li>
     *      <li>Usa una carta de derrumbe</li>
     *  </ul>
     * 
     * @param tipoEvento
     * @param idJugadorOrigen
     * @param idCarta
     * @param x
     * @param y
     */
    public Evento(String idJugadorOrigen, byte idCarta, byte x, byte y) {
        this(TipoEvento.USA_CARTA);
        this.idJugadorOrigen = idJugadorOrigen;
        this.idCarta = idCarta;
        this.x = x;
        this.y = y;
    }

    /**
     * Solo para cuando un jugador coloca una carta en la pila de descarte
     * 
     * @param idJugadorOrigen
     */
    public Evento(String idJugadorOrigen) {
        this(TipoEvento.DESCARTA);
        this.idJugadorOrigen = idJugadorOrigen;
    }

    /**
     * Constructor para cuando un jugador le aplica una carta de herramienta rota o reparada a otro jugador o a sí mismo
     *  
     * @param tipoEvento
     * @param idJugadorOrigen
     * @param idJugadorDestino
     * @param idCarta
     */
    public Evento(String idJugadorOrigen, String idJugadorDestino, byte idCarta) {
        this(TipoEvento.USA_CARTA);
        this.idJugadorOrigen = idJugadorOrigen;
        this.idJugadorDestino = idJugadorDestino;
        this.idCarta = idCarta;
    }

    public String getIdJugadorOrigen() {
        return idJugadorOrigen;
    }

    public String getIdJugadorDestino() {
        return idJugadorDestino;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public byte getIdCarta() {
        return idCarta;
    }

    public List<? extends IJugadorBase> getJugadores() {
        return jugadores;
    }

    public IJugador obtenerJugador(String idJugador) {
        return (IJugador) jugadores
                .stream()
                .filter(jugador -> idJugador.equals(jugador.getId()))
                .findFirst()
                .orElse(null);
    }
}
