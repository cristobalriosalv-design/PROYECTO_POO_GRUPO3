package co.edu.eia.ecocampus.app.gui;

import javafx.scene.control.Alert;

/** Ventanas de mensaje reutilizadas por todos los controladores. */
public final class Alertas {

    private Alertas() {
    }

    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle("EcoCampus Circular");
        alerta.setHeaderText(titulo);
        alerta.showAndWait();
    }
}