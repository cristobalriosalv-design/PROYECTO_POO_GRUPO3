package co.edu.eia.ecocampus.app.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Controlador de navegacion entre las pantallas FXML (interfaz grafica opcional). */
public class SceneController {

    public void volverMenuPrincipal(ActionEvent event) throws IOException {
        cambiarEscena(event, "MainProyecto.fxml");
    }

    public void switchToCrearEcopuntos(ActionEvent event) throws IOException {
        cambiarEscena(event, "CrearEcopuntos.fxml");
    }

    private void cambiarEscena(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
