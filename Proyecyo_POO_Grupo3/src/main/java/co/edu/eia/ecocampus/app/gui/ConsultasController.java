package co.edu.eia.ecocampus.app.gui;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.app.ContextoAplicacion;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.util.ColeccionesUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Controlador de Consultas.fxml: opciones 1 a 5 del submenu de consultas. */
public class ConsultasController {

    @FXML private TextArea txtPendientes;
    @FXML private TextField txtCodigoPunto;
    @FXML private TextArea txtPendientesPunto;
    @FXML private TextField txtIdPersona;
    @FXML private TextArea txtHistorial;
    @FXML private TextArea txtMateriales;
    @FXML private TextArea txtRanking;

    private final ContextoAplicacion ctx = ContextoGui.get();

    /** Las consultas que no piden datos se calculan al abrir la pantalla. */
    @FXML
    private void initialize() {
        // 1. Reportes pendientes por prioridad
        txtPendientes.setText(listar(ctx.getReportes().pendientesPorPrioridad()));

        // 4. Materiales recolectados (Map ordenado con un metodo generico)
        List<Map.Entry<TipoMaterial, Double>> materiales = ColeccionesUtil
                .ordenarPorValorDesc(ctx.getEcoPuntos().materialesRecolectados());
        txtMateriales.setText(listar(materiales.stream()
                .map(e -> String.format("%-11s %8.2f kg", e.getKey(), e.getValue())).toList()));

        // 5. Ranking de eco-puntos
        List<Persona> ranking = ctx.getEcoPuntos().ranking(ctx.getPersonas().listar());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranking.size(); i++) {
            Persona p = ranking.get(i);
            sb.append(String.format("%d. %-25s %5d pts%n", i + 1, p.getNombre(), p.getSaldoPuntos()));
        }
        txtRanking.setText(sb.length() == 0 ? "No se encontraron resultados." : sb.toString());
    }

    // 2. Reportes pendientes de un punto
    @FXML
    private void consultarPunto() {
        String codigo = txtCodigoPunto.getText().trim();
        txtPendientesPunto.setText(listar(ctx.getReportes().pendientesPorPunto(codigo)));
    }

    // 3. Historial de una persona
    @FXML
    private void consultarHistorial() {
        try {
            Persona persona = ctx.getPersonas().buscar(txtIdPersona.getText().trim());
            StringBuilder sb = new StringBuilder(persona.toString());
            sb.append("\n\n== Movimientos de eco-puntos ==\n").append(listar(persona.getHistorial()));
            sb.append("Total segun movimientos: ").append(ctx.getEcoPuntos().sumarPuntos(persona.getHistorial()))
                    .append(" pts\n");
            sb.append("\n== Reportes creados ==\n")
                    .append(listar(ctx.getReportes().reportesDePersona(persona.getId())));
            sb.append("\n== Notificaciones ==\n").append(listar(persona.getNotificaciones()));
            txtHistorial.setText(sb.toString());
        } catch (RecursoNoEncontradoException e) {
            txtHistorial.setText(e.getMessage());
        }
    }

    // 0. Volver
    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    /** Una linea por elemento; informa claramente cuando no hay resultados. */
    private String listar(Collection<?> elementos) {
        if (elementos.isEmpty()) {
            return "No se encontraron resultados.\n";
        }
        StringBuilder sb = new StringBuilder();
        for (Object elemento : elementos) {
            sb.append(elemento).append('\n');
        }
        return sb.toString();
    }
}
