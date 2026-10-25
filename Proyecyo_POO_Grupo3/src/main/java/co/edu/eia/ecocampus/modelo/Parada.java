package co.edu.eia.ecocampus.modelo;

import java.util.Objects;

import co.edu.eia.ecocampus.util.Validador;

/** Parte de una ruta: solo la crea RutaRecoleccion (constructor de paquete). */
public class Parada {

    private int orden;
    private final PuntoEcologico punto;
    private final String accionEsperada;
    private boolean completada;

    Parada(int orden, PuntoEcologico punto, String accionEsperada) {
        this.orden = orden;
        this.punto = Objects.requireNonNull(punto, "punto");
        this.accionEsperada = Validador.validarTexto(accionEsperada, "accionEsperada");
        this.completada = false;
    }

    public void marcarCompletada() {
        completada = true;
    }

    void setOrden(int orden) {
        this.orden = orden;
    }

    public int getOrden() {
        return orden;
    }

    public PuntoEcologico getPunto() {
        return punto;
    }

    public String getAccionEsperada() {
        return accionEsperada;
    }

    public boolean isCompletada() {
        return completada;
    }

    @Override
    public String toString() {
        return String.format("%d. %s -> %s%s", orden, punto.getId(), accionEsperada, completada ? " (completada)" : "");
    }
}
