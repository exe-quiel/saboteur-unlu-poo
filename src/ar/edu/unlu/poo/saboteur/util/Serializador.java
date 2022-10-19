package ar.edu.unlu.poo.saboteur.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.edu.unlu.poo.saboteur.excepcion.DeserializationException;
import ar.edu.unlu.poo.saboteur.excepcion.SerializationException;

public class Serializador {

    private String archivo;

    public Serializador(String archivo) {
        super();
        this.archivo = archivo;
    }

    public void serializar(Object o) {
        serializar(o, false);
    }

    public void serializar(Object o, boolean append) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(archivo);
                ObjectOutputStream outputStream = append ? new AppendableObjectOutputStream(fileOutputStream)
                        : new ObjectOutputStream(fileOutputStream)) {
            outputStream.writeObject(o);
        } catch (FileNotFoundException e) {
            throw new SerializationException("Ocurrió un error al serializar el objeto", e);
        } catch (IOException e) {
            throw new SerializationException("Ocurrió un error al serializar el objeto", e);
        }
    }

    public void serializar(List<Object> list) {
        Iterator<Object> iterator = list.iterator();
        if (iterator.hasNext()) {
            serializar(iterator.next());
            try (ObjectOutputStream outputStream = new AppendableObjectOutputStream(
                    new FileOutputStream(archivo, true))) {
                while (iterator.hasNext()) {
                    outputStream.writeObject(iterator.next());
                }
                // No es necesario cerrar el stream si se usa try-with-resources
                // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
                // outputStream.close();
            } catch (IOException e) {
                throw new SerializationException("Ocurrió un error al serializar el objeto", e);
            }
        }
    }

    public Object deserializar() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(archivo))) {
            return inputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Ocurrió un error al deserializar el objeto", e);
        }
    }

    public <T> T deserializar(Class<T> clazz) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(archivo))) {
            Object o = inputStream.readObject();
            if (clazz.isInstance(o)) {
                return clazz.cast(o);
            }
            throw new ClassCastException("El objeto no es una instancia de esa clase");
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Ocurrió un error al deserializar el objeto", e);
        }
    }

    public List<Object> deserializarTodos() {
        List<Object> objetos = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(archivo))) {
            Object o = inputStream.readObject();
            while (o != null) {
                objetos.add(o);
                o = inputStream.readObject();
            }
        } catch (EOFException e) {
            // Terminó de leer el archivo
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Ocurrió un error al deserializar el objeto", e);
        }
        return objetos;
    }

    public <T> List<T> deserializarTodos(Class<T> clazz) {
        List<T> objetos = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(archivo))) {
            Object o = inputStream.readObject();
            while (o != null) {
                if (clazz.isInstance(o)) {
                    objetos.add(clazz.cast(o));
                    o = inputStream.readObject();
                } else {
                    throw new ClassCastException("El objeto no es una instancia de esa clase");
                }
            }
        } catch (EOFException e) {
            // Terminó de leer el archivo
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Ocurrió un error al deserializar el objeto", e);
        }
        return objetos;
    }

    private class AppendableObjectOutputStream extends ObjectOutputStream {

        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // Pass
        }
    }
}
