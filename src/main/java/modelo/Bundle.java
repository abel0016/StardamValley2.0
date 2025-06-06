package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bundle {
    private String nombre;
    private String tipo;
    private Map<String, Boolean> elementosRequeridos = new HashMap<>();
    private boolean completado = false;

    public Bundle(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public void agregarElemento(String nombreElemento) {
        elementosRequeridos.put(nombreElemento, false);
    }

    public boolean entregar(String nombreElemento) {
        if (elementosRequeridos.containsKey(nombreElemento) && !elementosRequeridos.get(nombreElemento)) {
            elementosRequeridos.put(nombreElemento, true);
            comprobarCompletado();
            return true;
        }
        return false;
    }

    private void comprobarCompletado() {
        completado = elementosRequeridos.values().stream().allMatch(Boolean::booleanValue);
    }

    public boolean estaCompletado() {
        return completado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public Map<String, Boolean> getElementosRequeridos() {
        return elementosRequeridos;
    }
    public List<String> getElementosPendientes() {
        List<String> pendientes = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : elementosRequeridos.entrySet()) {
            if (!entry.getValue()) {
                pendientes.add(entry.getKey());
            }
        }
        return pendientes;
    }

}
