package ar.edu.unlu.poo.saboteur.util;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;

public final class GUIHelper {

    public static String obtenerRepresentacionGraficaCarta(CartaDeJuego carta) {
        if (carta == null) {
            return "X";
        }

        StringBuilder sb = new StringBuilder("<html><pre>");
        if (carta instanceof CartaDeAccion) {
            sb.append(carta.getId() + "<br>");
            CartaDeAccion c = (CartaDeAccion) carta;
            sb.append(c.getTipos()
                    .stream()
                    .map(TipoCartaAccion::toString)
                    .collect(Collectors.joining("<br>")));
        } else if (carta instanceof CartaDeTunel) {
            CartaDeTunel cartaDeTunel = (CartaDeTunel) carta;
            if (!cartaDeTunel.isVisible()) {
                sb.append("    ?    ");
            } else if (cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_ORO) {
                sb.append(TipoCartaTunel.DESTINO_ORO);
            } else if (cartaDeTunel.getTipo() == TipoCartaTunel.DESTINO_PIEDRA) {
                sb.append(TipoCartaTunel.DESTINO_PIEDRA);
            } else {
                sb.append(carta.getId());
                List<Entrada> entradas = cartaDeTunel.getEntradas();
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
                if (cartaDeTunel.isSinSalida()) {
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
}
