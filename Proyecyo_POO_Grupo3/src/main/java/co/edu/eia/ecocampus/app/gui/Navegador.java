package co.edu.eia.ecocampus.app.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Cambia la pantalla de la ventana actual por otro archivo FXML. */
public final class Navegador {

    private Navegador() {
    }

    /**
     * @param event el clic del boton (de el se obtiene la ventana)
     * @param fxml  nombre del archivo, por ejemplo "MenuPrincipal.fxml"
     */
    public static void irA(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(Navegador.class.getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException | RuntimeException e) {
            Alertas.error("No se pudo abrir " + fxml, String.valueOf(e.getMessage()));
        }
    }
}