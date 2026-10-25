package co.edu.eia.ecocampus.patron;

import java.util.List;

import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.util.Validador;

/** Tarifa plana: los mismos puntos por kilogramo sin importar el material. */
public class EstrategiaPorPesoTotal implements EstrategiaCalculoPuntos {

    private final int puntosPorKg;

    public EstrategiaPorPesoTotal(int puntosPorKg) {
        this.puntosPorKg = Validador.validarPositivo(puntosPorKg, "puntosPorKg");
    }

    @Override
    public int calcular(Persona persona, List<? extends DetalleRecoleccion> detalles) {
        double kg = 0;
        for (DetalleRecoleccion detalle : detalles) {
            kg += detalle.getPesoEnKg();
        }
        return (int) Math.max(0, Math.round(kg * puntosPorKg * persona.getFactorBonificacion()));
    }

    @Override
    public String getNombre() {
        return "Peso total (" + puntosPorKg + " pts/kg)";
    }
}
