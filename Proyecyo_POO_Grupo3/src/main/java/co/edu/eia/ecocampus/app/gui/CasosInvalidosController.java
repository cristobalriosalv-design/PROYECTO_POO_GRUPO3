package co.edu.eia.ecocampus.app.gui;

import co.edu.eia.ecocampus.app.DatosDemo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Controlador de DemostrarCasosInvalidos.fxml: ejecuta los mismos casos de la
 * opcion 8 de la consola y muestra con que excepcion se rechazo cada uno.
 */
public class CasosInvalidosController {

    @FXML private TextArea txtCasos;

    @FXML
    private void initialize() {
        ejecutarCasos();
    }

    @FXML
    private void ejecutarCasos() {
        txtCasos.setText(String.join("\n", DatosDemo.demostrarCasosInvalidos(ContextoGui.get())));
    }

    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }
}
