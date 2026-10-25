package co.edu.eia.ecocampus.util;

import java.util.regex.Pattern;

/**
 * Validaciones de datos de entrada reutilizadas por los constructores del
 * modelo. Lanza {@link IllegalArgumentException} porque un dato mal formado
 * es un error de programacion o de captura, no una regla de negocio.
 */
public final class Validador {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validador() {
    }

    public static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
        return valor.trim();
    }

    public static String validarCorreo(String correo) {
        String limpio = validarTexto(correo, "correo");
        if (!CORREO.matcher(limpio).matches()) {
            throw new IllegalArgumentException("Correo invalido: " + correo);
        }
        return limpio.toLowerCase();
    }

    public static double validarPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser mayor que cero");
        }
        return valor;
    }

    public static int validarPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser mayor que cero");
        }
        return valor;
    }
}
