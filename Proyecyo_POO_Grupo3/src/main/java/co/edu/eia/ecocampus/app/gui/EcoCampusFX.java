package co.edu.eia.ecocampus.app.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Interfaz grafica opcional. La aplicacion evaluada es la de consola
 * ({@code co.edu.eia.ecocampus.app.Main}); esta clase se ejecuta con
 * {@code mvn javafx:run} o desde Eclipse con {@link LanzadorGui}.
 */
public class EcoCampusFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MenuPrincipal.fxml"));
       
    	Image icon = new Image(getClass().getResourceAsStream("epiP.jpeg"));
        
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("EcoCampus Circular");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
