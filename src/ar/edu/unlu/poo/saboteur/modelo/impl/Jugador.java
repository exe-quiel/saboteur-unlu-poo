package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
        this.mano = new ArrayList<>();
    }

    public void inicializar() {
        this.mano.clear();
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

    @Override
    public boolean repararHerramienta(CartaDeAccion cartaDeReparacion) {
        List<TipoCartaAccion> tiposABuscar = cartaDeReparacion.getTipos()
                .stream()
                .map(tipo -> tipo.getCartaQueRepara())
                .collect(Collectors.toList());

        Iterator<CartaDeAccion> herramientasRotasIterator = this.herramientasRotas.iterator();
        boolean removida = false;
        while (herramientasRotasIterator.hasNext() && !removida) {
            CartaDeAccion carta = herramientasRotasIterator.next();
            TipoCartaAccion tipoCartaRota = carta.getTipos().get(0);
            if (tiposABuscar.contains(tipoCartaRota)) {
                herramientasRotasIterator.remove();
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

    @Override
    public void setRol(RolJugador rol) {
        this.rol = rol;
    }

    @Override
    public void removerCartaDeLaMano(CartaDeJuego carta) {
        if (this.mano.contains(carta)) {
            this.mano.remove(carta);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Jugador)) {
            return false;
        }
        Jugador otroJugador = (Jugador) object;
        return this.id.equals(otroJugador.getId());
        // TODO EXE - Ver tema hashcode
    }

    @Override
    public String getId() {
        return this.id;
    }
}
