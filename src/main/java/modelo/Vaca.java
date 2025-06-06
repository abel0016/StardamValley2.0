package modelo;

public class Vaca extends Animal {

    private final String alimentoRequerido = "Heno";
    private double peso;
    private int diasDesdeUltimaProduccion = 0;

    public Vaca(String nombre, double pesoInicial) {
        super(nombre);
        this.peso = pesoInicial;
        this.idAlimento = 3;
    }

    public Vaca(String nombre) {
        super(nombre);
        this.peso = 400.0;
        this.idAlimento = 3;
        setEstadoEdad(EstadoEdad.CRIA);
    }

    @Override
    public void alimentar(String alimento) {
        haComidoHoy = alimento.equalsIgnoreCase(alimentoRequerido);
    }

    @Override
    public int producir() {
        if (!puedeProducir()) return 0;

        diasDesdeUltimaProduccion++;
        if (diasDesdeUltimaProduccion < 1) return 0;

        diasDesdeUltimaProduccion = 0;
        int produccion = (int) Math.floor(peso * 0.01);
        if (estadoEdad == EstadoEdad.ANCIANO) {
            produccion = produccion / 2;
        }

        return produccion;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double nuevoPeso) {
        this.peso = nuevoPeso;
    }

    @Override
    public String toString() {
        return "Vaca{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + estadoEdad +
                ", peso=" + peso +
                ", haComidoHoy=" + haComidoHoy +
                '}';
    }
}
