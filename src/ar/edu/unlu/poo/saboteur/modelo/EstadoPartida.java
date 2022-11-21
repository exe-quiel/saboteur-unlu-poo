package ar.edu.unlu.poo.saboteur.modelo;

public enum EstadoPartida {

    RESULTADOS,
    TERCERA_RONDA(RESULTADOS),
    SEGUNDA_RONDA(TERCERA_RONDA),
    PRIMERA_RONDA(SEGUNDA_RONDA),
    LOBBY(PRIMERA_RONDA);

    private EstadoPartida siguienteEstado;

    private EstadoPartida() {
        this.siguienteEstado = null;
    }

    private EstadoPartida(EstadoPartida siguienteEstado) {
        this.siguienteEstado = siguienteEstado;
    }

    public EstadoPartida getSiguienteEstado() {
        return siguienteEstado;
    }
}
