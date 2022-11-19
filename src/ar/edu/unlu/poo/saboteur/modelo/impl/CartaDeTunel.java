package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;

public class CartaDeTunel implements CartaDeJuego {

    private TipoCartaTunel tipo;
    private boolean sinSalida;
    private List<Entrada> entradas;

    private int x;
    private int y;

    private CartaDeTunel norte;
    private CartaDeTunel sur;
    private CartaDeTunel este;
    private CartaDeTunel oeste;

    public CartaDeTunel(TipoCartaTunel tipo, boolean sinSalida, List<Entrada> entradas, String descripcion) {
        super();
        this.tipo = tipo;
        this.sinSalida = sinSalida;
        this.entradas = entradas;
        this.x = -1;
        this.y = -1;
    }

    public TipoCartaTunel getTipo() {
        return tipo;
    }

    public boolean isSinSalida() {
        return sinSalida;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CartaDeTunel getNorte() {
        return norte;
    }

    public boolean setNorte(CartaDeTunel carta) {
        if (this.entradas.contains(Entrada.NORTE) && carta.getEntradas().contains(Entrada.SUR)) {
            this.norte = carta;
            carta.setSur(this);
            return true;
        }
        return false;
    }

    public CartaDeTunel getSur() {
        return sur;
    }

    public boolean setSur(CartaDeTunel carta) {
        // TODO EXE - Considerar caso en el que derrumban la carta para todas las direcciones (carta == null)
        if (this.entradas.contains(Entrada.SUR) && carta.getEntradas().contains(Entrada.NORTE)) {
            this.sur = carta;
            carta.setNorte(this);
            return true;
        }
        return false;
    }

    public CartaDeTunel getEste() {
        return este;
    }

    public boolean setEste(CartaDeTunel carta) {
        if (this.entradas.contains(Entrada.ESTE) && carta.getEntradas().contains(Entrada.OESTE)) {
            this.este = carta;
            carta.setOeste(this);
            return true;
        }
        return false;
    }

    public CartaDeTunel getOeste() {
        return oeste;
    }

    public boolean setOeste(CartaDeTunel carta) {
        if (this.entradas.contains(Entrada.OESTE) && carta.getEntradas().contains(Entrada.ESTE)) {
            this.oeste = carta;
            carta.setEste(this);
            return true;
        }
        return false;
    }

    public boolean estaConectadaConCarta(CartaDeTunel carta) {
        if (this == carta) {
            return true;
        }

        if (norte != null && norte.estaConectadaConCarta(carta)) {
            return true;
        }

        if (sur != null && sur.estaConectadaConCarta(carta)) {
            return true;
        }

        if (este != null && este.estaConectadaConCarta(carta)) {
            return true;
        }

        if (oeste != null && oeste.estaConectadaConCarta(carta)) {
            return true;
        }

        return false;
    }

    public boolean colocarCartaEnElTablero(byte x, byte y, List<CartaDeTunel> cartasEnElTablero) {
        this.x = x;
        this.y = y;

        // Conectar la carta con las contiguas
        for (CartaDeTunel otraCarta : cartasEnElTablero) {
            boolean movimientoValido = true;

            if (otraCarta.getX() == this.x - 1 && otraCarta.getY() == this.y) {

                movimientoValido = this.setOeste(otraCarta) && otraCarta.setEste(this);

            } else if (otraCarta.getX() == this.x + 1 && otraCarta.getY() == this.y) {

                movimientoValido =this.setEste(otraCarta) && otraCarta.setOeste(this);

            } if (otraCarta.getX() == this.x && otraCarta.getY() == this.y - 1) {

                movimientoValido = this.setNorte(otraCarta) && otraCarta.setSur(this);

            } else if (otraCarta.getX() == this.x && otraCarta.getY() == this.y + 1) {

                movimientoValido = this.setSur(otraCarta) && otraCarta.setNorte(this);
            }

            if (!movimientoValido) {
                return false;
            }
        }

        return true;
    }

    public void derrumbar() {
        this.norte.setSur(null);
        this.norte = null;

        this.sur.setNorte(null);
        this.sur = null;

        this.oeste.setEste(null);
        this.oeste = null;

        this.este.setOeste(null);
        this.este = null;

        this.x = -1;
        this.y = -1;
    }

    public boolean colisionaCon(CartaDeJuego carta) {
        return this.x == carta.getX() && this.y == carta.getY();
    }

    public boolean admiteConexion(CartaDeTunel carta) {
        if (!colisionaCon(carta)) {
            if (this.y < carta.getY()) {
                return this.entradas.contains(Entrada.SUR)
                        && carta.getEntradas().contains(Entrada.NORTE);
            }
            if (this.y > carta.getY()) {
                return this.entradas.contains(Entrada.NORTE)
                        && carta.getEntradas().contains(Entrada.SUR);
            }
            if (this.x < carta.getX()) {
                return this.entradas.contains(Entrada.ESTE)
                        && carta.getEntradas().contains(Entrada.OESTE);
            }
            if (this.x > carta.getX()) {
                return this.entradas.contains(Entrada.OESTE)
                        && carta.getEntradas().contains(Entrada.ESTE);
            }
        }
        return false;
    }
}
