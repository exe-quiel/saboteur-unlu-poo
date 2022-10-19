package ar.edu.unlu.poo.saboteur.excepcion;

public class SerializationException extends RuntimeException {

	private static final long serialVersionUID = -3730738969354711907L;

	public SerializationException(String mensaje, Throwable excepcion) {
		super(mensaje, excepcion);
	}

	public SerializationException(Throwable excepcion) {
		super(excepcion);
	}
}
