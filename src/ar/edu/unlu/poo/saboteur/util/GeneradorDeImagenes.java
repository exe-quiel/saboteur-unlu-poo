package ar.edu.unlu.poo.saboteur.util;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ar.edu.unlu.poo.saboteur.modelo.Entrada;

public class GeneradorDeImagenes {

    final static Path ARCHIVO_SECCIONES = Paths.get("Saboteur_cartas.png");

    private static GeneradorDeImagenes instance;

    private final byte FACTOR_DE_ESCALA = 3;

    private BufferedImage imagenSecciones;
    private BufferedImage bloqueAbiertoVertical;
    private BufferedImage bloqueAbiertoHorizontal;
    private BufferedImage bloqueAbierto;
    private BufferedImage bloqueCerrado;

    private GeneradorDeImagenes() {
        try {
            imagenSecciones = ImageIO.read(ARCHIVO_SECCIONES.toFile());

            // https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(3, 3);
            AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            bloqueAbiertoVertical = affineTransformOp.filter(imagenSecciones.getSubimage(0, 0, 11, 16), null);
            bloqueAbiertoHorizontal = affineTransformOp.filter(imagenSecciones.getSubimage(11, 0, 11, 16), null);
            bloqueAbierto = affineTransformOp.filter(imagenSecciones.getSubimage(22, 0, 11, 16), null);
            bloqueCerrado = affineTransformOp.filter(imagenSecciones.getSubimage(33, 0, 11, 16), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GeneradorDeImagenes getInstance() {
        if (instance == null) {
            instance = new GeneradorDeImagenes();
        }
        return instance;
    }

    /**
     * Genera las imágenes de las cartas de túnel a partir de bloques predeterminados,
     * dependiendo de las características de la carta.
     * 
     * <p>
     * Las secciones de las esquinas (1, 3, 7 y 9) son siempre un bloque de piedra porque no hay entradas ni salidas ahí.
     * 
     * <p>
     * La sección del centro (5) determina si la carta bloquea el paso o no en el medio.
     * 
     * <p>
     * Las secciones 2, 4, 6 y 8 representan NORTE, OESTE, ESTE y SUR en el enum {@link Entrada}.
     * 
     * <pre>
     *  -----------------------
     * |       |       |       |
     * |       |       |       |
     * |   1   |   2   |   3   |
     * |       |       |       |
     * |       |       |       |
     * |-------+-------+-------|
     * |       |       |       |
     * |       |       |       |
     * |   4   |   5   |   6   |
     * |       |       |       |
     * |       |       |       |
     * |-------+-------+-------|
     * |       |       |       |
     * |       |       |       |
     * |   7   |   8   |   9   |
     * |       |       |       |
     * |       |       |       |
     *  -----------------------
     * </pre>
     * 
     * @param entradas
     * @param centroBloqueado
     * @return
     */
    public BufferedImage generarImagen(Entrada[] entradas, boolean centroBloqueado) {
        BufferedImage imagenResultante = new BufferedImage(11 * 3 * FACTOR_DE_ESCALA, 16 * 3 * FACTOR_DE_ESCALA, imagenSecciones.getType());
        Graphics graphics = imagenResultante.getGraphics();

        // Esquina superior izquierda
        graphics.drawImage(bloqueCerrado, 0, 0, null);

        // Esquina superior derecha
        graphics.drawImage(bloqueCerrado, imagenResultante.getWidth() - (11 * FACTOR_DE_ESCALA), 0, null);

        // Esquina inferior izquierda
        graphics.drawImage(bloqueCerrado, 0, imagenResultante.getHeight() - (16 * FACTOR_DE_ESCALA), null);

        // Esquina inferior derecha
        graphics.drawImage(bloqueCerrado, imagenResultante.getWidth() - (11 * FACTOR_DE_ESCALA), imagenResultante.getHeight() - (16 * FACTOR_DE_ESCALA), null);

        List<Entrada> entradasLista = Arrays.asList(entradas);

        if (entradasLista.contains(Entrada.NORTE)) {
            graphics.drawImage(bloqueAbiertoVertical, 11 * FACTOR_DE_ESCALA, 0, null);
        } else {
            graphics.drawImage(bloqueCerrado, 11 * FACTOR_DE_ESCALA, 0, null);
        }

        if (entradasLista.contains(Entrada.SUR)) {
            graphics.drawImage(bloqueAbiertoVertical, 11 * FACTOR_DE_ESCALA, 16 * FACTOR_DE_ESCALA, null);
        } else {
            graphics.drawImage(bloqueCerrado, 11 * FACTOR_DE_ESCALA, 16 * FACTOR_DE_ESCALA, null);
        }

        if (entradasLista.contains(Entrada.ESTE)) {
            graphics.drawImage(bloqueAbiertoHorizontal, (33 - 11) * FACTOR_DE_ESCALA, 16 * FACTOR_DE_ESCALA, null);
        } else {
            graphics.drawImage(bloqueCerrado, (33 - 11) * FACTOR_DE_ESCALA, 16 * FACTOR_DE_ESCALA, null);
        }

        if (entradasLista.contains(Entrada.OESTE)) {
            graphics.drawImage(bloqueAbiertoHorizontal, 0, 16 * FACTOR_DE_ESCALA, null);
        } else {
            graphics.drawImage(bloqueCerrado, 0, 16 * FACTOR_DE_ESCALA, null);
        }

        if (centroBloqueado) {
            graphics.drawImage(bloqueCerrado, 11 * FACTOR_DE_ESCALA, 16 * FACTOR_DE_ESCALA, null);
        } else {
            graphics.drawImage(bloqueAbierto, 11 * FACTOR_DE_ESCALA, 16 * FACTOR_DE_ESCALA, null);
        }

        return imagenResultante;
    }
}
