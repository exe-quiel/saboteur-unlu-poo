package ar.edu.unlu.poo.saboteur.util;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import ar.edu.unlu.poo.saboteur.modelo.CartaDeJuego;
import ar.edu.unlu.poo.saboteur.modelo.CartaDePuntos;
import ar.edu.unlu.poo.saboteur.modelo.Entrada;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaAccion;
import ar.edu.unlu.poo.saboteur.modelo.TipoCartaTunel;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeAccion;
import ar.edu.unlu.poo.saboteur.modelo.impl.CartaDeTunel;

public class CreadorDeCartas {

    public static void main(String[] args) {
        List<CartaDeJuego> cartasDeJuego = new ArrayList<>();
        List<CartaDePuntos> cartasDePuntos = new ArrayList<>();
        Path path = Paths.get("archivos");
        Arrays.asList(path.toFile().listFiles((file, name) -> name.endsWith(".csv")))
            .forEach(file -> {
                System.out.println(file.getName());
                Scanner fileScanner;
                try {
                    fileScanner = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                switch (file.getName()) {
                case "cartas_de_accion.csv":
                    cartasDeJuego.addAll(crearCartasDeAccion(fileScanner));
                    break;
                case "cartas_de_tunel.csv":
                    cartasDeJuego.addAll(crearCartasDeTunel(fileScanner));
                    break;
                case "cartas_de_pepitas_de_oro.csv":
                    cartasDePuntos.addAll(crearCartasDePuntos(fileScanner));
                    break;
                default:
                    break;
                }
                fileScanner.close();
            });
        Serializador serializadorCartasDeJuego = new Serializador("archivos/cartas_de_juego.dat");
        serializadorCartasDeJuego.serializar(cartasDeJuego);
        System.out.println("Archivo creado: cartas_de_juego.dat");

        Serializador serializadorCartasDePuntos = new Serializador("archivos/cartas_de_puntos.dat");
        serializadorCartasDePuntos.serializar(cartasDePuntos);
        System.out.println("Archivo creado: cartas_de_puntos.dat");
    }

    private static List<CartaDeJuego> crearCartasDeAccion(Scanner fileScanner) {
        List<CartaDeJuego> cartas = new ArrayList<>();
        // El primer renglón tiene los nombres de las columnas, así que lo salteo
        fileScanner.next();
        while (fileScanner.hasNext()) {
            String text = fileScanner.next();
            String[] valores = text.split(",");
            int id = Integer.valueOf(valores[0]);
            String[] tipos = valores[1].split("\\|");
            List<TipoCartaAccion> listaTipos = Arrays.stream(tipos).map(tipo -> TipoCartaAccion.valueOf(tipo)).collect(Collectors.toList());
            cartas.add(new CartaDeAccion(id, listaTipos));
        }
        return cartas;
    }

    private static Collection<? extends CartaDeJuego> crearCartasDeTunel(Scanner fileScanner) {
        List<CartaDeJuego> cartas = new ArrayList<>();
        // El primer renglón tiene los nombres de las columnas, así que lo salteo
        fileScanner.next();
        while (fileScanner.hasNext()) {
            String text = fileScanner.next();
            String[] valores = text.split(",");
            int id = Integer.valueOf(valores[0]);
            TipoCartaTunel tipo = TipoCartaTunel.valueOf(valores[1]);
            String entradas = valores[2];
            List<Entrada> listaEntradas = new ArrayList<>();
            if (entradas.contains("N")) {
                listaEntradas.add(Entrada.NORTE);
            }
            if (entradas.contains("S")) {
                listaEntradas.add(Entrada.SUR);
            }
            if (entradas.contains("E")) {
                listaEntradas.add(Entrada.ESTE);
            }
            if (entradas.contains("O")) {
                listaEntradas.add(Entrada.OESTE);
            }
            boolean sinSalida = Boolean.valueOf(valores[3]);
            CartaDeTunel cartaDeTunel = new CartaDeTunel(id, tipo, sinSalida, listaEntradas);
            cartaDeTunel.inicializar();
            if (tipo == TipoCartaTunel.INICIO) {
                cartaDeTunel.setPosicion(0, 2);
            } else if (tipo == TipoCartaTunel.DESTINO_ORO || tipo == TipoCartaTunel.DESTINO_PIEDRA) {
                cartaDeTunel.setVisible(false);
            }
            cartas.add(cartaDeTunel);
        }
        return cartas;
    }

    private static Collection<? extends CartaDePuntos> crearCartasDePuntos(Scanner fileScanner) {
        List<CartaDePuntos> cartas = new ArrayList<>();
        // El primer renglón tiene los nombres de las columnas, así que lo salteo
        fileScanner.next();
        while (fileScanner.hasNext()) {
            String text = fileScanner.next();
            String[] valores = text.split(",");
            int id = Integer.valueOf(valores[0]);
            int puntaje = Integer.valueOf(valores[1]);
            CartaDePuntos cartaDePuntos = new CartaDePuntos(id, puntaje);
            cartas.add(cartaDePuntos);
        }
        return cartas;
    }

}
