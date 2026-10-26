package co.edu.eia.ecocampus.app.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/** Controlador de MenuPrincipal.fxml: cada boton abre la pantalla de una opcion del menu. */
public class MenuPrincipalController {

    @FXML
    private void abrirPersonas(ActionEvent event) {
        Navegador.irA(event, "PersonasPuntos.fxml");
    }

    @FXML
    private void abrirReportes(ActionEvent event) {
        Navegador.irA(event, "Reportes.fxml");
    }

    @FXML
    private void abrirRutas(ActionEvent event) {
        Navegador.irA(event, "RutasDeRecoleccion.fxml");
    }

    @FXML
    private void abrirCampanias(ActionEvent event) {
        Navegador.irA(event, "CampaniasAmbientales.fxml");
    }

    @FXML
    private void abrirConsultas(ActionEvent event) {
        Navegador.irA(event, "Consultas.fxml");
    }

    @FXML
    private void abrirEstrategia(ActionEvent event) {
        Navegador.irA(event, "EstrategiasDeEcopuntos.fxml");
    }

    @FXML
    private void abrirResumen(ActionEvent event) {
        Navegador.irA(event, "ResumenGeneral.fxml");
    }

    @FXML
    private void abrirCasosInvalidos(ActionEvent event) {
        Navegador.irA(event, "DemostrarCasosInvalidos.fxml");
    }
}
