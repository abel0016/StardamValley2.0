package modelo;

public class Oveja extends Animal {

    private final String alimentoRequerido = "Avena";
    private final int lanaPorProduccion = 5;
    private int diasDesdeUltimaProduccion = 0;

    public Oveja(String nombre) {
        super(nombre);
        this.idAlimento = 2; // Avena
        setEstadoEdad(EstadoEdad.CRIA);
    }

    @Override
    public void alimentar(String alimento) {
        haComidoHoy = alimento.equalsIgnoreCase(alimentoRequerido);
    }

    @Override
    public int producir() {
        if (!puedeProducir()) return 0;

        if (estadoEdad == EstadoEdad.ANCIANO) {
            return lanaPorProduccion / 2;
        }

        return lanaPorProduccion;
    }

    @Override
    public String toString() {
        return "Oveja{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + estadoEdad +
                ", haComidoHoy=" + haComidoHoy +
                '}';
    }
}
