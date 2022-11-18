package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDePuntos;
import ar.edu.unlu.poo.saboteur.modelo.IJugador;
import ar.edu.unlu.poo.saboteur.modelo.RolJugador;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;

public class Jugador implements IJugador, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3336584393172378611L;
    private final String id;
    private List<CartaDeJuego> mano;
    private List<CartaDePuntos> puntaje;
    private List<CartaDeAccion> herramientasRotas;
    private boolean listo;
    private RolJugador rol;

    public Jugador(String id) {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public int calcularPuntaje() {
        return puntaje
                .stream()
                .mapToInt(CartaDePuntos::getPuntos)
                .sum();
    }

    @Override
    public boolean romperHerramienta(CartaDeAccion cartaDeAccion) {
        TipoCartaAccion tipo = cartaDeAccion.getTipos().get(0);
        boolean yaEstaRota = herramientasRotas
                .stream()
                .anyMatch(carta -> carta.getTipos().get(0) == tipo);
        if (yaEstaRota) {
            return false;
        }
        return herramientasRotas.add(cartaDeAccion);
    }

    public boolean arreglarHerramienta(CartaDeAccion cartaDeAccion) {
        List<TipoCartaAccion> tiposABuscar = cartaDeAccion.getTipos();
        TipoCartaAccion tipoABuscar = null;
        switch (tipo) {
        case LAMPARA_REPARADA:
            tipoABuscar = TipoCartaAccion.LAMPARA_ROTA;
            break;
        case CARRETILLA_REPARADA:
            tipoABuscar = TipoCartaAccion.CARRETILLA_ROTA;
            break;
        case PICO_REPARADO:
            tipoABuscar = TipoCartaAccion.PICO_ROTO;
            break;
        default:
            throw new RuntimeException("ERROR, CARTA INESPERADA");
        }
        Iterator<CartaDeAccion> herramientasRotasIt = herramientasRotas.iterator();
        boolean removida = false;
        while (herramientasRotasIt.hasNext() && !removida) {
            CartaDeAccion carta = (CartaDeAccion) herramientasRotasIt.next();
            if (carta.getTipo() == tipoABuscar) {
                herramientasRotasIt.remove();
                removida = true;
            }
        }
        return removida;
    }

    public void recibirCartas(List<CartaDeJuego> cartasDeJuego) {
        this.mano = cartasDeJuego;
    }

    public void marcarListo() {
        this.listo = true;
    }

    public boolean getListo() {
        return listo;
    }

    public void setHerramientasRotas(List<CartaDeAccion> herramientasRotas) {
        this.herramientasRotas = herramientasRotas;
    }

    @Override
    public List<CartaDeAccion> getHerramientasRotas() {
        return herramientasRotas;
    }

    @Override
    public List<CartaDeJuego> getMano() {
        return mano;
    }

    @Override
    public RolJugador getRol() {
        return rol;
    }
}
