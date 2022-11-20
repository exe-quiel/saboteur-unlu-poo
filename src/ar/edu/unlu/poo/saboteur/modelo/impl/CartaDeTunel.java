package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.util.List;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;

public class CartaDeTunel extends CartaDeJuego {

    /**
     * 
     */
    private static final long serialVersionUID = 87353013422380149L;
    private TipoCartaTunel tipo;
    private boolean sinSalida;
    private List<Entrada> entradas;

    private CartaDeTunel norte;
    private CartaDeTunel sur;
    private CartaDeTunel este;
    private CartaDeTunel oeste;

    public CartaDeTunel(int id, TipoCartaTunel tipo, boolean sinSalida, List<Entrada> entradas) {
        super(id);
        this.tipo = tipo;
        this.sinSalida = sinSalida;
        this.entradas = entradas;
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

    public CartaDeTunel getNorte() {
        return norte;
    }

    public boolean setNorte(CartaDeTunel carta) {
     // En caso de derrumbe de la carta contigua
        if (carta == null) {
            this.norte = null;
            return true;
        }
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
        // En caso de derrumbe de la carta contigua
        if (carta == null) {
            this.sur = null;
            return true;
        }
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
     // En caso de derrumbe de la carta contigua
        if (carta == null) {
            this.este = null;
            return true;
        }
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
     // En caso de derrumbe de la carta contigua
        if (carta == null) {
            this.oeste = null;
            return true;
        }
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

    public void derrumbar() {
        this.norte.setSur(null);
        this.norte = null;

        this.sur.setNorte(null);
        this.sur = null;

        this.oeste.setEste(null);
        this.oeste = null;

        this.este.setOeste(null);
        this.este = null;

        this.setPosicion(-1, -1);
    }

    public boolean colisionaCon(CartaDeJuego carta) {
        return this.getX() == carta.getX() && this.getY() == carta.getY();
    }

    public boolean admiteConexion(CartaDeTunel carta) {
        if (!colisionaCon(carta)) {
            if (this.getY() < carta.getY()) {
                return (this.entradas.contains(Entrada.SUR)
                        && carta.getEntradas().contains(Entrada.NORTE))
                        || (!this.entradas.contains(Entrada.SUR)
                                && !carta.getEntradas().contains(Entrada.NORTE));
            }
            if (this.getY() > carta.getY()) {
                return (this.entradas.contains(Entrada.NORTE)
                        && carta.getEntradas().contains(Entrada.SUR))
                        || (!this.entradas.contains(Entrada.NORTE)
                        && !carta.getEntradas().contains(Entrada.SUR));
            }
            if (this.getX() < carta.getX()) {
                return (this.entradas.contains(Entrada.ESTE)
                        && carta.getEntradas().contains(Entrada.OESTE))
                        || (!this.entradas.contains(Entrada.ESTE)
                                && !carta.getEntradas().contains(Entrada.OESTE));
            }
            if (this.getX() > carta.getX()) {
                return (this.entradas.contains(Entrada.OESTE)
                        && carta.getEntradas().contains(Entrada.ESTE))
                        || (!this.entradas.contains(Entrada.OESTE)
                                && !carta.getEntradas().contains(Entrada.ESTE));
            }
        }
        return false;
    }

    /**
     * Similar a this{@link #admiteConexion(CartaDeTunel)}, pero verifica específicamente
     * que se forme un tunel entre las dos cartas
     * 
     * @param carta carta a comparar
     * @return true si se forma un túnel entre las dos cartas
     */
    public boolean admiteConexionEstricta(CartaDeTunel carta) {
        if (!colisionaCon(carta)) {
            if (this.getY() < carta.getY()) {
                return this.entradas.contains(Entrada.SUR)
                        && carta.getEntradas().contains(Entrada.NORTE);
            }
            if (this.getY() > carta.getY()) {
                return this.entradas.contains(Entrada.NORTE)
                        && carta.getEntradas().contains(Entrada.SUR);
            }
            if (this.getX() < carta.getX()) {
                return this.entradas.contains(Entrada.ESTE)
                        && carta.getEntradas().contains(Entrada.OESTE);
            }
            if (this.getX() > carta.getX()) {
                return this.entradas.contains(Entrada.OESTE)
                        && carta.getEntradas().contains(Entrada.ESTE);
            }
        }
        return false;
    }

    public void inicializar() {
        if (this.tipo != TipoCartaTunel.INICIO && this.tipo != TipoCartaTunel.DESTINO_ORO
                && this.tipo != TipoCartaTunel.DESTINO_PIEDRA) {
            this.setPosicion(null, null);
        }
        this.setNorte(null);
        this.setSur(null);
        this.setEste(null);
        this.setOeste(null);
    }

    public boolean conectar(CartaDeTunel otraCarta) {
        if (otraCarta.getX() == this.getX() - 1 && otraCarta.getY() == this.getY()) {

            return  this.setOeste(otraCarta) && otraCarta.setEste(this);

        } else if (otraCarta.getX() == this.getX() + 1 && otraCarta.getY() == this.getY()) {

            return this.setEste(otraCarta) && otraCarta.setOeste(this);

        } if (otraCarta.getX() == this.getX() && otraCarta.getY() == this.getY() - 1) {

            return this.setNorte(otraCarta) && otraCarta.setSur(this);

        } else if (otraCarta.getX() == this.getX() && otraCarta.getY() == this.getY() + 1) {

            return this.setSur(otraCarta) && otraCarta.setNorte(this);
        }
        return false;
    }

    public boolean sonContiguas(CartaDeTunel carta) {
        return Math.abs(this.getX() - carta.getX()) == 1
                && Math.abs(this.getY() - carta.getY()) == 1;
    }
}
