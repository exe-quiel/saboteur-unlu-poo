package ar.edu.unlu.poo.saboteur.modelo;

public enum RolJugador {

    SABOTEADOR("Saboteador"), BUSCADOR("Buscador");

    private String nombreUserFriendly;

    private RolJugador(String nombreUserFriendly) {
        this.nombreUserFriendly = nombreUserFriendly;
    }

    @Override
    public String toString() {
        return this.nombreUserFriendly;
    }
}
