package co.edu.eia.ecocampus.patron;

import java.util.List;
import java.util.Objects;

import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.EstadoCampania;
import co.edu.eia.ecocampus.modelo.Persona;

/**
 * Regla de campania: envuelve otra estrategia y multiplica sus puntos cuando
 * la persona participa en una campania ACTIVA.
 */
public class EstrategiaBonificacionCampania implements EstrategiaCalculoPuntos {

    private final EstrategiaCalculoPuntos base;
    private final Campania campania;
    private final double multiplicador;

    public EstrategiaBonificacionCampania(EstrategiaCalculoPuntos base, Campania campania, double multiplicador) {
        if (multiplicador < 1) {
            throw new IllegalArgumentException("El multiplicador de campania debe ser mayor o igual a 1");
        }
        this.base = Objects.requireNonNull(base, "base");
        this.campania = Objects.requireNonNull(campania, "campania");
        this.multiplicador = multiplicador;
    }

    @Override
    public int calcular(Persona persona, List<? extends DetalleRecoleccion> detalles) {
        int puntosBase = base.calcular(persona, detalles);
        if (campania.getEstado() == EstadoCampania.ACTIVA && campania.esParticipante(persona)) {
            return (int) Math.round(puntosBase * multiplicador);
        }
        return puntosBase;
    }

    @Override
    public String getNombre() {
        return base.getNombre() + " x" + multiplicador + " en campania " + campania.getId();
    }
}
