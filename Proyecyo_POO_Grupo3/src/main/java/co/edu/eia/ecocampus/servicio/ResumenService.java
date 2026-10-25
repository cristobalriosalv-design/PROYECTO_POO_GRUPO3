package co.edu.eia.ecocampus.servicio;

import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.util.ColeccionesUtil;

/** Construye el resumen general en memoria (indicadores del sistema). */
public class ResumenService {

    private static final int TOP_RANKING = 5;

    private final PersonaService personas;
    private final PuntoEcologicoService puntos;
    private final ReporteService reportes;
    private final RutaService rutas;
    private final CampaniaService campanias;
    private final EcoPuntosService ecoPuntos;

    public ResumenService(PersonaService personas, PuntoEcologicoService puntos, ReporteService reportes,
            RutaService rutas, CampaniaService campanias, EcoPuntosService ecoPuntos) {
        this.personas = personas;
        this.puntos = puntos;
        this.reportes = reportes;
        this.rutas = rutas;
        this.campanias = campanias;
        this.ecoPuntos = ecoPuntos;
    }

    public String generarResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("================ RESUMEN ECOCAMPUS CIRCULAR ================\n");
        sb.append(String.format("Personas: %d | Puntos ecologicos: %d | Reportes: %d | Rutas: %d | Campanias: %d%n",
                personas.listar().size(), puntos.listar().size(), reportes.listar().size(), rutas.listar().size(),
                campanias.listar().size()));
        sb.append("Estrategia de eco-puntos: ").append(ecoPuntos.getEstrategiaActual().getNombre()).append('\n');

        sb.append("\n-- Reportes pendientes por prioridad --\n");
        List<ReporteResiduo> pendientes = reportes.pendientesPorPrioridad();
        if (pendientes.isEmpty()) {
            sb.append("  No hay reportes pendientes.\n");
        }
        pendientes.forEach(r -> sb.append("  ").append(r).append('\n'));

        sb.append("\n-- Rutas --\n");
        List<RutaRecoleccion> listaRutas = rutas.listar();
        if (listaRutas.isEmpty()) {
            sb.append("  No hay rutas registradas.\n");
        }
        listaRutas.forEach(r -> sb.append("  ").append(r).append('\n'));

        sb.append("\n-- Materiales recolectados (kg) --\n");
        List<Map.Entry<TipoMaterial, Double>> materiales = ColeccionesUtil
                .ordenarPorValorDesc(ecoPuntos.materialesRecolectados());
        if (materiales.isEmpty()) {
            sb.append("  Aun no se han registrado recolecciones.\n");
        }
        materiales.forEach(e -> sb.append(String.format("  %-11s %8.2f%n", e.getKey(), e.getValue())));

        sb.append("\n-- Campanias --\n");
        List<Campania> listaCampanias = campanias.listar();
        if (listaCampanias.isEmpty()) {
            sb.append("  No hay campanias registradas.\n");
        }
        listaCampanias.forEach(c -> sb.append("  ").append(c).append('\n'));

        sb.append("\n-- Ranking de eco-puntos (top ").append(TOP_RANKING).append(") --\n");
        List<Persona> ranking = ecoPuntos.ranking(personas.listar());
        for (int i = 0; i < Math.min(TOP_RANKING, ranking.size()); i++) {
            Persona p = ranking.get(i);
            sb.append(String.format("  %d. %-25s %5d pts%n", i + 1, p.getNombre(), p.getSaldoPuntos()));
        }
        sb.append("============================================================");
        return sb.toString();
    }
}
