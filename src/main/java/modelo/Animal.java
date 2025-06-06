package modelo;

public abstract class Animal {
    protected String nombre;
    protected int diasDeVida;
    protected EstadoEdad estadoEdad;
    protected boolean haComidoHoy;
    protected int idAlimento;

    public enum EstadoEdad {
        CRIA,
        ADULTO,
        ANCIANO,
        VIEJO
    }

    public Animal(String nombre) {
        this.nombre = nombre;
        this.diasDeVida = 0;
        this.estadoEdad = EstadoEdad.CRIA;
        this.haComidoHoy = false;
    }

    public void avanzarDia() {
        diasDeVida++;
        actualizarEstadoDesdeEdad();
        haComidoHoy = false;
    }

    public void setEdad(int edad) {
        this.diasDeVida = edad;
        actualizarEstadoDesdeEdad();
    }

    public void actualizarEstadoDesdeEdad() {
        if (diasDeVida >= 21) {
            estadoEdad = EstadoEdad.VIEJO;
        } else if (diasDeVida >= 18) {
            estadoEdad = EstadoEdad.ANCIANO;
        } else if (diasDeVida >= 3) {
            estadoEdad = EstadoEdad.ADULTO;
        } else {
            estadoEdad = EstadoEdad.CRIA;
        }
    }

    public boolean estaMuerto() {
        return estadoEdad == EstadoEdad.VIEJO;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoEdad getEstadoEdad() {
        return estadoEdad;
    }

    public void setEstadoEdad(EstadoEdad estadoEdad) {
        this.estadoEdad = estadoEdad;
    }

    public boolean puedeProducir() {
        if (!haComidoHoy) return false;
        return switch (estadoEdad) {
            case ADULTO, ANCIANO -> true;
            default -> false;
        };
    }

    public String getTipo() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public int getEdad() {
        return this.diasDeVida;
    }

    public void setHaComido(boolean haComido) {
        this.haComidoHoy = haComido;
    }

    public boolean isHaComido() {
        return this.haComidoHoy;
    }

    public void incrementarEdad() {
        diasDeVida++;
        actualizarEstadoDesdeEdad();
    }


    protected boolean haProducidoHoy;

    public void setHaProducidoHoy(boolean valor) {
        this.haProducidoHoy = valor;
    }

    public boolean getHaProducidoHoy() {
        return haProducidoHoy;
    }

    public int getIdAlimento() {
        return idAlimento;
    }

    public abstract void alimentar(String alimento);
    public abstract int producir();
}
