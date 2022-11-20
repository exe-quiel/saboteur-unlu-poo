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
    /**
     * Para ver si está conectada al inicio
     */
    private boolean yaRevisada;

    private CartaDeTunel norte;
    private CartaDeTunel sur;
    private CartaDeTunel este;
    private CartaDeTunel oeste;
    private boolean dadaVuelta;

    public CartaDeTunel(int id, TipoCartaTunel tipo, boolean sinSalida, List<Entrada> entradas) {
        super(id);
        this.tipo = tipo;
        this.sinSalida = sinSalida;
        this.entradas = entradas;
        this.yaRevisada = false;
        this.dadaVuelta = false;
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
            return true;
        }
        return false;
    }

    /**
     * Valida que la carta esté conectada con la carta pasada por parámetro.
     * 
     * Se puede usar para validar que las cartas estén conectadas al inicio al intentar conectar una nueva
     * y también para evaluar si se se logró unir el inicio con alguna carta de destino
     * 
     * @param carta carta objetivo
     * @return true si hay conexión, si no, false
     */
    public boolean estaConectadaConCarta(CartaDeTunel carta) {
        System.out.println("Evaluando conexión entre [" + this.getId() + "] y [" + carta.getId() + "]");
        if (this.yaRevisada) {
            System.out.println(carta.getId() + " ya revisada");
            return false;
        } else {
            this.yaRevisada = true;
        }

        if (this == carta) {
            System.out.println(this.getId() + " es la carta objetivo");
            return true;
        }

        if (this.sinSalida) {
            return false;
        }

        boolean estaConectada = false;

        if (norte != null && norte.estaConectadaConCarta(carta)) {
            estaConectada = true;
        }

        if (!estaConectada && sur != null && sur.estaConectadaConCarta(carta)) {
            estaConectada = true;
        }

        if (!estaConectada && este != null && este.estaConectadaConCarta(carta)) {
            estaConectada = true;
        }

        if (!estaConectada && oeste != null && oeste.estaConectadaConCarta(carta)) {
            estaConectada = true;
        }

        System.out.println(this.getId() + " está conectada? " + estaConectada);
        return estaConectada;
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

        this.setPosicion(null, null);
    }

    public boolean colisionaCon(CartaDeJuego carta) {
        return this.getX() == carta.getX() && this.getY() == carta.getY();
    }

    /**
     * Valida que las cartas sean compatibles en cuando a sus entradas.
     * 
     * Acepta los casos en los que la cartas NO tienen entradas que las comuniquen entre sí, que es un caso válido.
     * 
     * @param carta carta a conectar
     * @return true si es válido, si no, false
     */
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
     * @param carta carta a conectar
     * @return true si se forma un túnel entre las dos cartas, si no, false
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
        if (otraCarta.getX().equals(this.getX() - 1) && otraCarta.getY().equals(this.getY())) {

            return  this.setOeste(otraCarta) && otraCarta.setEste(this);

        } else if (otraCarta.getX().equals(this.getX() + 1) && otraCarta.getY().equals(this.getY())) {

            return this.setEste(otraCarta) && otraCarta.setOeste(this);

        } if (otraCarta.getX().equals(this.getX()) && otraCarta.getY().equals(this.getY() - 1)) {

            return this.setNorte(otraCarta) && otraCarta.setSur(this);

        } else if (otraCarta.getX().equals(this.getX()) && otraCarta.getY().equals(this.getY() + 1)) {

            return this.setSur(otraCarta) && otraCarta.setNorte(this);
        }
        return false;
    }

    public boolean sonContiguas(CartaDeTunel carta) {
        return (Math.abs(this.getX() - carta.getX()) == 1 && this.getY().equals(carta.getY()))
                || (Math.abs(this.getY() - carta.getY()) == 1 && this.getX().equals(carta.getX()));
    }

    public void setYaRevisada(boolean yaRevisada) {
        this.yaRevisada = yaRevisada;
    }

    public boolean estaDadaVuelta() {
        return dadaVuelta;
    }

    public void setDadaVuelta(boolean dadaVuelta) {
        this.dadaVuelta = dadaVuelta;
    }
}
