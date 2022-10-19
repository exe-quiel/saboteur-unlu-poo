package ar.edu.unlu.poo.saboteur.excepcion;

public class DeserializationException extends RuntimeException {

	private static final long serialVersionUID = -3730738969354711907L;

	public DeserializationException(String mensaje, Throwable excepcion) {
		super(mensaje, excepcion);
	}

	public DeserializationException(Throwable excepcion) {
		super(excepcion);
	}
}
