package ar.edu.unlu.poo.saboteur.modelo;

public enum TipoCartaTunel {

    INICIO("Inicio"), DESTINO_ORO("Oro"), DESTINO_PIEDRA("Piedra"), TUNEL("Túnel");

    private String nombreUserFriendly;

    private TipoCartaTunel(String nombreUserFriendly) {
        this.nombreUserFriendly = nombreUserFriendly;
    }

    @Override
    public String toString() {
        return this.nombreUserFriendly;
    }
}
