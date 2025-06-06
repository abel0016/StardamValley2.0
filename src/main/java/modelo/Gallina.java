package modelo;

public class Gallina extends Animal {

    private final String alimentoRequerido = "Maiz";
    private final int cantidadHuevos = 2;

    public Gallina(String nombre) {
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

        if (estadoEdad == EstadoEdad.ANCIANO) {
            return cantidadHuevos / 2;
        }

        return cantidadHuevos;
    }

    @Override
    public String toString() {
        return "Gallina{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + estadoEdad +
                ", haComidoHoy=" + haComidoHoy +
                '}';
    }
}
