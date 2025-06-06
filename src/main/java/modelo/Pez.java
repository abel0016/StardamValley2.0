package modelo;

public class Pez {
    private String nombre;
    private int valorBase;
    private int tamanioMinimo;
    private int tamanioMaximo;
    private String tipoDeAguas;

    private int tamanioReal = -1;
    private int valorReal = -1;

    public Pez(String nombre, int valorBase, int tamanioMinimo, int tamanioMaximo, String tipoDeAguas) {
        this.nombre = nombre;
        this.valorBase = valorBase;
        this.tamanioMinimo = tamanioMinimo;
        this.tamanioMaximo = tamanioMaximo;
        this.tipoDeAguas = tipoDeAguas;
    }

    public void generarTamanioReal() {
        this.tamanioReal = tamanioMinimo + (int) (Math.random() * (tamanioMaximo - tamanioMinimo + 1));
        this.valorReal = calcularValorSegunTamanio();
    }

    private int calcularValorSegunTamanio() {
        // Proporcionalidad del tamaño respecto al valor base
        double proporcion = (double)(tamanioReal - tamanioMinimo) / (tamanioMaximo - tamanioMinimo);
        return valorBase + (int) (valorBase * proporcion * 0.5); // Hasta +50% si es del tamaño máximo
    }

    public String getNombre() {
        return nombre;
    }

    public int getValorBase() {
        return valorBase;
    }

    public int getTamanioMinimo() {
        return tamanioMinimo;
    }

    public int getTamanioMaximo() {
        return tamanioMaximo;
    }

    public String getTipoDeAguas() {
        return tipoDeAguas;
    }

    public int getTamanioReal() {
        return tamanioReal;
    }

    public int getValorReal() {
        return valorReal;
    }
    public void setTamanioReal(int tamanioReal) {
        this.tamanioReal = tamanioReal;
    }

    @Override
    public String toString() {
        if (tamanioReal != -1 && valorReal != -1) {
            return nombre + " (" + tamanioReal + " cm, " + valorReal + " monedas)";
        } else {
            return nombre + " (valor base: " + valorBase + ", tamaño: " + tamanioMinimo + "-" + tamanioMaximo + " cm)";
        }
    }
}
