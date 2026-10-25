package co.edu.eia.ecocampus.app.gui;

import javafx.application.Application;

/**
 * Lanzador para Eclipse ("Run As > Java Application"). Una clase que no hereda
 * de Application evita el error "JavaFX runtime components are missing"
 * cuando JavaFX llega por el classpath de Maven y no por el module path.
 */
public class LanzadorGui {

    public static void main(String[] args) {
        Application.launch(EcoCampusFX.class, args);
    }
}
