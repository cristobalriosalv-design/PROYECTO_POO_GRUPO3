package co.edu.eia.ecocampus.app.gui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    /** Pregunta Aceptar/Cancelar y devuelve true solo si el usuario acepta. */
    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje);
        alerta.setTitle("EcoCampus Circular");
        alerta.setHeaderText(titulo);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle("EcoCampus Circular");
        alerta.setHeaderText(titulo);
        alerta.showAndWait();
    }
}