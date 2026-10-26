package co.edu.eia.ecocampus.app.gui;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.app.ContextoAplicacion;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.util.ColeccionesUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/** Controlador de ResumenGeneral.fxml: la opcion 7 del menu, dividida en secciones. */
public class ResumenController {

    private static final int TOP_RANKING = 5;

    @FXML private TextArea txtTotales;
    @FXML private TextArea txtPendientes;
    @FXML private TextArea txtRutas;
    @FXML private TextArea txtMateriales;
    @FXML private TextArea txtCampanias;
    @FXML private TextArea txtRanking;

    private final ContextoAplicacion ctx = ContextoGui.get();

    @FXML
    private void initialize() {
        txtTotales.setText(String.format(
                "Personas: %d | Puntos ecologicos: %d | Reportes: %d | Rutas: %d | Campanias: %d%n"
                        + "Estrategia de eco-puntos: %s",
                ctx.getPersonas().listar().size(), ctx.getPuntos().listar().size(),
                ctx.getReportes().listar().size(), ctx.getRutas().listar().size(),
                ctx.getCampanias().listar().size(), ctx.getEcoPuntos().getEstrategiaActual().getNombre()));
        txtPendientes.setText(listar(ctx.getReportes().pendientesPorPrioridad(), "No hay reportes pendientes."));
        txtRutas.setText(listar(ctx.getRutas().listar(), "No hay rutas registradas."));

        List<Map.Entry<TipoMaterial, Double>> materiales = ColeccionesUtil
                .ordenarPorValorDesc(ctx.getEcoPuntos().materialesRecolectados());
        txtMateriales.setText(listar(materiales.stream()
                .map(e -> String.format("%-11s %8.2f", e.getKey(), e.getValue())).toList(),
                "Aun no se han registrado recolecciones."));

        txtCampanias.setText(listar(ctx.getCampanias().listar(), "No hay campanias registradas."));

        List<Persona> ranking = ctx.getEcoPuntos().ranking(ctx.getPersonas().listar());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(TOP_RANKING, ranking.size()); i++) {
            Persona p = ranking.get(i);
            sb.append(String.format("%d. %-25s %5d pts%n", i + 1, p.getNombre(), p.getSaldoPuntos()));
        }
        txtRanking.setText(sb.toString());
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    private String listar(Collection<?> elementos, String siVacio) {
        if (elementos.isEmpty()) {
            return siVacio;
        }
        StringBuilder sb = new StringBuilder();
        for (Object elemento : elementos) {
            sb.append(elemento).append('\n');
        }
        return sb.toString();
    }
}
