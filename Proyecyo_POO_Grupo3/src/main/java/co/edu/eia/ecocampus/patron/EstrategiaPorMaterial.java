package co.edu.eia.ecocampus.patron;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.TipoMaterial;

/** Puntos por kilogramo segun el tipo de material, multiplicados por el factor de la persona. */
public class EstrategiaPorMaterial implements EstrategiaCalculoPuntos {

    private final Map<TipoMaterial, Integer> puntosPorKg;

    public EstrategiaPorMaterial() {
        puntosPorKg = new EnumMap<>(TipoMaterial.class);
        puntosPorKg.put(TipoMaterial.ORGANICO, 5);
        puntosPorKg.put(TipoMaterial.RECICLABLE, 10);
        puntosPorKg.put(TipoMaterial.ESPECIAL, 20);
        puntosPorKg.put(TipoMaterial.OTRO, 2);
    }

    @Override
    public int calcular(Persona persona, List<? extends DetalleRecoleccion> detalles) {
        double total = 0;
        for (DetalleRecoleccion detalle : detalles) {
            total += detalle.getPesoEnKg() * puntosPorKg.getOrDefault(detalle.getMaterial(), 0);
        }
        return (int) Math.max(0, Math.round(total * persona.getFactorBonificacion()));
    }

    public int getPuntosPorKg(TipoMaterial material) {
        return puntosPorKg.getOrDefault(material, 0);
    }

    @Override
    public String getNombre() {
        return "Por tipo de material";
    }
}
