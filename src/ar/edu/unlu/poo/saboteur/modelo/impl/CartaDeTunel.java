package ar.edu.unlu.poo.saboteur.modelo.impl;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;

public class CartaDeTunel implements CartaDeJuego {

    private TipoCartaTunel tipo;
    private boolean sinSalida;
    private List<Entrada> entradas;

    private byte x;
    private byte y;

    private CartaDeTunel cartaAlNorte;
    private CartaDeTunel cartaAlSur;
    private CartaDeTunel cartaAlEste;
    private CartaDeTunel cartaAlOeste;

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

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public CartaDeTunel getCartaAlNorte() {
        return cartaAlNorte;
    }

    public boolean setCartaAlNorte(CartaDeTunel cartaAlNorte) {
        if (this.entradas.contains(Entrada.NORTE)) {
            this.cartaAlNorte = cartaAlNorte;
            return true;
        }
        return false;
    }

    public CartaDeTunel getCartaAlSur() {
        return cartaAlSur;
    }

    public boolean setCartaAlSur(CartaDeTunel cartaAlSur) {
        if (this.entradas.contains(Entrada.SUR)) {
            this.cartaAlSur = cartaAlSur;
            return true;
        }
        return false;
    }

    public CartaDeTunel getCartaAlEste() {
        return cartaAlEste;
    }

    public boolean setCartaAlEste(CartaDeTunel cartaAlEste) {
        if (this.entradas.contains(Entrada.ESTE)) {
            this.cartaAlEste = cartaAlEste;
            return true;
        }
        return false;
    }

    public CartaDeTunel getCartaAlOeste() {
        return cartaAlOeste;
    }

    public boolean setCartaAlOeste(CartaDeTunel cartaAlOeste) {
        if (this.entradas.contains(Entrada.OESTE)) {
            this.cartaAlOeste = cartaAlOeste;
            return true;
        }
        return false;
    }

    public boolean estaConectadaAlInicio() {
        return verificarCartaConectadaAlInicio(cartaAlNorte)
                || verificarCartaConectadaAlInicio(cartaAlSur)
                || verificarCartaConectadaAlInicio(cartaAlEste)
                || verificarCartaConectadaAlInicio(cartaAlOeste);
    }

    private boolean verificarCartaConectadaAlInicio(CartaDeTunel carta) {
        if (carta == null) {
            return false;
        }
        return carta.getTipo() == TipoCartaTunel.INICIO || carta.estaConectadaAlInicio();
    }

    public boolean estaConectadaAlOro() {
        return verificarCartaConectadaAlOro(cartaAlNorte)
                || verificarCartaConectadaAlOro(cartaAlSur)
                || verificarCartaConectadaAlOro(cartaAlEste)
                || verificarCartaConectadaAlOro(cartaAlOeste);
    }

    private boolean verificarCartaConectadaAlOro(CartaDeTunel carta) {
        if (carta == null) {
            return false;
        }
        return carta.getTipo() == TipoCartaTunel.DESTINO_ORO || carta.estaConectadaAlOro();
    }

    public boolean colocarCartaEnElTablero(byte x, byte y, List<CartaDeTunel> cartasEnElTablero) {
        this.x = x;
        this.y = y;

        // Conectar la carta con las contiguas
        for (CartaDeTunel otraCarta : cartasEnElTablero) {
            boolean movimientoValido = true;

            if (otraCarta.getX() == this.x - 1 && otraCarta.getY() == this.y) {

                movimientoValido = this.setCartaAlOeste(otraCarta) && otraCarta.setCartaAlEste(this);

            } else if (otraCarta.getX() == this.x + 1 && otraCarta.getY() == this.y) {

                movimientoValido =this.setCartaAlEste(otraCarta) && otraCarta.setCartaAlOeste(this);

            } if (otraCarta.getX() == this.x && otraCarta.getY() == this.y - 1) {

                movimientoValido = this.setCartaAlNorte(otraCarta) && otraCarta.setCartaAlSur(this);

            } else if (otraCarta.getX() == this.x && otraCarta.getY() == this.y + 1) {

                movimientoValido = this.setCartaAlSur(otraCarta) && otraCarta.setCartaAlNorte(this);
            }

            if (!movimientoValido) {
                return false;
            }
        }

        return true;
    }
}
