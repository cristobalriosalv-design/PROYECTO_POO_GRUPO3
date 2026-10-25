package co.edu.eia.ecocampus.patron;

import java.util.List;

import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.Persona;

/**
 * Strategy: politica intercambiable de calculo de eco-puntos. Cambiar la
 * politica no obliga a modificar Persona, ReporteResiduo ni Recoleccion.
 */
public interface EstrategiaCalculoPuntos {

    /** Los detalles solo se leen (productor), por eso {@code ? extends DetalleRecoleccion}. */
    int calcular(Persona persona, List<? extends DetalleRecoleccion> detalles);

    String getNombre();
}
