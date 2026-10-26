package co.edu.eia.ecocampus.app.gui;

import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.patron.EstrategiaBonificacionCampania;
import co.edu.eia.ecocampus.patron.EstrategiaCalculoPuntos;
import co.edu.eia.ecocampus.patron.EstrategiaPorMaterial;
import co.edu.eia.ecocampus.patron.EstrategiaPorPesoTotal;
import co.edu.eia.ecocampus.servicio.CampaniaService;
import co.edu.eia.ecocampus.servicio.EcoPuntosService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Controlador de EstrategiasDeEcopuntos.fxml: cambia la Strategy de calculo de eco-puntos. */
public class EstrategiasController {

    @FXML private TextArea txtActualMaterial;
    @FXML private TextArea txtActualPeso;
    @FXML private TextField txtPuntosKg;
    @FXML private TextArea txtActualBono;
    @FXML private TextField txtCampaniaBono;
    @FXML private TextField txtMultiplicador;

    private final EcoPuntosService ecoPuntos = ContextoGui.get().getEcoPuntos();
    private final CampaniaService campanias = ContextoGui.get().getCampanias();

    @FXML
    private void initialize() {
        mostrarActual();
    }

    @FXML
    private void usarPorMaterial() {
        aplicar(new EstrategiaPorMaterial());
    }

    @FXML
    private void usarPorPeso() {
        try {
            aplicar(new EstrategiaPorPesoTotal(Integer.parseInt(txtPuntosKg.getText().trim())));
        } catch (NumberFormatException e) {
            Alertas.error("Dato invalido", "Los puntos por kg deben ser un numero entero.");
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        }
    }

    @FXML
    private void usarBonificacion() {
        try {
            Campania campania = campanias.buscar(txtCampaniaBono.getText().trim());
            double multiplicador = Double.parseDouble(txtMultiplicador.getText().trim().replace(',', '.'));
            aplicar(new EstrategiaBonificacionCampania(new EstrategiaPorMaterial(), campania, multiplicador));
        } catch (RecursoNoEncontradoException e) {
            Alertas.error("Campania no encontrada", e.getMessage());
        } catch (NumberFormatException e) {
            Alertas.error("Dato invalido", "El multiplicador debe ser un numero, por ejemplo 1.5");
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        }
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    /** Polimorfismo: cualquier implementacion de la interfaz sirve. */
    private void aplicar(EstrategiaCalculoPuntos estrategia) {
        ecoPuntos.cambiarEstrategia(estrategia);
        mostrarActual();
        Alertas.info("Estrategia actualizada", "Ahora se usa: " + estrategia.getNombre());
    }

    private void mostrarActual() {
        String nombre = ecoPuntos.getEstrategiaActual().getNombre();
        txtActualMaterial.setText(nombre);
        txtActualPeso.setText(nombre);
        txtActualBono.setText(nombre);
    }
}
