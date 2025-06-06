package modelo;

import app.GameContext;

public class Cerdo extends Animal {

    private final String alimentoRequerido = "Maiz";
    private final int diasParaTrufa = 2;
    private int diasDesdeUltimaProduccion = 0;

    public Cerdo(String nombre) {
        super(nombre);
        this.idAlimento = 1; // Maíz
        setEstadoEdad(EstadoEdad.CRIA);
    }

    @Override
    public void alimentar(String alimento) {
        haComidoHoy = alimento.equalsIgnoreCase(alimentoRequerido);
    }

    @Override
    public int producir() {
        if (!puedeProducir()) return 0;
        if (estadoEdad == EstadoEdad.CRIA) return 0;

        Estacion estacion = GameContext.getGranja().getEstacion();

        return switch (estacion) {
            case INVIERNO -> 0;
            case PRIMAVERA, OTONIO -> 2;
            case VERANO -> 1;
        };
    }


    @Override
    public String toString() {
        return "Cerdo{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + estadoEdad +
                ", haComidoHoy=" + haComidoHoy +
                '}';
    }
}
